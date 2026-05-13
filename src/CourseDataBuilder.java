package src;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Claude was used to help understand and impliment regex and json elements in this file
 * The conversation is here: https://claude.ai/share/530ba387-69ba-4b2a-a458-dba96aa8878d
 * Builds 2 csv files based on an inputted java file
 * @author Henry, Deniz, Tom, Phineus
 */
public class CourseDataBuilder {

    /** 
     * Parse a JSON file, returning a list of sections that map the key to the value found
     * @param path - the location of the file
     * @throws IOException if it can't find the value
     * @return the array of maps 
     */
    static List<Map<String, String>> parseJsonArray(Path path) throws IOException {
        String text = new String(Files.readAllBytes(path), "UTF-8");
        List<Map<String, String>> result = new ArrayList<>();
        for (String block : splitObjects(text)) {
            result.add(parseFlatObject(block));
        }
        return result;
    }

    /**
     * Parse a JSON array where elements may contain nested string arrays.
     * @param path - the path to the json file
     * @throws IOException - if it fails it will throw an error
     * @return the List value
     */
    static List<Map<String, Object>> parseJsonArrayRaw(Path path) throws IOException {
        String text = new String(Files.readAllBytes(path), "UTF-8");
        List<Map<String, Object>> result = new ArrayList<>();
        for (String block : splitObjects(text)) {
            result.add(parseObjectWithArrays(block));
        }
        return result;
    }

    /** 
     * Split JSON array strings into smaller sections that can be parsed for values at each point
     * @param json
     * @return the list of values
     */
    private static List<String> splitObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if      (c == '{') { if (depth++ == 0) start = i; }
            else if (c == '}') { if (--depth == 0 && start >= 0) { objects.add(json.substring(start, i + 1)); start = -1; } }
        }
        return objects;
    }

    /** 
     * Takes a block of values in the json file and returns a Map that maps the key to the value
     * @param block - the data as a string
     * @return a map value that maps the key to the value
     */
    private static Map<String, String> parseFlatObject(String block) {
        Map<String, String> map = new LinkedHashMap<>();
        // Matches:  "key" : "string value"   OR   "key" : number
        Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"((?:[^\"\\\\]|\\\\.)*)\"|(-?\\d+(?:\\.\\d+)?))");
        Matcher m = p.matcher(block);
        while (m.find()) {
            String key   = m.group(1);
            String value = m.group(2) != null ? m.group(2) : m.group(3);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Parse a JSON object that may contain nested string arrays.
     * Array values are extracted first (and stored as List<String>),
     * then the remaining scalar values are parsed normally.
     * @param block - the block in string form
     * @return the Map mapping the key to the value
     */
    private static Map<String, Object> parseObjectWithArrays(String block) {
        Map<String, Object> map = new LinkedHashMap<>();
        Pattern arrPat = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[([^\\]]*)\\]");
        Matcher arrMat = arrPat.matcher(block);
        StringBuffer sb = new StringBuffer();
        while (arrMat.find()) {
            String key      = arrMat.group(1);
            String contents = arrMat.group(2);
            List<String> items = new ArrayList<>();
            Matcher itemMat = Pattern.compile("\"([^\"]*)\"").matcher(contents);
            while (itemMat.find()) items.add(itemMat.group(1));
            map.put(key, items);
            arrMat.appendReplacement(sb, "");
        }
        arrMat.appendTail(sb);

        // Parse remaining scalar fields
        map.putAll(parseFlatObject(sb.toString()));
        return map;
    }

    /** 
     * Converts dates from -> to:
     * YYYYMMDD -> YYYY-MM-DD 
     * @param raw - the date in raw form YYYYMMDD
     * @return date in the form of YYYY-MM-DD
     */
    private static String formatDate(String raw) {
        if (raw == null || raw.length() != 8) {
            if (raw == null) {
                return "";
            }
            else {
                return raw;
            }
        }
        return raw.substring(0, 4) + "-" + raw.substring(4, 6) + "-" + raw.substring(6, 8);
    }

    /** 
     * Convert time from form of: 
     * "1315" -> "13:15"
     * handles 3- or 4-digit strings 
     * @param raw - time in raw form
     * @return the date in the converted format
     */
    private static String formatTime(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        try {
            String padded = String.format("%04d", Integer.parseInt(raw.trim()));
            return padded.substring(0, 2) + ":" + padded.substring(2, 4);
        } catch (NumberFormatException e) {
            return raw;
        }
    }

    /**
     * Convert the 7-char meeting-days to a readable string.
     * Position order: S M T W R F S  (Sun Mon Tue Wed Thu Fri Sat)
     * @param input the in letter form
     * @return the new format
     */
    private static String parseDays(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] labels = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Math.min(input.length(), labels.length); i++) {
            if (input.charAt(i) != '-') {
                if (out.length() > 0) out.append(";");
                out.append(labels[i]);
            }
        }
        return out.toString();
    }

    /** 
     * Expand single-character status codes to readable strings. 
     * @param code
     * @return the code expanded
     */
    private static String expandStatus(String code) {
        switch (code) {
            case "O": return "Open";
            case "C": return "Closed";
            case "W": return "Waitlisted";
            default:  return code;
        }
    }

    /** 
     * Safely get a string from an Object-valued map. Use this helper as we don't know if values mapped correctly from json
     */
    private static String strVal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? "" : v.toString();
    }

    /**
     * Wrap a value for CSV output.
     * Always quotes to safely handle commas, newlines, and special characters.
     * Internal double-quotes are escaped by doubling them per RFC 4180.
     * @param value - the string
     * @return a new string that is safe to put into a csv (escapes special characters)
     */
    private static String csv(String value) {
        if (value == null) {
            value = "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /** 
     * Create a UTF-8 PrintWriter for a given path. 
     * @param path - the path that we want to write to 
     * @throws IOException - if an error arrises
     * @return PrintWriter to use
     */
    private static PrintWriter utf8Writer(Path path) throws IOException {
        return new PrintWriter(new OutputStreamWriter(
            new FileOutputStream(path.toFile()), "UTF-8"
        ));
    }

    public static void main(String[] args) throws Exception {
        String dataDir = "data";
        Path dir = Paths.get(dataDir);
        System.out.println("Reading source files from: " + dir.toAbsolutePath());

        // 1. Read through the staff data and get the names mapped to their id
        Map<String, String> staffNames = new LinkedHashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("alt-staff.json"))) {
            String name = row.getOrDefault("altName", "").replace("\\,", ",");
            staffNames.put(row.getOrDefault("cxId", ""), name);
        }
        for (Map<String, String> row : parseJsonArray(dir.resolve("staff.json"))) {
            staffNames.put(row.getOrDefault("cxId", ""), row.getOrDefault("fullName", ""));
        }

        // 2. Calendar section times, gets start and end dates for a given semester id
        Map<String, String> sessionBegin = new LinkedHashMap<>();
        Map<String, String> sessionEnd   = new LinkedHashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("calendar-session.json"))) {
            String id = row.getOrDefault("externalId", "");
            sessionBegin.put(id, formatDate(row.getOrDefault("beginDate", "")));
            sessionEnd  .put(id, formatDate(row.getOrDefault("endDate",   "")));
        }

        // 3. Gets the start and end dates for a given section that has a id
        Map<String, String> sectionToSession = new LinkedHashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("calendar-session-section.json"))) {
            sectionToSession.put(
                row.getOrDefault("courseSectionId", ""),
                row.getOrDefault("calendarSessionExternalId", "")
            );
        }

        // 4. Gets the description of an area from the area code
        Map<String, String> areaDesc = new LinkedHashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("course-areas-description.json"))) {
            areaDesc.put(row.getOrDefault("area", ""), row.getOrDefault("description", ""));
        }

        // 5. For each section, gets all the areas that it meets
        Map<String, String> courseAreasStr = new LinkedHashMap<>();
        for (Map<String, Object> row : parseJsonArrayRaw(dir.resolve("course-area.json"))) {
            String code = strVal(row, "course_code");
            @SuppressWarnings("unchecked")
            List<String> areas = (List<String>) row.getOrDefault("course_areas", Collections.emptyList());
            List<String> formatted = new ArrayList<>();
            for (String a : areas) {
                formatted.add(a + ":" + areaDesc.getOrDefault(a, a));
            }
            courseAreasStr.put(code, String.join(";", formatted));
        }

        // 6. Gets the location, days, and time for each section
        Map<String, Map<String, String>> schedules = new LinkedHashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("course-section-schedule.json"))) {
            schedules.put(row.getOrDefault("courseSectionId", ""), row);
        }

        // 7. For each section, gets the section instructors
        Map<String, String> sectionInstructorStr = new LinkedHashMap<>();
        for (Map<String, Object> row : parseJsonArrayRaw(dir.resolve("section-instructor.json"))) {
            String sectionId = strVal(row, "courseSectionId");
            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) row.getOrDefault("staffExternalIds", Collections.emptyList());
            List<String> names = new ArrayList<>();
            for (String id : ids) {
                names.add(staffNames.getOrDefault(id, "Unknown(" + id + ")"));
            }
            sectionInstructorStr.put(sectionId, String.join(";", names));
        }

        // 8. for each section, counts the perms
        Map<String, String> permCounts = new LinkedHashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("perm-count.json"))) {
            permCounts.put(row.getOrDefault("courseSectionId", ""), row.getOrDefault("permCount", "0"));
        }

        // 9. Seperates out the code, section number, and semester id for each seciton
        Pattern sectionPattern = Pattern.compile("^(.+)-(\\d+)\\s+(\\S+)$");
        List<Map<String, String>> allSections = parseJsonArray(dir.resolve("course-section.json"));
        Map<String, List<Map<String, String>>> courseToSections = new LinkedHashMap<>();
        for (Map<String, String> sec : allSections) {
            String sectionId = sec.getOrDefault("courseSectionId", "");
            Matcher m = sectionPattern.matcher(sectionId);
            if (!m.matches()) {
                continue;
            }
            String courseCode = m.group(1);
            courseToSections.computeIfAbsent(courseCode, k -> new ArrayList<>()).add(sec);
        }

        // 10. Parse course.txt
        //  Records are separated by "|#|"
        //  Fields within a record are separated by "|^|"
        //  Field order: COURSE_CODE | NAME | AREA_CODE | CIP_CODE | COLLEGE | DESCRIPTION
        String courseTxt = new String(Files.readAllBytes(dir.resolve("course.txt")), "UTF-8");
        String[] rawRecords = courseTxt.split("\\|#\\|");

        // courseCode -> String[] { courseName, areaCode, cipCode, college, description }
        Map<String, String[]> courseInfo = new LinkedHashMap<>();
        for (String record : rawRecords) {
            record = record.trim();
            if (record.startsWith("DBSTART") || record.isEmpty()) continue;
            String[] f = record.split("\\|\\^\\|", -1);
            if (f.length < 5) continue;
            String code = f[0].trim();
            courseInfo.putIfAbsent(code, new String[]{
                f[1].trim(),                        // [0] courseName
                f[2].trim(),                        // [1] areaCode
                f[3].trim(),                        // [2] cipCode
                f[4].trim(),                        // [3] college
                f.length > 5 ? f[5].trim() : ""    // [4] description
            });
        }
        System.out.printf("Parsed %d unique courses from course.txt%n", courseInfo.size());

        // 11. Write courses.csv
        Path coursesOut = dir.resolve("courses.csv");
        try (PrintWriter pw = utf8Writer(coursesOut)) {
            pw.println("courseCode,courseName,college,areaCode,cipCode,courseAreas,description");
            for (Map.Entry<String, String[]> e : courseInfo.entrySet()) {
                String code  = e.getKey();
                String[] inf = e.getValue();
                pw.println(
                    csv(code) + "," +
                    csv(inf[0]) + "," + // courseName
                    csv(inf[3]) + "," + // college
                    csv(inf[1]) + "," + // areaCode
                    csv(inf[2]) + "," + // cipCode
                    csv(courseAreasStr.getOrDefault(code, "")) + "," +
                    csv(inf[4])  // description
                );
            }
        }
        System.out.println("Wrote " + courseInfo.size() + " rows -> " + coursesOut.toAbsolutePath());

        // 12. Write sections.csv
        Path sectionsOut = dir.resolve("sections.csv");
        int sectionRowCount = 0;
        try (PrintWriter pw = utf8Writer(sectionsOut)) {
            pw.println(
                "courseCode,sectionId,sectionNumber,semesterId,sessionId," +
                "beginDate,endDate,creditHours,capacity,currentEnrollment," +
                "status,permCount,meetingDays,meetingDaysRaw,startTime,endTime," +
                "room,instructors"
            );
            for (Map.Entry<String, List<Map<String, String>>> entry : courseToSections.entrySet()) {
                String courseCode = entry.getKey();
                for (Map<String, String> sec : entry.getValue()) {
                    String sectionId = sec.getOrDefault("courseSectionId", "");
                    Matcher m = sectionPattern.matcher(sectionId);
                    String sectionNum = "", semesterId = "";
                    if (m.matches()) {
                        sectionNum  = m.group(2);
                        semesterId  = m.group(3);
                    }
                    String sessionId = sectionToSession.getOrDefault(sectionId, semesterId);
                    String beginDate = sessionBegin.getOrDefault(sessionId, "");
                    String endDate   = sessionEnd  .getOrDefault(sessionId, "");
                    Map<String, String> sched = schedules.getOrDefault(sectionId, Collections.emptyMap());
                    String daysRaw   = sched.getOrDefault("classMeetingDays", "");
                    String startTime = formatTime(sched.getOrDefault("classBeginningTime", ""));
                    String endTime   = formatTime(sched.getOrDefault("classEndingTime",   ""));
                    String room      = sched.getOrDefault("instructionSiteName", "");
                    pw.println(
                        csv(courseCode) + "," +
                        csv(sectionId) + "," +
                        csv(sectionNum) + "," +
                        csv(semesterId) + "," +
                        csv(sessionId) + "," +
                        csv(beginDate) + "," +
                        csv(endDate) + "," +
                        csv(sec.getOrDefault("creditHours", "")) + "," +
                        csv(sec.getOrDefault("capacity", "")) + "," +
                        csv(sec.getOrDefault("currentEnrollment", "")) + "," +
                        csv(expandStatus(sec.getOrDefault("status", ""))) + "," +
                        csv(permCounts.getOrDefault(sectionId, "0")) + "," +
                        csv(parseDays(daysRaw)) + "," +
                        csv(daysRaw) + "," +
                        csv(startTime) + "," +
                        csv(endTime) + "," +
                        csv(room) + "," +
                        csv(sectionInstructorStr.getOrDefault(sectionId, ""))
                    );
                    sectionRowCount++;
                }
            }
        }
        System.out.println("Wrote " + sectionRowCount + " rows -> " + sectionsOut.toAbsolutePath());
    }
}