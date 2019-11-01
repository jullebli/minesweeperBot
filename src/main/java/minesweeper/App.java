/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
import javafx.stage.Stage;
// import minesweeper.gui.GameView;
import minesweeper.gui.StartSelectView;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        StartSelectView startScreen = new StartSelectView();
        Scene scene = new Scene(startScreen.get());
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
