

# Developer Guide




* [Setting Up](#setting-up)
* [Design](#design)
* [Implementation](#implementation)
* [Testing](#testing)
* [Dev Ops](#dev-ops)
* [Appendix A: User Stories](#appendix-a--user-stories)
* [Appendix B: Use Cases](#appendix-b--use-cases)
* [Appendix C: Non Functional Requirements](#appendix-c--non-functional-requirements)
* [Appendix D: Glossary](#appendix-d--glossary)
* [Appendix E : Product Survey](#appendix-e--product-survey)




## Setting up




### Prerequisites




1. **JDK 8** or later
2. **Eclipse** IDE
3. **e(fx)clipse** plugin for Eclipse 




### Importing the project into Eclipse




0. Fork this repo, and clone the fork to your computer
1. Open Eclipse (Note: Ensure you have installed the **e(fx)clipse plugin** as given in the prerequisites above)
2. Click `File` > `Import`
3. Click `General` > `Existing Projects into Workspace` > `Next`
4. Click `Browse`, then locate the project's directory
5. Click `Finish`




### Troubleshooting Project Setup



**Problem: Eclipse reports compile errors after new commits are pulled from Git**
* Reason: Eclipse fails to recognize new files that appeared due to the Git pull. 
* Solution: Refresh the project in Eclipse:<br> 
  Right click on the project (in Eclipse package explorer), choose `Gradle` -> `Refresh Gradle Project`.
  

**Problem: Eclipse reports some required libraries missing**
* Reason: Required libraries may not have been downloaded during the project import. 
* Solution: [Run tests using Gradle](UsingGradle.md) once (to refresh the libraries).




<!-- @@author A0140019W -->
## Design




### Architecture
<img src="images/Architecture.png" width="900"/> <br><br><br>
The **_Architecture Diagram_** given above explains the high-level design of the App.
Given below is a quick overview of each component.



`Main` has only one class called [`MainApp`](../src/main/java/seedu/tasklist/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connect them up with each other.
* At shut down: Shuts down the components and invoke cleanup method where necessary.



[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.
Two of those classes play important roles at the architecture level.
* `EventsCenter` : This class (written using [Google's Event Bus library](https://github.com/google/guava/wiki/EventBusExplained))
  is used by components to communicate with other components using events (i.e. a form of _Event Driven_ design)
* `LogsCenter` : Used by many classes to write log messages to the App's log file.



The rest of the App consists four components.
* [**`UI`**](#ui-component) : The UI of the App.
* [**`Logic`**](#logic-component) : The command executor.
* [**`Model`**](#model-component) : Holds the data of the App in-memory.
* [**`Storage`**](#storage-component) : Reads data from, and writes data to, the hard disk.




Each of the four components
* Defines its _API_ in an `interface` with the same name as the Component.
* Exposes its functionality using a `{Component Name}Manager` class.




For example, the `Logic` component (see the class diagram given below) defines it's API in the `Logic.java`
interface and exposes its functionality using the `LogicManager.java` class.<br>
<img src="images/LogicClassDiagram.png" width="900"/> <br><br><br>


### Events-Driven nature of the design 


###Delete Command
The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `delete 1`.


<img src="images\SDforDeleteTask.png" width="800">




> Note how the `Model` simply raises a `TaskListChangedEvent` when the Task List data are changed, instead of asking the `Storage` to save the updates to the hard disk.



The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\SDforDeleteTaskEventHandling.PNG" width="800">




> Note how the event is propagated through the `EventsCenter` to the `Storage` and `UI` without `Model` having
  to be coupled to either of them. This is an example of how this Event Driven approach helps us reduce direct coupling between components.


###Add Command
The Sequence Diagram below shows how the components interact for the scenario where the user issues the command  `add CS2103`.


<img src="images\addSequence.png" width="800">



The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\addSequenceEvent.png" width="800">




###Mark/Unmark Command
The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `mark 1/unmark 1`.


<img src="images\markunmarkSequence.png" width="800">


The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\markunmarkEventSequence.png" width="800">


###Storage Command
The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `storage docs/tasklist.xml`.


<img src="images\storageSequence.png" width="800">

The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\storageSequenceEvent.png" width="800">


###Select Command


The Sequence Diagram below shows how the components interact for the scenario where the user issues the command  `select 1`.


<img src="images\SelectEventSequence.png" width="800">


###Exit Command

The Sequence Diagram below shows how the components interact for the scenario where the user issues the command  `exit`.


<img src="images\exitSequence.png" width="800">



The sections below give more details of each component.


###Help Command
The Sequence Diagram below shows how the components interact for the scenario where the user issues the command  `help`.


<img src="images\HelpSequence.png" width="800">


###Invalid Command
The Sequence Diagram below shows how the components interact for the scenario where the user issues the command  `adds CS2103`.


<img src="images\InvalidCommandSequence.png" width="800">




The sections below give more details of each component.



### UI component




<img src="images/UiClassDiagram.png" width="800"><br>



**API** : [`Ui.java`](../src/main/java/seedu/tasklist/ui/Ui.java)




The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `TaskListPanel`,
`StatusBarFooter`, etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class and they can be loaded using the `UiPartLoader`.



The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files
 that are in the `src/main/resources/view` folder.<br>
 For example, the layout of the [`MainWindow`](../src/main/java/seedu/tasklist/ui/MainWindow.java) is specified in
 [`MainWindow.fxml`](../src/main/resources/view/MainWindow.fxml)



The `UI` component,
* Executes user commands using the `Logic` component.
* Binds itself to some data in the `Model` so that the UI can auto-update when data in the `Model` change.
* Responds to events raised from various parts of the App and updates the UI accordingly.




### Logic component




<img src="images/LogicClassDiagram.png" width="800"><br>



**API** : [`Logic.java`](../src/main/java/seedu/tasklist/logic/Logic.java)



1. `Logic` uses the `Parser` class to parse the user command.
2. This results in a `Command` object which is executed by the `LogicManager`.
3. The command execution can affect the `Model` (e.g. adding a tasks) and/or raise events.
4. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.



Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")`
 API call.<br>
<img src="images/DeleteTaskSdForLogic.png" width="800"><br>

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("add CS2103")`
 API call.<br>
<img src="images/addTaskSdForLogic.png" width="800"><br>

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("mark 1")`
 API call.<br>
<img src="images/markTaskSdForLogic.png" width="800"><br>

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("clear")`
 API call.<br>
<img src="images/clearTaskSdForLogic.png" width="800"><br>


### Model component



<img src="images/ModelClassDiagram.png" width="800"><br>



**API** : [`Model.java`](../src/main/java/seedu/tasklist/model/Model.java)



The `Model`,
* stores a `UserPref` object that represents the user's preferences.
* stores the Task List data.
* exposes a `UnmodifiableObservableList<ReadOnlyTask>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.




### Storage component



<img src="images/StorageClassDiagram.png" width="800"><br>



**API** : [`Storage.java`](../src/main/java/seedu/tasklist/storage/Storage.java)



The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save the Task List data in xml format and read it back.




### Common classes



Classes used by multiple components are in the `seedu.tasklist.commons` package.




### Object Oriented Domain Model




<img src="images/oodm.png" width="800"><br>




<!-- @@author -->
## Implementation



### Logging



We are using `java.util.logging` package for logging. The `LogsCenter` class is used to manage the logging levels and logging destinations.




* The logging level can be controlled using the `logLevel` setting in the configuration file
  (See [Configuration](#configuration))
* The `Logger` for a class can be obtained using `LogsCenter.getLogger(Class)` which will log messages according to
  the specified logging level
* Currently log messages are output through: `Console` and to a `.log` file.



**Logging Levels**



* `SEVERE` : Critical problem detected which may possibly cause the termination of the application
* `WARNING` : Can continue, but with caution
* `INFO` : Information showing the noteworthy actions by the App
* `FINE` : Details that is not usually noteworthy but may be useful in debugging
  e.g. print the actual list instead of just its size



### Configuration




Certain properties of the application can be controlled (e.g App name, logging level) through the configuration file 
(default: `config.json`):




## Testing



Tests can be found in the `./src/test/java` folder.



**In Eclipse**:
* To run all tests, right-click on the `src/test/java` folder and choose
  `Run as` > `JUnit Test`
* To run a subset of tests, you can right-click on a test package, test class, or a test and choose
  to run as a JUnit test.



**Using Gradle**:
* See [UsingGradle.md](UsingGradle.md) for how to run tests using Gradle.




We have two types of tests:



1. **GUI Tests** - These are _System Tests_ that test the entire App by simulating user actions on the GUI. These are in the `guitests` package.


2. **Non-GUI Tests** - These are tests not involving the GUI. They include,
   1. _Unit tests_ targeting the lowest level methods/classes. <br>
      e.g. `seedu.tasklist.commons.UrlUtilTest`
   2. _Integration tests_ that are checking the integration of multiple code units 
     (those code units are assumed to be working).<br>
      e.g. `seedu.tasklist.storage.StorageManagerTest`
   3. Hybrids of unit and integration tests. These test are checking multiple code units as well as 
      how the are connected together.<br>
      e.g. `seedu.tasklist.logic.LogicManagerTest`
  

#### Headless GUI Testing



Thanks to the [TestFX](https://github.com/TestFX/TestFX) library we use,
 our GUI tests can be run in the _headless_ mode. 
 In the headless mode, GUI tests do not show up on the screen.
 That means the developer can do other things on the Computer while the tests are running.<br>
 See [UsingGradle.md](UsingGradle.md#running-tests) to learn how to run tests in headless mode.
 

#### Troubleshooting tests



 **Problem: Tests fail because NullPointException when AssertionError is expected**
 * Reason: Assertions are not enabled for JUnit tests. 
   This can happen if you are not using a recent Eclipse version (i.e. _Neon_ or later)
 * Solution: Enable assertions in JUnit tests as described 
   [here](http://stackoverflow.com/questions/2522897/eclipse-junit-ea-vm-option). <br>
   Delete run configurations created when you ran tests earlier.
  

## Dev Ops



### Build Automation



See [UsingGradle.md](UsingGradle.md) to learn how to use Gradle for build automation.



### Continuous Integration



We use [Travis CI](https://travis-ci.org/) to perform _Continuous Integration_ on our projects.
See [UsingTravis.md](UsingTravis.md) for more details.




### Code Review




We use [Codacy](https://www.codacy.com/) to perform _Code Review_ on our projects.



### Making a Release



Here are the steps to create a new release.
 

 1. Generate a JAR file [using Gradle](UsingGradle.md#creating-the-jar-file).
 2. Tag the repo with the version number. e.g. `v0.1`
 2. [Create a new release using GitHub](https://help.github.com/articles/creating-releases/) 
    and upload the JAR file you created.
   

### Managing Dependencies



A project often depends on third-party libraries. For example, Task List depends on the
[Jackson library](http://wiki.fasterxml.com/JacksonHome) for XML parsing. Managing these _dependencies_
can be automated using Gradle. For example, Gradle can download the dependencies automatically, which is better than these alternatives.<br>
a. Include those libraries in the repo (this bloats the repo size)<br>
b. Require developers to download those libraries manually (this creates extra work for developers)<br>




<!-- @@author A0146840E -->
## Appendix A : User Stories




Priorities: High (must have) - `* * *`, Medium (nice to have)  - `* *`,  Low (unlikely to have) - `*`




Priority | As a ... | I want to ... | So that I can...
-------- | :-------- | :--------- | :-----------
`* * *` | new user | see usage instructions | refer to instructions when I forget how to use the App
`* * *`| new user | set my storage file location | use a cloud syncing services to access data from multiple computers
`* * *`| user | create a task | keep track of my new tasks; may include date and/or time
`* * *`| user | find a task by name | locate details of tasks without having to go through the entire list
`* * *`| user | find a task by dates | locate details of tasks without having to go through the entire list
`* * *`| user | find a task by description | locate details of tasks without having to go through the entire list
`* * *`| user | find a task by labels | locate details of tasks without having to go through the entire list
`* * *`| user | edit the tasks | updates the changes of the task
`* * *`| user | delete a tasks | remove entries that I no longer need
`* * *`| user | list my tasklist | know the uncompleted task for today/week/all
`* * *`| user | undo my previous commands | undo my mistakes
`* * *`| user | redo the previous commands | redo similar commands
`* * *`| user | display the number of tasks completed or left | keep track of my progress
`* * *`| user | mark the task as done | know which tasks are completed
`* * * `| user | clear all the task | start a totally new task list
`* * * `| user | exit the application | close the application 
` * *`| user | know how much time do I have before a deadline | don't need to calculate manually how much do I have left
`* * `| new user | set my username | regard the app as my own
`* * `| user | hide private tasks by default | minimize chance of someone else seeing them by accident
`* * `| user | change the command words | make it more intuitive
`*  `| user | lock task list | prevent unauthorised access
`* `| user | check for time collision  | be pre-empted for stacked events
`* `| user | change the task list's theme | personalise the look of the application




<!-- @@author A0138516A -->
## Appendix B : Use Cases




(For all use cases below, the **System** is the `TaskList` and the **Actor** is the `user`, unless specified otherwise)




### Use case: `List tasks`




**MSS**




1. User requests to list tasks
2. TaskList shows a list of tasks
Use case ends.




**Extensions**




2a. The list is empty




> Use case ends




### Use case: `Add task`




**MSS**




1. User requests to add task
2. TaskList add the task 
Use case ends.




**Extensions**




2a. Invalid parameters




 > 2a1.TaskList shows an error message
 > 2a2. Use case ends




### Use case: `Find tasks`




**MSS**




1. User requests to find tasks
2. TaskList shows a list of tasks
Use case ends.




**Extensions**




2a. The list is empty




> Use case ends




### Use case: `Edit task`




**MSS**




1. User requests to list tasks
2. TaskList shows a list of tasks
3. User requests to edit a specific task in the list
4. TaskList edit the task
Use case ends.




**Extensions**




2a. The list is empty




> Use case ends




3a. The given index is invalid




> 3a1. TaskList shows an error message <br>
  Use case resumes at step 2




4a. Invalid command




 > 4a1.TaskList shows an error message
 > 4a2. Use case ends




### Use case: `Delete task`




**MSS**




1. User requests to list tasks
2. TaskList shows a list of tasks
3. User requests to delete a specific task in the list
4. TaskList deletes the task 
Use case ends.




**Extensions**




2a. The list is empty




> Use case ends




3a. The given index is invalid




> 3a1. TaskList shows an error message <br>
  Use case resumes at step 2




### Use case: `Undo task`




**MSS**




1. User requests to undo the previous command
2. TaskList shows the list of task before the last command
Use case ends.




**Extensions**




2a. There is no previous command




> Use case ends




### Use case: `Set storage file path`




**MSS**




1. User requests to change the storage file path
2. TaskList original file path change to new file path
3. TaskList delete old storage file




> Use case ends


### Use case: `Check time remaining for a task`




**MSS**




1. User requests to check the time remaining for specified task
2. TaskList show the time remaining for that task


> Use case ends






### Use case: `Mark the task as complete`




**MSS**




1. User requests to list tasks
2. TaskList shows a list of tasks
3. User requests to mark a specific task as completed
4. TaskList update the task as completed
Use case ends.




**Extensions**




2a. The list is empty




> Use case ends




3a. Task is already marked
> 3a1. TaskList shows an error message <br>
  Use case resumes at step 2




> Use case ends




### Use case: `Unmark a completed task`




**MSS**




1. User requests to list tasks
2. TaskList shows a list of tasks
3. User requests to mark specific task to be not completed
4. TaskList update the task as not completed
Use case ends.




**Extensions**




2a. The list is empty




> Use case ends




3a. Task is not marked
> 3a1. TaskList shows an error message <br>
  Use case resumes at step 2




> Use case ends




## Appendix C : Non Functional Requirements




1. Should work on any [mainstream OS](#mainstream-os) as long as it has Java 8 or higher installed.
2. Should be able to hold up to 1000 tasks.
3. Should come with automated unit tests and open source code.
4. Should favor DOS style commands over Unix-style commands.
5. Should be intuitive for users to use the commands and user interface
6. Should respond to the user in less than 0.5 seconds
7. [Constraints] (http://www.comp.nus.edu.sg/~cs2103/AY1617S1/contents/handbook.html#handbook-project-constraints)




## Appendix D : Glossary




### Mainstream OS




> Windows, Linux, Unix, OS-X




<!-- @@author A0153837X -->
## Appendix E : Product Survey




### Product Name: Todoist
* Description: To Do List and Task Manager
* Product Review:
    * Strengths:
        * Easy to add, view and organize tasks.
        * Tasks with deadlines and recurring dates.
        * Focus on important tasks with priority levels.
    * Weaknesses:
        * Tasks are not auto sorted by dates, priorities or names.




### Product Name: Todo.Txt
* Description: Simple to-do list, operated through commandline interface
* Product Review:
    * Strengths:
        * While simple, the application has 3 main features that make it useful.
        * Priority: to sort to do list based on relative importance.
        * Project: to categorise tasks.
        * Context: to insert comments and additional information about the item.
    * Weaknesses:
        * Users unfamiliar with commandline tools will be intimidated to use it.
        * Not too user-friendly as using the application is not intuitive; users may need to read the documentation to              fully understand the app.


### Product Name: Fantastical
* Description: Calendar to-do-list app
* Product Review:
    * Strengths:
        * Clean design, works well with different types of calendar display.
        * Group multiple calendars together from multiple devices.
    * Weaknesses:
        * Some users find the many features too distracting and seldom use them outside of the main ones.




### Product Name: BusyCal3
* Description: To-do list and calendar
* Product Review:
    * Strengths:
        * SmartFilter: allows users to customise what tasks to be shown and also by what order of priority
        * Users can create multiple filters and switch between them
        * Mini calendar widget
    * Weaknesses:
        * Some users face integration error between devices.




