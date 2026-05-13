package src;
// This is the Student Class
// Each Student Object is 1 user
//

//Imports
import java.util.ArrayList;
import java.util.HashMap;


public class Student implements StudentInterface<Course>{

    private int studentID;
    private String email;
    private String major;
    private ArrayList<Course> coursesCompleted;
   
   
    //Constructor
    protected Student(String Email, int id, String major){
        this.studentID = id;
        this.email=Email;
        this.coursesCompleted = new ArrayList<Course>();
        
        
    }

    // This method checks the student's progress towards their major requirements and prints out which courses they still need to complete, if any.
    protected void checkMajorProgress(){
        String[] requiredCourses = {"51", "54", "62", "101", "105", "140", "190"};
        HashMap<String, Boolean> coursesCompletedMap = new HashMap<>();
        for (String majorCourse : requiredCourses) {
            coursesCompletedMap.put(majorCourse, false);
        }
        int electivesCompleted = 0;
        // loop through the student's completed courses and check if they have completed the required courses
        for (Course myCourse : this.coursesCompleted){
            if (myCourse.getId().contains("CSCI") && myCourse.getId().length()>=10) {
                // check if it's valid cs class
                // check each intro/core class, if it's completed, mark it as true in the map
               if (myCourse.getId().equals("CSCI051 PO")){
                   coursesCompletedMap.put("51", true);
               }
            else if (myCourse.getId().equals("CSCI054 PO")){
                   coursesCompletedMap.put("54", true);
               }
            else if (myCourse.getId().equals("CSCI062 PO")){
                   coursesCompletedMap.put("62", true);
               }
            else if (myCourse.getId().equals("CSCI101 PO")){
                   coursesCompletedMap.put("101", true);
               }
            else if (myCourse.getId().equals("CSCI105 PO") || myCourse.getId().equals("CSCI105 HM")){
                   coursesCompletedMap.put("105", true);
               }
            else if (myCourse.getId().equals("CSCI140 PO") || myCourse.getId().equals("CSCI140 HM")){
                   coursesCompletedMap.put("140", true);
               }
            else if (myCourse.getId().equals("CSCI190 PO")){
                   coursesCompletedMap.put("190", true);
               }
            else if (myCourse.getId().contains("CSCI050")  || myCourse.getId().charAt(7)=='L'){
                // if it's a lab or cs 50, we do not count it as an elective
            }
            else{
            //any other valid cs course counts as an elective
                electivesCompleted++;}
            }
        }
        // check which required courses are still needed and how many electives are still needed
        ArrayList<String> coursesStillNeeded = new ArrayList<String>();
        for (String course : requiredCourses) {
            if (!coursesCompletedMap.get(course)) {
                coursesStillNeeded.add(course);
            }
        }
        // finally print the results to the user
        if (coursesStillNeeded.size() == 0 && electivesCompleted >= 3) {
            System.out.println("Congratulations! You have completed all the required courses for your major.");
        } else {
            if (coursesStillNeeded.size() > 0) {
                System.out.println("You still need to complete the following introductory/core courses for your major:");
                for (String course : coursesStillNeeded) {
                    System.out.println(course);
                }
            }
            // there needs to be at least 3 electives
            if (electivesCompleted < 3) {
                System.out.println("You need to complete " + (3 - electivesCompleted) + " more elective courses.");
            }
        }

    }


    //adds a course to the student's list
    protected void addCourseCompleted(Course completed){
        if (!this.coursesCompleted.contains(completed)) {this.coursesCompleted.add(completed);}       
    }

    //true if the user has completed that course
    //I added this, don't know if it will end up being needed
    //here in case, we can always take out
    protected boolean completedCourse(Course C){
        if (this.coursesCompleted.contains(C)) {return true;}
        return false;
    }

      //gets the students completed course at that index
    protected Course getCourseCompleted(int index){
        return this.coursesCompleted.get(index);
    }

    //returns the student's array list of completed courses 
    protected ArrayList<Course> getCompletedCourseList(){
        return this.coursesCompleted;
    }
    

    
}
