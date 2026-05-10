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
        
        // Process desired courses
        for (Course course : desiredCourses) {
            if (schedule.size() >= 4) break;
            
            if (canTakeCourse(student, course, true)) {
                // TODO: Add time-conflict validation here later once Section formatting is complete
                schedule.add(course);
            } 
        }

        // Randomly populate the rest of the schedule up to 4 courses
        if (schedule.size() < 4) {
            List<Course> availableCoursesList = new ArrayList<>(allCourses.values());
            Random rand = new Random();

            int attempts = 0;
            // Prevent infinite loop if catalog is too small
            while (schedule.size() < 4 && attempts < availableCoursesList.size() * 2) {
                Course randomCourse = availableCoursesList.get(rand.nextInt(availableCoursesList.size()));
                
                boolean alreadyInSchedule = schedule.contains(randomCourse);
                boolean alreadyTaken = student.completedCourse(randomCourse); 
                
                // Pass 'false' so we DON'T spam the console with errors while randomly guessing
                if (!alreadyInSchedule && !alreadyTaken && canTakeCourse(student, randomCourse, false)) {
                    // TODO: Add time-conflict validation here later
                    schedule.add(randomCourse);
                }
                attempts++;
            }
        }

        return schedule;
    }

    /**
     * Iteratively checks if the student meets all prerequisites to take the course.
     * @param isDesired If true, prints out the exact missing courses to the console.
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
     * Returns a list of the specific core courses (51, 54, 62) the student has NOT taken.
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