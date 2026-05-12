package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ScheduleGenerator {

    /**
     * Generates a 4-course schedule based on desired courses and fills the rest randomly.
     * @param student        The current student object
     * @param desiredCourses A list of Course objects the student explicitly wants to take
     * @param allCourses     The HashMap of all available courses in the catalog (from App.java)
     * @return An ArrayList representing the generated schedule
     */
    public ArrayList<Course> generateSchedule(Student student, List<Course> desiredCourses, HashMap<String, Course> allCourses) {
        ArrayList<Course> schedule = new ArrayList<>();
        ArrayList<Section> scheduledSections = new ArrayList<>(); 
        
        // 1. Process desired courses
        for (Course course : desiredCourses) {
            // Stop if we already have 4 main courses
            if (countMainCourses(schedule) >= 4) break;
            
            if (canTakeCourse(student, course, true)) {
                Section validSection = findNonConflictingSection(course, scheduledSections);
                
                if (validSection != null) {
                    schedule.add(course);
                    scheduledSections.add(validSection);
                } else {
                    String targetId = (course.getId() != null) ? course.getId() : "Unknown Course";
                    System.out.println("Cannot add " + targetId + " - Time conflict with already scheduled courses or missing section data.");
                }
            } 
        }

        // 2. Randomly populate the rest of the schedule up to 4 MAIN courses
        List<Course> availableCoursesList = new ArrayList<>(allCourses.values());
        Random rand = new Random();
        int attempts = 0;
        
        while (countMainCourses(schedule) < 4 && attempts < availableCoursesList.size() * 2) {
            Course randomCourse = availableCoursesList.get(rand.nextInt(availableCoursesList.size()));
            
            // Skip labs when randomly searching for main classes
            if (isLab(randomCourse)) {
                attempts++;
                continue;
            }

            boolean alreadyInSchedule = schedule.contains(randomCourse);
            boolean alreadyTaken = student.completedCourse(randomCourse); 
            
            // Pass 'false' so we DON'T spam the console with errors
            if (!alreadyInSchedule && !alreadyTaken && canTakeCourse(student, randomCourse, false)) {
                Section validSection = findNonConflictingSection(randomCourse, scheduledSections);
                if (validSection != null) {
                    schedule.add(randomCourse);
                    scheduledSections.add(validSection);
                }
            }
            attempts++;
        }

        return schedule;
    }

    /**
     * Helper to count only main lectures (ignores labs) so students get a full 4-course load.
     */
    private int countMainCourses(ArrayList<Course> schedule) {
        int count = 0;
        for (Course c : schedule) {
            if (!isLab(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Helper to identify if a course is a lab based on its name or ID.
     */
    private boolean isLab(Course course) {
        // Assuming your Course class has a getName() method. If not, rely entirely on getId()
        if (course.getName() != null && course.getName().toLowerCase().contains("lab")) {
            return true;
        }
        // Check if the ID ends with 'L' (a common naming convention for labs)
        if (course.getId() != null && course.getId().endsWith("L")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the course has at least one section that does not conflict with the existing schedule.
     */
    private Section findNonConflictingSection(Course course, ArrayList<Section> scheduledSections) {
        ArrayList<Section> availableSections = course.getSections();
        
        if (availableSections == null || availableSections.isEmpty()) {
            return null; 
        }

        for (Section section : availableSections) {
            boolean conflict = false;
            for (Section scheduled : scheduledSections) {
                if (doesConflict(section, scheduled)) {
                    conflict = true;
                    break;
                }
            }
            if (!conflict) {
                return section; 
            }
        }
        return null; 
    }

    /**
     * Determines if two specific sections overlap in days and time.
     */
    private boolean doesConflict(Section s1, Section s2) {
        if (s1.getDays() == null || s2.getDays() == null || s1.getTime() == null || s2.getTime() == null) {
            return false; 
        }

        boolean daysOverlap = false;
        for (char day : s1.getDays().toCharArray()) {
            if (s2.getDays().indexOf(day) != -1) {
                daysOverlap = true;
                break;
            }
        }

        if (!daysOverlap) {
            return false;
        }

        return s1.getTime().equals(s2.getTime());
    }

    /**
     * Iteratively checks if the student meets all prerequisites to take the course.
     */
    private boolean canTakeCourse(Student student, Course course, boolean isDesired) {
        String targetId = course.getId();
        if (targetId == null) return false;

        ArrayList<String> missingIntro = getMissingIntroCourses(student);
        boolean isTargetAnIntroCourse = targetId.startsWith("CSCI051") || targetId.startsWith("CSCI054") || targetId.startsWith("CSCI062");

        if (isTargetAnIntroCourse) {
            // STRICT SEQUENCE ENFORCEMENT
            if (targetId.startsWith("CSCI054") && missingIntro.contains("CSCI051")) {
                if (isDesired) System.out.println("Cannot add " + targetId + " - You must take CSCI051 first.");
                return false;
            }
            if (targetId.startsWith("CSCI062") && (missingIntro.contains("CSCI051") || missingIntro.contains("CSCI054"))) {
                if (isDesired) System.out.println("Cannot add " + targetId + " - You must take CSCI051 and CSCI054 first.");
                return false;
            }
        } else if (!missingIntro.isEmpty()) {
            // For ALL OTHER courses, they must have finished all intro courses
            if (isDesired) {
                System.out.println("Cannot add " + targetId + " - You must first complete these intro courses: " + String.join(", ", missingIntro));
            }
            return false;
        }

        // Check standard prereqs stored in the Course object
        ArrayList<Course> preReqs = course.getPreReq(); 
        if (preReqs != null && !preReqs.isEmpty()) {
            for (Course req : preReqs) {
                if (!student.completedCourse(req)) { 
                    if (isDesired) {
                        String reqId = (req.getId() != null) ? req.getId() : "Unknown Course";
                        System.out.println("Cannot add " + targetId + " - Missing specific prerequisite: " + reqId);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method to verify intro CS courses.
     */
    private ArrayList<String> getMissingIntroCourses(Student student) {
        ArrayList<Course> completed = student.getCompletedCourseList(); 
        
        boolean has51 = false;
        boolean has54 = false;
        boolean has62 = false;

        for (Course c : completed) {
            String courseId = c.getId();
            if (courseId == null) continue;

            if (courseId.startsWith("CSCI051")) has51 = true;
            else if (courseId.startsWith("CSCI054")) has54 = true;
            else if (courseId.startsWith("CSCI062")) has62 = true;
        }
        
        ArrayList<String> missing = new ArrayList<>();
        if (!has51) missing.add("CSCI051");
        if (!has54) missing.add("CSCI054");
        if (!has62) missing.add("CSCI062");

        return missing;
    }
}