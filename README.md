# CS 62 Final Project - Major Progress Tracker

We have developed this major progress tracker for the CS major at Pomona College. 

Our project helps students plan their classes by showing which classes they still need to complete the CS major and suggesting an appropriate schedule for the next semester. These recommendations are based on a dataset of offered classes and their prerequisites. While the main focus is completing the CS major on time, we also include non-CS classes in the suggested schedule.

Developed by Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci for the CS 62 Final Project in Spring 2026.

Our writeup can be found [here](https://docs.google.com/document/d/1U_NVlNIJujsB7xpD5aeN6MF5crR_XXT_DuV9SYN1R3E/edit?usp=sharing).

## Main Features
Here is a high-level description of the three main features we developed. 
- The user can create an account and input the CS classes they have taken so far. They can add or remove courses later. When adding or removing courses we first verify that the course they are referring to actually exists. If it does not, we do not change their courses taken list, and we output: "invalid course". If it does exist, and they have either taken it or not taken it respectively, we also change nothing. If it does exist and is valid to be removed/added we update their courses completed list.

- Based on the completed courses, user can request a major progress check. If they haven't completed all the required classes yet, they receive a list of remaining introductory and core classes, as well as the number of electives still needed.

- The user can input courses they intend to take and be given an appropriate schedule for the next semester. They will be recommended a schedule of 4 classes they are eligible to take based on prerequisites. The courses they intend to take will only be allowed if they meet all relevant prerequisites. Furthermore, the 4 classes won't have any time conflicts.

For the algorithms and data structures behind these features, see [Implementation Details](#implementation-details) below.


## Instructions for Running the Code

We are providing you with two ways to run our project:
 1) Through the executable `run.Jar` (Recommended)
 2) Manually through the terminal in VSCode

### Running the Executable (Recommended)

Enter `./run.sh` in the terminal. After that, follow the instructions on the terminal.

### Running through the terminal in VSCode

Open `App.java` in `src`, and run the code. Then in the terminal, press the up button, and paste in 

```
src.App --courses data/courses.csv --sections data/sections.csv
```

and press Return/Enter. After that, follow the instructions on the terminal.

## Public API

Our code is supposed to be run through `App.java`, but if someone wanted to use our API in their own program, below are the most important methods they may use.

### App.java
The first thing to use the App.java file would be to create an object using the constructor. In our app, we do so in the main loop. An example to set this up would be:
```java
String[] args = {"--courses", "data/courses.csv", "--sections", "data/sections.csv"};
App app = new App();
```
The order of which inputs go does not matter as long as the flag comes before the file path to its respective data. To work the data must be in the same format as outlined in the output of `CourseDataBuilder.java`.

Once you have an app object, you can then run the other public function
```java
runApp();
```
This app takes no inputs and will then run the app as intended, outputting into the terminal. It stays in the loop until the user selects to exit the program.

### Course.java

`Course.java` is responsible for maintaining the data we access for each course while the app is running. It stores the following data about classes: id, name, description, a list of cooresponding majors, a list of cooresponding sections, and a list of cooresponding pre-reqs.

The constructor of the class takes in three elements: the id, the name, and the description. An example of calling this would be the following:
```java
String id = "123";
String name = "name";
String description = "a new course";
Course course = new Course(id, name, description);
```
The following are a descripton of each method in Course and an example call:

#### `addPreReq()`:
This adds a new prereq to the arrayList of prereqs.
```java
addPreReq(new Course(id, name, course));
```

#### `getPreReq()`:
This retrieves all the prereqs for a course.
```java
getPreReq();
-> ArrayList<Course> preReqs
```

#### `addSection()`:
This function adds a section object to the arraylist of sections
```java
addSectin(new Section(days, startTime, endTime, professor, location));
```

#### `getSection()`:
This function gets a section at a given integer index
```java
getSection(i);
-> Section section
```

#### `getSection()`:
This function returns the arraylist of sections
```java
getSections();
-> ArrayList<Section> sections
```

#### `setDiscription()`:
This function sets the description as the inputted String.
```java
String description;
setDiscription(description);
```

#### `getDiscription()`:
This function returns the description of a course
```java
getDiscription();
-> String description
```



### Printing remaining CS courses needed

After you create a student, call it `currentStudent`, and inserted the classes they have taken, you can call the void function  `currentStudent.checkMajorProgress();` to print what other courses are still required to complete the CS major. 

The printed output will include any introductory or core class and the number of electives still needed.

## Usage Examples

## Authors

Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci
