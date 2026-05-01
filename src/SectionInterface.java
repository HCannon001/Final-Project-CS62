package src;

import java.time.LocalTime;

/**
 * This is the interface for a section of a course
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface SectionInterface {
    
    public void setTime(LocalTime time);

    public LocalTime getTime();

    public String getLocation();

    public void setLocation(String location);

    public void setProfessor(String professor);

    public String getProfessor();
}
