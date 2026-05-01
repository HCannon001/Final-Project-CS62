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

    public void addCourseNeeded(C course);

    public void addCourseCompleted(C completed);

    public C getCourseCompleted(int index);

    public ArrayList<C> getCompletedCourse();
}
