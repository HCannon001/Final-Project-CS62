package src;

import java.time.LocalTime;

/**
 * This is the interface for a section of a course
 * 
 * @author Henry, Phineus, Deniz, Tom
 */
public interface Section {
    
    public void addTime(LocalTime time);

    public LocalTime getTime();

    public String getLocation();

    public void addLocation(String location);

    public void addProfessor(String professor);

    public String getProfessor();
}
