package src;

import java.util.ArrayList;

/**
 * This is the interface for a student object
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface StudentInterface<C> {

    public ArrayList<C> getMajorCoursesNeeded();

    public ArrayList<C> getCoursesNeeded();

    public void addCourseCompleted(Course completed);

    public C getCourseCompleted(int index);

    public ArrayList<C> getCompletedCourseList();

    public String getMajor();

    public void setMajor(String major);
}
