package src;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Builds 2 csv files based on an inputted java file
 */
public class CourseDataBuilder {

    /** Parse a JSON array where every element is a flat object (all string values). */
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
     * Array values come back as List<String>; scalar values come back as String.
     */
    static List<Map<String, Object>> parseJsonArrayRaw(Path path) throws IOException {
        String text = new String(Files.readAllBytes(path), "UTF-8");
        List<Map<String, Object>> result = new ArrayList<>();
        for (String block : splitObjects(text)) {
            result.add(parseObjectWithArrays(block));
        }
        return result;
    }

    /** Split a top-level JSON array string into individual "{...}" object strings. */
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

    /** Parse a flat JSON object — all values treated as strings. */
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
     */
    private static Map<String, Object> parseObjectWithArrays(String block) {
        Map<String, Object> map = new LinkedHashMap<>();

        // Find and extract "[...]" array values before the flat parser runs
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
            arrMat.appendReplacement(sb, "");   // remove so flat parser ignores it
        }
        arrMat.appendTail(sb);

        // Parse remaining scalar fields
        map.putAll(parseFlatObject(sb.toString()));
        return map;
    }

    /** YYYYMMDD -> YYYY-MM-DD */
    private static String formatDate(String raw) {
        if (raw == null || raw.length() != 8) return raw == null ? "" : raw;
        return raw.substring(0, 4) + "-" + raw.substring(4, 6) + "-" + raw.substring(6, 8);
    }

    /** "1315" -> "13:15"; handles 3- or 4-digit strings */
    private static String formatTime(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        try {
            String padded = String.format("%04d", Integer.parseInt(raw.trim()));
            return padded.substring(0, 2) + ":" + padded.substring(2, 4);
        } catch (NumberFormatException e) {
            return raw;
        }
    }

    /**
     * Convert the 7-char meeting-days bitmask to a readable string.
     * Position order: S M T W R F S  (Sun Mon Tue Wed Thu Fri Sat)
     * A non-dash character means that day meets.
     * Days are separated by semicolons to avoid conflict with the CSV delimiter.
     */
    private static String parseDays(String mask) {
        if (mask == null || mask.isEmpty()) return "";
        String[] labels = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Math.min(mask.length(), labels.length); i++) {
            if (mask.charAt(i) != '-') {
                if (out.length() > 0) out.append(";");
                out.append(labels[i]);
            }
        }
        return out.toString();
    }

    /** Expand single-character status codes to readable strings. */
    private static String expandStatus(String code) {
        switch (code) {
            case "O": return "Open";
            case "C": return "Closed";
            case "W": return "Waitlisted";
            default:  return code;
        }
    }

    /** Safely get a string from an Object-valued map. */
    private static String strVal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? "" : v.toString();
    }

    /**
     * Wrap a value for CSV output.
     * Always quotes to safely handle commas, newlines, and special characters.
     * Internal double-quotes are escaped by doubling them per RFC 4180.
     */
    private static String csv(String value) {
        if (value == null) value = "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /** Create a UTF-8 PrintWriter for a given path. */
    private static PrintWriter utf8Writer(Path path) throws IOException {
        return new PrintWriter(new OutputStreamWriter(
            new FileOutputStream(path.toFile()), "UTF-8"
        ));
    }

    public static void main(String[] args) throws Exception {
        String dataDir = args.length > 0 ? args[0] : ".";
        Path dir = Paths.get(dataDir);
        System.out.println("Reading source files from: " + dir.toAbsolutePath());

        // 1. Staff  (alt-staff first as baseline, staff.json overwrites)
        Map<String, String> staffNames = new HashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("alt-staff.json"))) {
            // altName is stored as "Last\, First" — clean the escaped comma
            String name = row.getOrDefault("altName", "").replace("\\,", ",");
            staffNames.put(row.getOrDefault("cxId", ""), name);
        }
        for (Map<String, String> row : parseJsonArray(dir.resolve("staff.json"))) {
            staffNames.put(row.getOrDefault("cxId", ""), row.getOrDefault("fullName", ""));
        }

        // 2. Calendar sessions  (semesterId -> beginDate, endDate)
        Map<String, String> sessionBegin = new HashMap<>();
        Map<String, String> sessionEnd   = new HashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("calendar-session.json"))) {
            String id = row.getOrDefault("externalId", "");
            sessionBegin.put(id, formatDate(row.getOrDefault("beginDate", "")));
            sessionEnd  .put(id, formatDate(row.getOrDefault("endDate",   "")));
        }

        // 3. Section -> calendar session
        Map<String, String> sectionToSession = new HashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("calendar-session-section.json"))) {
            sectionToSession.put(
                row.getOrDefault("courseSectionId", ""),
                row.getOrDefault("calendarSessionExternalId", "")
            );
        }

        // 4. Area code descriptions
        Map<String, String> areaDesc = new HashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("course-areas-description.json"))) {
            areaDesc.put(row.getOrDefault("area", ""), row.getOrDefault("description", ""));
        }

        // 5. Course areas  (course_code -> semicolon-joined "CODE:Description" list)
        Map<String, String> courseAreasStr = new HashMap<>();
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

        // 6. Section schedules
        Map<String, Map<String, String>> schedules = new HashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("course-section-schedule.json"))) {
            schedules.put(row.getOrDefault("courseSectionId", ""), row);
        }

        // 7. Section instructors  (sectionId -> semicolon-joined full names)
        Map<String, String> sectionInstructorStr = new HashMap<>();
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

        // 8. Perm counts
        Map<String, String> permCounts = new HashMap<>();
        for (Map<String, String> row : parseJsonArray(dir.resolve("perm-count.json"))) {
            permCounts.put(row.getOrDefault("courseSectionId", ""), row.getOrDefault("permCount", "0"));
        }

        // 9. Course sections
        //    courseSectionId format: "<courseCode>-<sectionNum> <semesterId>"
        //    e.g. "AFRI020  PZ-01 SP2026"
        Pattern sectionPattern = Pattern.compile("^(.+)-(\\d+)\\s+(\\S+)$");

        List<Map<String, String>> allSections = parseJsonArray(dir.resolve("course-section.json"));

        // courseCode -> ordered list of section rows
        Map<String, List<Map<String, String>>> courseToSections = new LinkedHashMap<>();
        for (Map<String, String> sec : allSections) {
            String sectionId = sec.getOrDefault("courseSectionId", "");
            Matcher m = sectionPattern.matcher(sectionId);
            if (!m.matches()) continue;
            String courseCode = m.group(1);
            courseToSections.computeIfAbsent(courseCode, k -> new ArrayList<>()).add(sec);
        }

        // 10. Parse course.txt
        //
        //  Records are separated by "|#|"
        //  Fields within a record are separated by "|^|"
        //  Field order: COURSE_CODE | NAME | AREA_CODE | CIP_CODE | COLLEGE | DESCRIPTION
        //  The first record is a DBSTART header — skip it.
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
                    csv(code)       + "," +
                    csv(inf[0])     + "," +   // courseName
                    csv(inf[3])     + "," +   // college
                    csv(inf[1])     + "," +   // areaCode
                    csv(inf[2])     + "," +   // cipCode
                    csv(courseAreasStr.getOrDefault(code, "")) + "," +
                    csv(inf[4])               // description
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
                        csv(courseCode)                                             + "," +
                        csv(sectionId)                                              + "," +
                        csv(sectionNum)                                             + "," +
                        csv(semesterId)                                             + "," +
                        csv(sessionId)                                              + "," +
                        csv(beginDate)                                              + "," +
                        csv(endDate)                                                + "," +
                        csv(sec.getOrDefault("creditHours", ""))                   + "," +
                        csv(sec.getOrDefault("capacity", ""))                      + "," +
                        csv(sec.getOrDefault("currentEnrollment", ""))             + "," +
                        csv(expandStatus(sec.getOrDefault("status", "")))          + "," +
                        csv(permCounts.getOrDefault(sectionId, "0"))               + "," +
                        csv(parseDays(daysRaw))                                    + "," +
                        csv(daysRaw)                                               + "," +
                        csv(startTime)                                             + "," +
                        csv(endTime)                                               + "," +
                        csv(room)                                                  + "," +
                        csv(sectionInstructorStr.getOrDefault(sectionId, ""))
                    );
                    sectionRowCount++;
                }
            }
        }
        System.out.println("Wrote " + sectionRowCount + " rows -> " + sectionsOut.toAbsolutePath());
    }
}