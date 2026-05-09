package src;
//This is the Student Class
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

    //adds a course to the student's list, if it is not already in there
    //This has no checks for validity because completed will be checked before function is called
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
