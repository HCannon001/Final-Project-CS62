package src;

import java.time.LocalTime;

/**
 * This is the interface for a section of a course
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface SectionInterface {
    
    public void setStartTime(LocalTime startTime);

    public LocalTime getStartTime();

    public void setEndTime(LocalTime endTime);

    public LocalTime getEndTime();

    public String getLocation();

    public void setLocation(String location);

    public void setProfessor(String professor);

    public String getProfessor();
}
