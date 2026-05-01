package src;

import java.util.ArrayList;

/**
 * Course Class for each course
 * 
 * This is the class that each courses data gets stored in
 * 
 * @author Henry, Deniz, Tom, Phineus
 */
public class Course implements CourseInterface<Course, Section> {

    private String id;
    private String name;
    private String description;
    private ArrayList<String> majors;
    private ArrayList<Section> sections;
    private ArrayList<Course> preReqs;

    /**
     * Constructor for Course object
     * @param id
     * @param name
     * @param description
     */
    Course(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        majors = new ArrayList<>();
        sections = new ArrayList<>();
        preReqs = new ArrayList<>();
    }

    /**
     * add to ArrayList of preReqs
     * @param course
     */
    public void addPreReq(Course course) {
        preReqs.add(course);
    }

    /**
     * getter of preReq ArrayList
     */
    public ArrayList<Course> getPreReq() {
        return preReqs;
    }

    /**
     * Add a section for the course
     * @param section
     */
    public void addSecion(Section section) {
        sections.add(section);
    }

    /**
     * Return a Section at a given index
     * 
     * @param index
     * 
     * @return section
     */
    public Section getSection(int index) {
        return sections.get(index);
    }

    /**
     * Get all sections offered
     * 
     * @return sections
     */
    public ArrayList<Section> getSections() {
        return sections;
    }

    /**
     * setter for descriptions
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getter for description
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Add a major to the array of majors
     * 
     * @param major
     */
    public void addMajor(String major) {
        majors.add(major);
    }

    /**
     * Return ArrayList of majors
     * 
     * @return majors
     */
    public ArrayList<String> getMajor() {
        return majors;
    }
}
