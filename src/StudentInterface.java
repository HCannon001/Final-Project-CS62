package src;

import java.util.ArrayList;

/**
 * This is the interface for a student object
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface StudentInterface<C> {

    public void addCourseCompleted(Course completed);

    public String getEmail();

    public int getID();
    
    public void checkMajorProgress(); 

    public C getCourseCompleted(int index);

    public ArrayList<C> getCompletedCourseList();

    public String getMajor();

}
