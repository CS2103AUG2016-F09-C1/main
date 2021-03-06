package seedu.tasklist.ui;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seedu.tasklist.commons.core.LogsCenter;
import seedu.tasklist.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.tasklist.commons.util.FxViewUtil;
import seedu.tasklist.logic.Logic;
import seedu.tasklist.logic.commands.*;

import java.util.ArrayList;
import java.util.logging.Logger;

public class CommandBox extends UiPart {
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";

    private AnchorPane placeHolderPane;
    private AnchorPane commandPane;
    private ResultDisplay resultDisplay;
    private ArrayList<String> previousCommandList = new ArrayList<String>();
    private int previousCommandIndex;

    private Logic logic;

    @FXML
    private TextField commandTextField;

    public static CommandBox load(Stage primaryStage, AnchorPane commandBoxPlaceholder, ResultDisplay resultDisplay,
            Logic logic) {
        CommandBox commandBox = UiPartLoader.loadUiPart(primaryStage, commandBoxPlaceholder, new CommandBox());
        commandBox.configure(resultDisplay, logic);
        commandBox.addToPlaceholder();
        return commandBox;
    }

    public void configure(ResultDisplay resultDisplay, Logic logic) {
        this.resultDisplay = resultDisplay;
        this.logic = logic;
        registerAsAnEventHandler(this);
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(commandTextField);
        FxViewUtil.applyAnchorBoundaryParameters(commandPane, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(commandTextField, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public void setNode(Node node) {
        commandPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    @FXML
    private void handleCommandInputChanged() {
        // Take a copy of the command text
        previousCommandIndex = 0;
        previousCommandList.add(previousCommandIndex, commandTextField.getText());

        /*
         * We assume the command is correct. If it is incorrect, the command box
         * will be changed accordingly in the event handling code {@link
         * #handleIncorrectCommandAttempted}
         */
        setStyleToIndicateCorrectCommand();
        CommandResult mostRecentResult = logic.execute(previousCommandList.get(previousCommandIndex));
        resultDisplay.postMessage(mostRecentResult.feedbackToUser);
        logger.info("Result: " + mostRecentResult.feedbackToUser);
    }

    /**
     * Sets the command box style to indicate a correct command.
     */
    private void setStyleToIndicateCorrectCommand() {
        commandTextField.getStyleClass().remove("error");
        commandTextField.setText("");
    }

    @Subscribe
    private void handleIncorrectCommandAttempted(IncorrectCommandAttemptedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Invalid command: " + previousCommandList));
        setStyleToIndicateIncorrectCommand();
        restoreCommandText();
    }

    //@@author A0146840E
    /**
     * Restores the command box text to the previously entered command
     */
    private void restoreCommandText() {
        commandTextField.setText(previousCommandList.get(previousCommandIndex));
        previousCommandList.remove(previousCommandIndex);
    }

    /**
     * Select and restores the command box text to the previously entered
     * command for the Up key
     */
    public void selectPreviousCommandTextNext() {
        if (!previousCommandList.isEmpty()) {
            commandTextField.setText(previousCommandList.get(previousCommandIndex));
            if (previousCommandIndex < previousCommandList.size() - 1) {
                previousCommandIndex++;
            }
        }
    }

    /**
     * Select and restores the command box text to the previously entered
     * command for the Down key
     */
    public void selectPreviousCommandTextPrevious() {
        if (!previousCommandList.isEmpty()) {
            commandTextField.setText(previousCommandList.get(previousCommandIndex));
            if (previousCommandIndex > 0) {
                previousCommandIndex--;
            }
        }
    }

    //@@author
    /**
     * Sets the command box style to indicate an error
     */
    private void setStyleToIndicateIncorrectCommand() {
        commandTextField.getStyleClass().add("error");
    }

}
