package src;

import java.util.HashMap;

public class App {
    
    HashMap<String, Course> courses;
    HashMap<String, Student> students;
    boolean run = true;

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
                    System.out.println("The Section CSV is set to " + sectionsCSV);
                    sectionsCSV = csvFiles[i + 1];
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

        //code for parsing and adding courses values
        this.courses = new HashMap<>();
    }

    /**
     * The main loop for the program
     */
    public void runApp() {
        //place holder loop to show how the function will run on an over arching base
        while (run) {
            run = false;
        }
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
