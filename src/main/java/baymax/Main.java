package baymax;

import java.io.IOException;
import java.io.InputStream;

import baymax.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Creates Baymax's JavaFX window and connects it to the command-processing core.
 */
public class Main extends Application {
    private static final String APPLICATION_FONT_PATH = "/fonts/Orbitron.ttf";

    private Baymax baymax;

    /**
     * Loads and displays the main JavaFX scene.
     *
     * @param stage the primary JavaFX stage
     * @throws IOException if the FXML scene cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        baymax = new Baymax();
        loadApplicationFont();

        FXMLLoader fxmlLoader =
                new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setBot(baymax);
        mainWindow.showBotMessage(
                "Hello. I am Baymax, your personal task companion.\n"
                        + "I am here to keep your tasks healthy and organized.\n"
                        + "How may I assist you?");

        Scene scene = new Scene(root);
        stage.setTitle("Baymax Care Companion");
        stage.setScene(scene);
        stage.setMinWidth(420.0);
        stage.setMinHeight(600.0);
        stage.show();
    }

    private static void loadApplicationFont() throws IOException {
        try (InputStream fontStream = Main.class.getResourceAsStream(APPLICATION_FONT_PATH)) {
            if (fontStream == null || Font.loadFont(fontStream, 14.0) == null) {
                throw new IOException("Unable to load application font: " + APPLICATION_FONT_PATH);
            }
        }
    }

    /**
     * Saves tasks when the JavaFX window is closed.
     */
    @Override
    public void stop() {
        if (baymax == null) {
            return;
        }

        try {
            baymax.saveTasks();
        } catch (IOException exception) {
            System.err.println("Baymax could not save your care plan before closing.");
        }
    }
}
