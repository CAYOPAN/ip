package baymax;

import java.io.IOException;

import baymax.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Creates Baymax's JavaFX window and connects it to the command-processing core.
 */
public class Main extends Application {
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

        FXMLLoader fxmlLoader =
                new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setBot(baymax);
        mainWindow.showBotMessage(
                "Hello! I'm Baymax. Your personal task companion.\n"
                        + "What can I do for you?");

        Scene scene = new Scene(root);
        stage.setTitle("Baymax");
        stage.setScene(scene);
        stage.setMinWidth(420.0);
        stage.setMinHeight(600.0);
        stage.show();
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
            System.err.println("Can not save tasks list before closing Baymax.");
        }
    }
}
