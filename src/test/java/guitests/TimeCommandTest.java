package guitests;

import org.junit.Test;

//@@author A0153837X
public class TimeCommandTest extends TaskListGuiTest{
	
	@Test
    public void time(){
	    //Overdue Task
	    commandBox.runCommand("add overdue e/11112011");
	    commandBox.runCommand("list overdue");
		commandBox.runCommand("time 1");
        assertResultMessage("Task is overdue!");
		
        //Task with endTime but without endDate
        commandBox.runCommand("add meeting e/2300");
		commandBox.runCommand("time 9");
        assertResultMessage("Task has no date time specification!");
        
        //Task without endDate and endTime
        commandBox.runCommand("add meeting");
		commandBox.runCommand("time 10");
        assertResultMessage("Task has no date time specification!");
        
	}
}