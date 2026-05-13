package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class App {
    
    ScheduleGenerator scheduleGenerator;
    //key is course id
    HashMap<String, Course> courses;
    //key is name, value is course id
    HashMap<String, String> courseNameToId;
    //key is course email
    HashMap<String, Student> students;
    //is app running
    boolean run = true;
    //the current student logged in
    Student currentStudent = null;

    /**
     * Constructor for App
     * Builds courses and associated sections from the files
     * 
     * @param csvFiles - expects flag followed by the associated file
     */
    App(String[] csvFiles) {
        this.students = new HashMap<>();
        scheduleGenerator = new ScheduleGenerator();

        String coursesCSV = null;
        String sectionsCSV = null;

        //get directiories for the csv files
        for (int i = 0; i < csvFiles.length; i++) {
            if (csvFiles[i].equals("--courses")) {
                if (i + 1 < csvFiles.length) {
                    coursesCSV = csvFiles[i + 1];
                    System.out.println("The Course CSV is set to " + coursesCSV);
                    i++;
                } else {
                    System.err.print("Error: Flag " + csvFiles[i] + " requires an argument.");
                    return;
                }
            }
            if (csvFiles[i].equals("--sections")) {
                if (i + 1 < csvFiles.length) {
                    sectionsCSV = csvFiles[i + 1];
                    System.out.println("The Section CSV is set to " + sectionsCSV);
                    i++;
                } else {
                    System.err.print("Error: Flag " + csvFiles[i] + " requires an argument.");
                    return;
                }
            }
        }

        if (coursesCSV == null) {
            throw new IllegalArgumentException("Error: The --courses flag is requried to run the program along with a csv file");
        }
        if (sectionsCSV == null) {
            throw new IllegalArgumentException("Error: The --sections flag is requried to run the program along with a csv file");
        }

        this.courses = new HashMap<>();
        this.courseNameToId = new HashMap<>();

        loadCourses(coursesCSV);
        loadSections(sectionsCSV);
    }

    private void loadCourses(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = parseCSVLine(line);
                if (fields.length < 3) {
                    continue;
                }

                String courseCode = fields[0].replaceAll("\\s{2,}", " ").trim();
                String courseName = fields[1].trim();
                String description = fields.length > 6 ? fields[6].trim() : "";

                Course course = new Course(courseCode, courseName, description);

                if (fields.length > 2) {
                    course.addMajor(fields[2].trim());
                }
                if (fields.length > 3) {
                    course.addMajor(fields[3].trim());
                }

                courses.put(courseCode, course);
                courseNameToId.put(courseName, courseCode);
                
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading courses file: " + e.getMessage());
        }
       
    }

    private void loadSections(String filePath) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        int attached = 0;
        int skipped = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = parseCSVLine(line);
                if (fields.length < 17) {
                    continue;
                }

                String courseCode = fields[0].replaceAll("\\s{2,}", " ").trim();
                String meetingDays = fields[12].trim();
                String startTimestr = fields[14].trim();
                String endTimestr = fields[15].trim();
                String room = fields[16].trim();
                String instructor = fields[17].trim();

                LocalTime startTime;
                LocalTime endTime;
                try {
                    startTime = LocalTime.parse(startTimestr, timeFormatter);
                    endTime = LocalTime.parse(endTimestr, timeFormatter);
                } catch (Exception e) {
                    startTime = LocalTime.MIDNIGHT;
                    endTime = LocalTime.MIDNIGHT;
                }

                Section section = new Section(meetingDays, startTime, endTime, instructor, room);

                Course course = courses.get(courseCode);
                if (course != null) {
                    course.addSection(section);
                    attached++;
                } else {
                    skipped++;
                }
                br.close();
            }
        } catch (IOException e) {
            System.err.println("Error reading section file: " + e.getMessage());
        }
    }

    private String[] parseCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }

    /**
     * The main loop for the program
     */
    public void runApp() {
        Scanner inputScanner = new Scanner(System.in);
        currentStudent = new Student(null, 0, null);
        while (run) {
            System.out.println("Press: \n 1: to add classes taken \n 2: to remove a class from classes taken \n 3: to see the courses entered \n 4: to check your major progress \n 5: to get a possible schedule \n 6: reset data \n 7: exit app");
            try{
                int response = inputScanner.nextInt();
                inputScanner.nextLine();
                boolean proceed;
                switch (response) {
                    case 1:
                        proceed = true;
                        System.out.println("Please Now Enter The Courses You Would Like To Add By ID, for example: CSCI054 PO");
                        while(proceed){
                            ArrayList<Course> addedClasses = new ArrayList<Course>();
                            System.out.println("Enter Course To Add, or type 'STOP': ");
                            String line = inputScanner.nextLine().trim();
                            System.out.println(line);
                            if (line.equals("STOP")){
                                proceed = false;
                            }
                            else{
                                Course adding = this.courses.get(line);
                                if (adding != null){
                                    if (currentStudent.getCompletedCourseList().contains(adding)){
                                        System.out.println("You Have Already Added This Course To Your Completed Courselist.");
                                    }else{
                                        currentStudent.addCourseCompleted(adding); 
                                        System.out.println("Course Added!");
                                        addedClasses.add(adding);
                                    }
                                    
                                }
                                else{
                                    System.out.println("That is not a valid course.");
                                    System.out.println("");
                                }
                            }
                            System.out.println("The Following Courses Were Added:");
                            for (Course c : addedClasses){
                                System.out.println(c.getName());
                            }
                        }
                        break;
                    case 2:
                        proceed = true;
                        System.out.println("Please Now Enter The Courses You Would Like To Remove By ID, for example: CSCI054 PO");
                        while(proceed){
                            ArrayList<Course> removedClasses = new ArrayList<Course>();
                            System.out.println("Enter Course To Remove, or type 'STOP': ");
                            String line = inputScanner.nextLine().trim();
                            if (line.equals("STOP")){
                                proceed = false;
                            }
                            else{
                                Course removing = this.courses.get(line);
                                if (removing != null){
                                    if (!currentStudent.getCompletedCourseList().contains(removing)){
                                        System.out.println("You Cannot Remove A Course You Have Not Taken Yet");
                                    }else{
                                        currentStudent.removeCourseCompleted(removing); 
                                        System.out.println("Course Removed!");
                                        removedClasses.add(removing);
                                    }
                                    
                                }
                                else{
                                    System.out.println("That is not a valid course.");
                                    System.out.println("");
                                }
                            }
                            System.out.println("The Following Courses Were Removed:");
                            for (Course c : removedClasses){
                                System.out.println(c.getName());
                            }
                        }
                        break;
                    case 3:
                        ArrayList<Course> completedCourses = currentStudent.getCompletedCourseList();
                        System.out.println("The courses that you have taken are:");
                        for (int i = 0; i < completedCourses.size(); i++) {
                            System.out.println((i + 1) + ": " + completedCourses.get(i));
                        }
                        System.out.println("Hit enter to continue");
                        inputScanner.nextLine();
                        break;
                    case 4:
                        currentStudent.checkMajorProgress();
                        System.out.println("Hit enter to continue");
                        inputScanner.nextLine();
                        break;
                    case 5:
                        boolean proceedSchedule = true;
                        ArrayList<Course> desiredCourses = new ArrayList<>();
                        System.out.println("Please enter the courses you know you want to take (max 4).");
                        
                        while (proceedSchedule && desiredCourses.size() < 4) {
                            System.out.println("Enter Course Code (e.g., CSCI051 PO), type 'DONE' to generate, or 'STOP' to cancel: ");
                            String line = inputScanner.nextLine().trim();
                            
                            if (line.equalsIgnoreCase("STOP")) {
                                proceedSchedule = false;
                                desiredCourses = null; // Flag to abort generation
                                System.out.println("Returning to main menu...");
                            } else if (line.equalsIgnoreCase("DONE")) {
                                proceedSchedule = false;
                            } else {
                                Course adding = courses.get(line);
                                if (adding != null) {
                                    desiredCourses.add(adding);
                                    System.out.println("Course added to your desired list!");
                                } else {
                                    System.out.println("That is not a valid course.");
                                    System.out.println("");
                                }
                            }
                        }
                        
                        if (desiredCourses != null) {
                            ArrayList<Course> schedule = scheduleGenerator.generateSchedule(currentStudent, desiredCourses, courses);
                            System.out.println("A possible schedule is:");
                            for (Course course : schedule) {
                                if (course != null) {
                                    System.out.println(course.getName());
                                }
                            }
                            System.out.println("Hit ENTER to continue");
                            inputScanner.nextLine();
                        }
                        break;
                    case 6:
                        currentStudent = new Student(null, 0, null);
                        break;

                    case 7:
                        run = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose a number between 1 and 6.");
                        break;
                }
            }catch(InputMismatchException e) {
                    System.out.println("Please Enter A Number Option");
                    inputScanner.next();
            }
        }
        inputScanner.close();
    }

    /**
     * This is the main entry to the program. Here we will parse csv files to get information on classes.
     * Then, we will run a function that loops until the user quits the app.
     * 
     * Flags:
     * --courses
     * --sections
     * 
     * @param args - expects 2 csv files with appropriate flags beforehand
     */
    public static void main(String[] args) {
        //create the application file
        App application = new App(args);
        
        //call the main loop - start the app
        application.runApp();
    }
}
