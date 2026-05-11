package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class App {
    
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

                String courseCode = fields[0].trim();
                String meetingDays = fields[12].trim();
                String startTimestr = fields[14].trim();
                String room = fields[16].trim();
                String instructor = fields[17].trim();

                LocalTime startTime;
                try {
                    startTime = LocalTime.parse(startTimestr, timeFormatter);
                } catch (Exception e) {
                    startTime = LocalTime.MIDNIGHT;
                }

                Section section = new Section(meetingDays, startTime, instructor, room);

                Course course = courses.get(courseCode);
                if (course != null) {
                    course.addSection(section);
                    attached++;
                } else {
                    skipped++;
                }
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
        while (run) {
            //sign in/create account
            if (currentStudent == null) {
                System.out.println("Press 1 to create an account, 2 to sign in");
                int input = inputScanner.nextInt();
                inputScanner.nextLine();
                if (input == 1) {
                    System.out.println("Hello! Thank You For Making An Account With Us!");
                    System.out.println("Enter Email: ");
                    String userEmail = inputScanner.nextLine().trim();
                    System.out.println(userEmail);
                    System.out.println("Enter Student ID number: ");
                    int userID =  inputScanner.nextInt();
                    inputScanner.nextLine();
                    System.out.println("Enter Major: ");
                    String userMajor = inputScanner.nextLine().trim();
                    System.out.println("Enter Password: ");
                    String userPass = inputScanner.nextLine().trim();
                    Student newStudent = new Student(userEmail, userID, userMajor, userPass);
                    System.out.println("You account is now made and you are signed in!");
                    currentStudent = newStudent;
                    students.put(userEmail, newStudent);
                } else if (input == 2) {
                    System.out.println("Enter Your Email: ");
                    String line = inputScanner.nextLine().trim();
                    Student current = this.students.get(line);
                    //User does not have an account yet, prompt them to set one up
                    if (current == null){
                        System.out.println("You do not have an account yet, please set one up. ");
                        continue;
                    }
                    else{
                        System.out.println("Enter Your Password: ");
                        line = inputScanner.nextLine().trim();
                        if (line.equals(current.getPassword())){
                            currentStudent = current;
                            break;
                        }
                        else{
                            System.out.println("Wrong Password");
                        }
                    }
                }
            } else {
                System.out.println("Press 1 to add classes taken, 2 to check your major progress, 3 to get a possible schedule, and 4 to log out: ");
                int response = inputScanner.nextInt();
                inputScanner.nextLine();
                if (response == 1) {
                    boolean proceed = true;
                    System.out.println("Please Now Enter The Courses You Have Taken");
                    while(proceed){
                        System.out.println("Enter Course, or type 'STOP': ");
                        String line = inputScanner.nextLine().trim();
                        if (line.equals("STOP")){
                            proceed = false;
                        }
                        else{
                            Course adding = this.courses.get(line);
                            if (adding != null){
                                currentStudent.addCourseCompleted(adding); 
                            }
                            else{
                                System.out.println("That is not a valid course.");
                                System.out.println("");
                            }
                        }
                    }
                } else if (response == 2) {
                    currentStudent.checkMajorProgress();
                } else if (response == 3) {
                    //do phineus' work
                } else if (response == 4) {
                    currentStudent = null;
                }
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
