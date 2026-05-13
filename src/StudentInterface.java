package src;

import java.util.ArrayList;

/**
 * This is the interface for a student object
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface StudentInterface<C> {

    public void addCourseCompleted(Course completed);

    public void checkMajorProgress(); 

    public ArrayList<C> getCompletedCourseList();


}
