package src;

import java.util.ArrayList;

/**
 * This is the interface for each course, it provides how a course object must operate
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface Course {

    public void addPreReq(Course course);

    public ArrayList<Course> getPreReq();

    public void addSecion(Section section);

    public Section getSection(int index);

    public ArrayList<Section> getSections();

    public void addDescription(String descrition);

    public String getDescription();

    public void addMajor(String major);

    public String getMajor();
}