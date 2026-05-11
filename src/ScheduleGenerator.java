package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ScheduleGenerator {

    /**
     * Generates a 4-course schedule based on desired courses and fills the rest randomly.
     * @param student
     * @param desiredCourses
     * @param allCourses
     * @return
     */
    public ArrayList<Course> generateSchedule(Student student, List<Course> desiredCourses, HashMap<String, Course> allCourses) {
        ArrayList<Course> schedule = new ArrayList<>();
        ArrayList<Section> scheduledSections = new ArrayList<>(); 
        
        for (Course course : desiredCourses) {
            if (schedule.size() >= 4) break;
            
            if (canTakeCourse(student, course, true)) {
                Section validSection = findNonConflictingSection(course, scheduledSections);
                
                if (validSection != null) {
                    schedule.add(course);
                    scheduledSections.add(validSection);
                } else {
                    String targetId = (course.getId() != null) ? course.getId() : "Unknown Course";
                    System.out.println("Cannot add " + targetId + " - Time conflict with already scheduled courses.");
                }
            } 
        }

        if (schedule.size() < 4) {
            List<Course> availableCoursesList = new ArrayList<>(allCourses.values());
            Random rand = new Random();

            int attempts = 0;
            while (schedule.size() < 4 && attempts < availableCoursesList.size() * 2) {
                Course randomCourse = availableCoursesList.get(rand.nextInt(availableCoursesList.size()));
                
                boolean alreadyInSchedule = schedule.contains(randomCourse);
                boolean alreadyTaken = student.completedCourse(randomCourse); 
                
                if (!alreadyInSchedule && !alreadyTaken && canTakeCourse(student, randomCourse, false)) {
                    
                    Section validSection = findNonConflictingSection(randomCourse, scheduledSections);
                    if (validSection != null) {
                        schedule.add(randomCourse);
                        scheduledSections.add(validSection);
                    }
                }
                attempts++;
            }
        }

        return schedule;
    }

    /**
     * Checks if the course has at least one section that does not conflict with the existing schedule.
     */
    private Section findNonConflictingSection(Course course, ArrayList<Section> scheduledSections) {
        ArrayList<Section> availableSections = course.getSections();
        
        // If a course has no sections listed in the data, it cannot be taken
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
     * @param isDesired 
     */
    private boolean canTakeCourse(Student student, Course course, boolean isDesired) {
        String targetId = course.getId();
        if (targetId == null) return false;

        // Check Intro CS requirement
        ArrayList<String> missingIntro = getMissingIntroCourses(student);
        boolean isTargetAnIntroCourse = targetId.startsWith("CSCI051") || targetId.startsWith("CSCI054") || targetId.startsWith("CSCI062");

        // If they are missing intro courses AND the class they want isn't one of those intro classes
        if (!missingIntro.isEmpty() && !isTargetAnIntroCourse) {
            if (isDesired) {
                System.out.println("Cannot add " + targetId + " - You must first complete these intro courses: " + String.join(", ", missingIntro));
            }
            return false;
        }

        // Check prereqs stored in the Course object
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
     * Returns a list of the specific intro courses (51, 54, 62) the student has NOT taken.
     */
    private ArrayList<String> getMissingIntroCourses(Student student) {
        ArrayList<Course> completed = student.getCompletedCourseList(); 
        
        boolean has51 = false;
        boolean has54 = false;
        boolean has62 = false;

        // Check what they have taken
        for (Course c : completed) {
            String courseId = c.getId();
            if (courseId == null) continue;

            if (courseId.startsWith("CSCI051")) { 
                has51 = true;
            } else if (courseId.startsWith("CSCI054")) {
                has54 = true;
            } else if (courseId.startsWith("CSCI062")) {
                has62 = true;
            }
        }
        
        // Compile the missing list
        ArrayList<String> missing = new ArrayList<>();
        if (!has51) missing.add("CSCI051");
        if (!has54) missing.add("CSCI054");
        if (!has62) missing.add("CSCI062");

        return missing;
    }
}