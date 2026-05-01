package src;
//This is the Student Class
// Each Student Object is 1 user


//Imports
import java.util.ArrayList;


public class Student implements StudentInterface<Course>{

    private int studentID;
    private String email;
    private String major;
    private ArrayList<Course> coursesCompleted;
    private ArrayList<Course> majorCoursesNeeded; // once we get data from fileparser this will be pre-initialized for each student 
    private ArrayList<Course> geCoursesNeeded; // once we get data from fileparser this will be pre-initialized for each student  

    public Student(String Email, int id, String major){
        this.studentID = id;
        this.email=Email;
        this.coursesCompleted = new ArrayList<Course>();
        this.majorCoursesNeeded = new ArrayList<Course>();
        this.geCoursesNeeded = new ArrayList<Course>();
    }

    //returns student's MajorCousesNeeded 
    public ArrayList<Course> getMajorCoursesNeeded(){
        return this.majorCoursesNeeded;
    }

    //get ge CoursesNeeded
    public ArrayList<Course> getGECoursesNeeded(){
        return this.geCoursesNeeded;
    }

    //right now set to null because don't know if this should be GEs or how we are handling that yet
    public ArrayList<Course> getCoursesNeeded(){
        return null;
    }

    //adds a course to the student's list
    public void addCourseCompleted(Course completed){
        this.coursesCompleted.add(completed);
        if (this.majorCoursesNeeded.contains(completed)){
            this.majorCoursesNeeded.remove(completed);
        }
        if (this.geCoursesNeeded.contains(completed)){
            this.geCoursesNeeded.remove(completed);
        }
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

    //loops through the major courses needed and removes any that have been taken
    //will be used when changing majors
    public void updateCoursesNeeded(){
        for (Course n :this.majorCoursesNeeded){
            if (this.completedCourse(n)){
                this.majorCoursesNeeded.remove(n);
            }
        }
    }
    //Sets the student's major to new major
    // creates a new arraylist for their new major and updates it
    public void setMajor(String major){
        this.major = major;
        this.majorCoursesNeeded = new ArrayList<Course>(); // 
        this.updateCoursesNeeded();
    }



}
