# CS 62 Final Project - Major Progress Tracker

We have developed this major progress tracker for the CS major at Pomona College. 

Our project helps students plan their classes by showing which classes they still need to complete the CS major and suggesting an appropriate schedule for the next semester. These recommendations are based on a dataset of offered classes and their prerequisites. While the main focus is completing the CS major on time, we also include non-CS classes in the suggested schedule.

Developed by Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci for the CS 62 Final Project in Spring 2026.

Our writeup can be found [here](./CS62FinalWriteUp.pdf).

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
<details>
<summary>The following are a descripton of each method in Course and an example call:</summary>

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

#### `getId()`:
This function returns the Id of a course
```java
getId();
-> String id
```

### `addMajor()`:
This function adds a major to the array of majors
```java
addMajor("some major");
```

#### `getName()`:
This function returns the Id of a course
```java
getName();
-> String name
```

#### `equals()`:
This funciton checks all the elements of course and if they are all equal it returns true
```java
Course course = new course("id", "name", "description")
course.equals(new course("id", "name", "description"))
-> true
```

#### `toString()`:
This function outputs the name of the course as the string
```java
Course course = new Course("id", "name", "description")
course.toString()
-> "name"
```
</details>

### Section.java
`Section.java` is responsible for storing the data for each section of a course while the app is running. It stores the following data about sections: days, startTime, endTime, professor, and location.

The constructor takes in five elements: days, startTime, endTime, professor, and location. An example of calling this would be the following:
```java
String days = "MWF";
LocalTime startTime = LocalTime.of(9, 0);
LocalTime endTime = LocalTime.of(10, 0);
String professor = "Dr. Smith";
String location = "Building A";
Section section = new Section(days, startTime, endTime, professor, location);
```
<details>
<summary>The following are a description of each method in Section and an example call:</summary>

#### `setDays()`:
This sets the days the section is offered.
```java
setDays("MWF");
```

#### `getDays()`:
This returns the days the section is offered.
```java
getDays();
-> String days
```

#### `setStartTime()`:
This sets the start time of the section.
```java
setStartTime(LocalTime.of(9, 0));
```

#### `getStartTime()`:
This returns the start time of the section.
```java
getStartTime();
-> LocalTime startTime
```

#### `setEndTime()`:
This sets the end time of the section.
```java
setEndTime(LocalTime.of(10, 0));
```

#### `getEndTime()`:
This returns the end time of the section.
```java
getEndTime();
-> LocalTime endTime
```

#### `setLocation()`:
This sets the location where the section is held.
```java
setLocation("Building A");
```

#### `getLocation()`:
This returns the location where the section is held.
```java
getLocation();
-> String location
```

#### `setProfessor()`:
This sets the professor teaching the section.
```java
setProfessor("Dr. Smith");
```

#### `getProfessor()`:
This returns the professor teaching the section.
```java
getProfessor();
-> String professor
```

</details>

### CourseDataBuilder.java
The only public function that is apart `CourseDataBuilder.java` is the main funciton. To run this class, you must have a data folder that contains the following files from hyper schedule's Github: `alt-staff.json`, `calendar-session-section.json`, `calendar-session.json`, `course-area.json`, `course-areas-description.json`, `course-section-schedule.json`, `course-section.json`, `course.txt`, `per,-count.json`, `section-instructor.json`, `staff.json`.

The class then outputs two files, `courses.csv` and `sections.csv`. `courses.csv` is a csv file that organizes the data in the following way: courseCode, courseName, college, areaCode, cipCode, courseAreas, description. `sections.csv` is a csv file that organizes the data in the following way: courseCode, sectionId, sectionNumber, semesterId, sessionId, beginDate, endDate, creditHours, capacity, currentEnrollment, status, permCount,  meetingDays,meetingDaysRaw, startTime, endTime, room, instructors

### CourseInterface.java
This is an interface for a course object. The functions that are apart of the interface are: `addPreReq`, `getPreReq`, `addSection`, `getSection`, `getSections`, `setDescription`, `getDescription`, `addMajor`, and `getMajor`.

### ScheduleGenerator.java
`ScheduleGenerator.java` is responsible for generating a 4-course schedule for a student based on their desired courses, completed courses, prerequisites, and section time availability. It fills any remaining slots randomly from the course catalog, avoiding labs and varsity sports.

The class has no constructor and is used by instantiating it and calling `generateSchedule()`. An example of calling this would be the following:
```java
ScheduleGenerator generator = new ScheduleGenerator();
ArrayList<Course> schedule = generator.generateSchedule(student, desiredCourses, allCourses);
```

Additionally, there is only one public class:

#### `generateSchedule()`:
This generates a schedule of up to 4 main courses for the given student. It first tries to add each desired course, then randomly fills remaining slots from the full course catalog. Labs and varsity sports are skipped during random filling. Returns the final schedule as an ArrayList of Course objects.
```java
ArrayList<Course> schedule = generator.generateSchedule(student, desiredCourses, allCourses);
-> ArrayList<Course> schedule
```

### SectionInterface.java
This is an interface for a section object. The functions that are apart of the interface are: `setStartTime`, `getStartTime`, `setEndTime`, `getEndTime`, `getLocation`, `setLocation`, `setProfessor`, and `getProfessor`.

### Student.java
This file does not have any public functions. Our intention is that this object is used to store classes for a temporary user that gets controlled by `App.java`.

### runs.sh
This is a bash file that is used to easily run the app. Simply type `./run.sh` in the root of the project and the project will run `App.java` with the proper inputs.

## Authors

Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci
