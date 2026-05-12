# CS 62 Final Project - Major Progress Tracker

We have developed this major progress tracker for the CS major at Pomona College. 

Our project helps students plan their classes by showing which classes they still need to complete the CS major and suggesting an appropriate schedule for the next semester. These recommendations are based on a dataset of offered classes and their prerequisites. While the main focus is completing the CS major on time, we also include non-CS classes in the suggested schedule.

Developed by Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci for the CS 62 Final Project in Spring 2026.

## Introduction

### Need Finding

### Dataset

Our dataset is a CSV file of more than 1,000 courses offered in Fall 2026 at the Claremont Colleges, [publicly available courtesy of Hyperschedule](https://github.com/hyperschedule/hyperschedule/blob/v2/data/FA2026/course.txt). Since our schedule recommendations are not limited to CS classes, we use all of this dataset in our project.

## Main Features
Here is a high-level description of the three main features we developed. 
- The user can create an account and input the CS classes they have taken so far. They can add or remove courses later.

- Based on the completed courses, user can request a major progress check. If they haven't completed all the required classes yet, they receive a list of remaining introductory and core classes, as well as the number of electives still needed.

- The user can ask for an appropriate schedule for the next semester. They will be recommended a schedule of 4 classes which includes some CS classes they are eligible to take based on prerequisites as well as some general education classes. The 4 classes won't have any time conflicts.

For the algorithms and data structures behind these features, see [Implementation Details](#implementation-details) below.


## Instructions for Running the Code

To run the code in VSCode, first run the code in App.java. Then in the terminal, press the up button, and paste in 
```
src.App --courses data/courses.csv --sections data/sections.csv
```
and press Return/Enter. After that, follow the instructions on the terminal.

## Usage Examples

## Implementation Details
### Data Structures
### Public API
### Algorithms and Runtimes

## Authors

Tom Burton, Henry Cannon, Phineus Choi, Deniz Tanaci
