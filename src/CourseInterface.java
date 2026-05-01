package src;

import java.util.ArrayList;

/**
 * This is the interface for each course, it provides how a course object must operate
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface CourseInterface<C, S> {

    public void addPreReq(C course);

    public ArrayList<C> getPreReq();

    public void addSecion(S section);

    public S getSection(int index);

    public  ArrayList<S> getSections();

    public void setDescription(String descrition);

    public String getDescription();

    public void addMajor(String major);

    public ArrayList<String> getMajor();
}