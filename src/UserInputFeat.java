package src;
import java.util.Scanner;

//there is no constructor or object because it is just temporary storage for the code we are going to put in app
public class UserInputFeat {

  
    
   public static void main(String[] args) {


    




        // CODE 1 TO RUN WHEN USER IS SETTING UP ACCOUNT
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello! Thank You For Making An Account With Us!");
        System.out.println("Enter Email: ");
        String userEmail = scanner.nextLine().trim();
        System.out.println("Enter Student ID number: ");
        int userID =  scanner.nextInt();
        System.out.println("Enter Major: ");
        String userMajor = scanner.nextLine().trim();
        System.out.println("Enter Password: ");
        String userPass = scanner.nextLine().trim();
        Student newStudent = new Student(userEmail, userID, userMajor, userPass);
        // then we will add this new student to whatever array list we are keeping them in
        System.out.println("Account Made!");
        scanner.close();
        
        //CODE FOR LOGGING USERS IN
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("Enter Email ");
        String line = scanner1.nextLine().trim();
        Student current = this.students.get(line);
            //User does not have an account yet, prompt them to set one up
            if (current == null){
                System.out.println("You do not have an account yet, please set one up. ");
                //code from above for creating an account
                loggedIn = true;
            }
            else{
                System.out.println("enter password ");
                line = scanner1.nextLine().trim();
                if (line.equals(current.getPassword())){
                    loggedIn = true;
                }
                else{
                    System.out.println("Wrong Password");
                    //call login func again from here so they don';t have to re input username
                }

            }

        

        


        //FEATURE WHERE USER IS CHOOSING TO ADD MORE COURSES
        Scanner scanner1 = new Scanner(System.in);
        boolean proceed = true;
        System.out.println("Please Now Enter The Courses You Have Taken");
        while(proceed){
            System.out.println("Enter Course, or type 'STOP': ");
            String line = scanner1.nextLine().trim();
            if (line.equals("STOP")){
                proceed = false;
            }
            else{
                Course adding = this.courses.get(line);
                if (adding != null){
                    newStudent.addCourseCompleted(adding); 
                }
                else{
                    System.out.println("That is not a valid course.");
                    System.out.println("");
                }
            }
        }
        System.out.println("Your Account Is Updated!");
        scanner1.close();


        //CODE TO LOG USER IN
        boolean loggedIn = false;
        //run all of our features in here
        //add feature to "log out"

        if (loggedIn){


        
        }
        // Send user to log in screen
        else{
            


        }












    }
    
    
}
