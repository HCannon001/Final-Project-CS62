package src;

import java.time.LocalTime;

/**
 * This is the Section Class
 * 
 * Each Course has sections, this is the object that stores the data
 * 
 * Here we have course time, days, professors, and locations
 * 
 * @author Henry, Deniz, Tom, Phineus
 */
public class Section implements SectionInterface {

    private String days;
    private LocalTime time;
    private String professor;
    private String location;

    /**
     * Constructor for Section
     * @param days
     * @param time
     * @param professor
     * @param location
     */
    Section(String days, LocalTime time, String professor, String location) {
        this.days = days;
        this.time = time;
        this.professor = professor;
        this.location = location;
    }

    /**
     * Days setter for Section offering
     * 
     * Input the string cooresponding to the days the class is offered
     * @param days
     */
    public void setDays(String days) {
        this.days = days;
    }

    /**
     * Days getter for the Section Offering
     *
     * @return days
     */
    public String getDays() {
        return days;
    }

    /**
     * Time setter for the section offering
     * 
     * @param time
     */
    @Override
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Time getter for the section offering
     * 
     * @return time
     */
    @Override
    public LocalTime getTime() {
        return time;
    }

    /**
     * Location getter for the section offering
     * 
     * @return location
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Location setter for the section offering
     * 
     * @param location
     */
    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Professor setter for the section offering
     * 
     * @param professor
     */
    @Override
    public void setProfessor(String professor) {
        this.professor = professor;
    }

    /**
     * Professor getter for the section offering
     * 
     * @return professor
     */
    @Override
    public String getProfessor() {
        return this.professor;
    }
    
}
