package baymax.ui;

import java.io.IOException;

import baymax.Baymax;
import baymax.Baymax.CommandResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Controls Baymax's JavaFX chat window.
 */
public class MainWindow extends AnchorPane {
    private static final int AVATAR_SIZE = 56;
    private static final double AVATAR_RADIUS = 25.0;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Baymax bot;
    private Image userImage;
    private Image baymaxImage;

    /**
     * Initializes scrolling and the generated user and Baymax avatars.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow.fxml should inject scrollPane.";
        assert dialogContainer != null : "MainWindow.fxml should inject dialogContainer.";
        assert userInput != null : "MainWindow.fxml should inject userInput.";
        assert sendButton != null : "MainWindow.fxml should inject sendButton.";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userImage = createAvatar(Color.DODGERBLUE);
        baymaxImage = createAvatar(Color.CRIMSON);
    }

    /**
     * Sets the Baymax instance used to process commands.
     *
     * @param bot the shared Baymax command processor
     */
    public void setBot(Baymax bot) {
        assert bot != null : "MainWindow should receive Baymax before processing input.";
        this.bot = bot;
    }

    /**
     * Displays an initial message from Baymax.
     *
     * @param message the message to display
     */
    public void showBotMessage(String message) {
        assert dialogContainer != null : "Dialog container should be initialized before display.";
        assert baymaxImage != null : "Baymax avatar should be initialized before display.";

        dialogContainer.getChildren().add(
                DialogBox.getBaymaxDialog(message, baymaxImage));
    }

    /**
     * Processes text submitted through the input field or Send button.
     */
    @FXML
    private void handleUserInput() {
        assert bot != null : "Baymax should be set before handling user input.";
        assert userImage != null : "User avatar should be initialized before display.";
        assert baymaxImage != null : "Baymax avatar should be initialized before display.";

        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(
                DialogBox.getUserDialog(input, userImage));

        CommandResponse response = bot.processCommand(input);
        dialogContainer.getChildren().add(
                DialogBox.getBaymaxDialog(response.message(), baymaxImage));
        userInput.clear();

        if (response.shouldExit()) {
            saveAndDisableInput();
        }
    }

    private void saveAndDisableInput() {
        try {
            bot.saveTasks();
        } catch (IOException exception) {
            dialogContainer.getChildren().add(DialogBox.getBaymaxDialog(
                    "Can not save tasks list. Previous tasks list can not be retrieve.",
                    baymaxImage));
        }
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }

    private Image createAvatar(Color color) {
        assert color != null : "Avatar creation should receive a color.";

        WritableImage avatar = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        PixelWriter pixelWriter = avatar.getPixelWriter();
        double center = (AVATAR_SIZE - 1) / 2.0;

        for (int y = 0; y < AVATAR_SIZE; y++) {
            for (int x = 0; x < AVATAR_SIZE; x++) {
                double distanceFromCenter = Math.hypot(x - center, y - center);
                pixelWriter.setColor(
                        x,
                        y,
                        distanceFromCenter <= AVATAR_RADIUS
                                ? color
                                : Color.TRANSPARENT);
            }
        }

        return avatar;
    }
}
