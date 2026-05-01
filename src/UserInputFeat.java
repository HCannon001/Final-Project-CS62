package src;
import java.util.Scanner;

//there is no constructor or object because it is just temporary storage for the code we are going to put in app
public class UserInputFeat {
    //Scanner scanner = new Scanner(System.in);
   public static void main(String[] args) {
        // CODE 1 TO RUN WHEN USER IS SETTING UP ACCOUNT
        // I am assuming this code will run in the part of our logic where the user is creating their account
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello! Thank You For Making An Account With Us!");
        System.out.println("Enter Email: ");
        String userEmail = scanner.nextLine().trim();
        System.out.println("Enter Student ID number: ");
        int userID =  scanner.nextInt();
        System.out.println("Enter Major: ");
        String userMajor = scanner.nextLine().trim();
        Student newStudent = new Student(userEmail, userID, userMajor);
        // then we will add this new student to whatever array list we are keeping them in
        boolean stop = true;
        System.out.println("Account Made!");
        System.out.println("Please Now Enter The Courses You Have Taken");
        while(stop){
            System.out.println("Enter Course, or type 'STOP': ");
            String line = scanner.nextLine().trim();
            if (line.equals("'STOP'")){stop = false;}

            else{
                newStudent.addCourseCompleted(new Course(line)); //TODO: make a shorthand course constructor that only takes the name
            }
        }
        System.out.println("Your Account Is All Set up!");
        scanner.close();


        //CODE 2 WHERE USER HAS SET UP THEIR ACCOUNT ALREADY AND IS CHOOSING TO ADD MORE COURSES
        Scanner scanner1 = new Scanner(System.in);
        System.out.println("Enter The Courses You Would Like To Add");
        while(stop){
            System.out.println("Enter Course, or type 'STOP': ");
            String line = scanner.nextLine().trim();
            if (line.equals("'STOP'")){stop = false;}

            else{
                newStudent.addCourseCompleted(new Course(line)); //TODO: make a shorthand course constructor that only takes the name
            }
        }
        System.out.println("Your Account Is Updated!");
        scanner1.close();
   }
    
    
}
