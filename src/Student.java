package src;
// This is the Student Class
// Each Student Object is 1 user
//

/***
 * WORK TO BE DONE BY TOM:
 * Put in tests for edge cases






 * 
 * 
 * 
 * 
 * 
 */

//Imports
import java.util.ArrayList;
import java.util.HashMap;


public class Student implements StudentInterface<Course>{

    private int studentID;
    private String email;
    private String major;
    private ArrayList<Course> coursesCompleted;
    private String password;
   

    public Student(String Email, int id, String major, String password){
        this.studentID = id;
        this.email=Email;
        this.coursesCompleted = new ArrayList<Course>();
        this.password = password;
        
    }

    public String getPassword(){
        return this.password;
    }

    public String getEmail(){
        return this.email;
    }

    public int getID(){
        return this.studentID;
    }

    public void setMajor(String major) {
        this.major = major;
    }
  
    public void checkMajorProgress(){
        String[] requiredCourses = {"51", "54", "62", "101", "105", "140", "190"};
        HashMap<String, Boolean> coursesCompletedMap = new HashMap<>();
        for (String majorCourse : requiredCourses) {
            coursesCompletedMap.put(majorCourse, false);
        }
        int electivesCompleted = 0;

        for (Course myCourse : this.coursesCompleted){
            if (myCourse.getId().contains("CSCI")){
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
            else if (myCourse.getId().contains("CSCI051L") || myCourse.getId().contains("CSCI054L") || myCourse.getId().contains("CSCI062L") || myCourse.getId().contains("CSCI101L") || myCourse.getId().contains("CSCI105L") || myCourse.getId().contains("CSCI140L") || myCourse.getId().contains("CSCI190L")){
                // if it's a lab for one of the required courses, we do not count it as an elective
            }
            else{
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

    //true if the user has completed that course
    //I added this, don't know if it will end up being needed
    //here in case, we can always take out
    public boolean completedCourse(Course C){
        if (this.coursesCompleted.contains(C)) {return true;}
        return false;
    }

    //gets the students completed course at that index
    public Course getCourseCompleted(int index){
        return this.coursesCompleted.get(index);
    }

    //returns the student's array list of completed courses 
    public ArrayList<Course> getCompletedCourseList(){
        return this.coursesCompleted;
    }
    
    //returns the students major
    public String getMajor(){
        return this.major;
    }


    // //loops through the major courses needed and removes any that have been taken
    // //will be used when changing majors
    // public void updateCoursesNeeded(){
    //     for (Course n :this.majorCoursesNeeded){
    //         if (this.completedCourse(n)){
    //             this.majorCoursesNeeded.remove(n);
    //         }
    //     }
    // }
    // //Sets the student's major to new major
    // // creates a new arraylist for their new major and updates it
    // public void setMajor(String major){
    //     this.major = major;
    //     this.majorCoursesNeeded = new ArrayList<Course>(); // 
    //     this.updateCoursesNeeded();
    // }



    
}
