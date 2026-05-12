# CS 62 Final Project - Major Progress Tracker

We have developed this major progress tracker for the CS major at Pomona College. 

Our project helps students plan their classes by showing which classes they still need to complete the CS major and suggesting an appropriate schedule for the next semester. These recommendations are based on a dataset of offered classes and their prerequisites. While the main focus is completing the CS major on time, we also include non-CS classes in the suggested schedule.

Developed by Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci for the CS 62 Final Project in Spring 2026.

Our writeup can be found [here](https://docs.google.com/document/d/1U_NVlNIJujsB7xpD5aeN6MF5crR_XXT_DuV9SYN1R3E/edit?usp=sharing).

## Main Features
Here is a high-level description of the three main features we developed. 
- The user can create an account and input the CS classes they have taken so far. They can add or remove courses later.

- Based on the completed courses, user can request a major progress check. If they haven't completed all the required classes yet, they receive a list of remaining introductory and core classes, as well as the number of electives still needed.

- The user can ask for an appropriate schedule for the next semester. They will be recommended a schedule of 4 classes which includes some CS classes they are eligible to take based on prerequisites as well as some general education classes. The 4 classes won't have any time conflicts.

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

## Usage Examples

## Authors

Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci
