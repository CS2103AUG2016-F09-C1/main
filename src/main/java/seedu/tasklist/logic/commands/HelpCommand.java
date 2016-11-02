package seedu.tasklist.logic.commands;


import seedu.tasklist.commons.core.EventsCenter;
import seedu.tasklist.commons.events.ui.ShowHelpRequestEvent;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    public HelpCommand() {}

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new ShowHelpRequestEvent());
        return new CommandResult(SHOWING_HELP_MESSAGE);
    }

    //@@author A0146840E
    /**
     * Parses arguments in the context of the help command.
     *
     * @param args should be empty
     * @return the prepared command
     */
    @Override
    public Command prepare(String args) {
        return new HelpCommand();
    }
}
