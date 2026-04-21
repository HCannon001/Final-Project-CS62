package src;

import java.util.ArrayList;

/**
 * This is the interface for a student object
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface Student {

    public ArrayList<Course> getMajorCoursesNeeded();

    public ArrayList<Course> getCoursesNeeded();

    public void addCourseNeeded(Course course);

    public void addCourseCompleted(Course completed);

    public Course getCourseCompleted(int index);

    public ArrayList<Course> getCompletedCourse();
}
