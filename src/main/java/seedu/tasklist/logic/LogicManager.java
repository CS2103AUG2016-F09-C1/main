package seedu.tasklist.logic;

import javafx.collections.ObservableList;
import seedu.tasklist.commons.core.ComponentManager;
import seedu.tasklist.commons.core.LogsCenter;
import seedu.tasklist.logic.commands.Command;
import seedu.tasklist.logic.commands.CommandResult;
import seedu.tasklist.logic.parser.Parser;
import seedu.tasklist.model.Model;
import seedu.tasklist.model.task.ReadOnlyTask;
import seedu.tasklist.storage.Storage;

import java.util.List;
import java.util.logging.Logger;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Parser parser;

    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.parser = new Parser();
    }

    @Override
    public CommandResult execute(String commandText) {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        Command command = parser.parseCommand(commandText);
        command.setData(model);
        return command.execute();
    }

    //@@author A0146840E
    @Override
    public ObservableList<ReadOnlyTask> getFilteredTaskList() {
        return model.getFilteredTaskList();
    }

    @Override
    public ObservableList<ReadOnlyTask> getMainFilteredTaskList() {
        return model.getMainFilteredTaskList();
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        return model.getTaskList().getTaskList();
    }

}
