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
    public Student(String Email, int id, String major){
        this.studentID = id;
        this.email=Email;
        this.coursesCompleted = new ArrayList<Course>();
        
        
    }
    
    public void checkMajorProgress(){
        String[] requiredCourses = {"51", "54", "62", "101", "105", "140", "190"};
        HashMap<String, Boolean> coursesCompletedMap = new HashMap<>();
        for (String majorCourse : requiredCourses) {
            coursesCompletedMap.put(majorCourse, false);
        }
        int electivesCompleted = 0;

        for (Course myCourse : this.coursesCompleted){
            if (myCourse.getId().contains("CSCI") && myCourse.getId().length()>=10) {
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
                // if it's a lab for one of the required courses, we do not count it as an elective
            }
            else{
            // add checks for labs and CS 50
            //might wanna add lab checks and/or low division checks and/or repetition checks
                electivesCompleted++;}
            }
        }

        ArrayList<String> coursesStillNeeded = new ArrayList<String>();
        for (String course : requiredCourses) {
            if (!coursesCompletedMap.get(course)) {
                coursesStillNeeded.add(course);
            }
        }
        if (coursesStillNeeded.size() == 0 && electivesCompleted >= 3) {
            System.out.println("Congratulations! You have completed all the required courses for your major.");
        } else {
            if (coursesStillNeeded.size() > 0) {
                System.out.println("You still need to complete the following introductory/core courses for your major:");
                for (String course : coursesStillNeeded) {
                    System.out.println(course);
                }
            }
            if (electivesCompleted < 3) {
                System.out.println("You need to complete " + (3 - electivesCompleted) + " more elective courses.");
            }
        }
        }


    //adds a course to the student's list
    public void addCourseCompleted(Course completed){
        if (!this.coursesCompleted.contains(completed)) {this.coursesCompleted.add(completed);}       
    }

    //returns the student's array list of completed courses 
    public ArrayList<Course> getCompletedCourseList(){
        return this.coursesCompleted;
    }
    

    
}
