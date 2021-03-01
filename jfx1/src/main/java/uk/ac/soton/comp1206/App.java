package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * JavaFX Perlin Noise thingy??
 */
public class App extends Application {

    Stage window;

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();



        Stage window = new Stage();
        window.setTitle("JavaFX " + javafxVersion + ", running on Java " + javaVersion);
        var label = new Label("Hello");

        Button button = new Button("Press me nerd");
        button.setOnAction(e -> {
            //AlertBox.display("Title", "O shit :o");

        });

        //Layout
        //Layout or Bottom bar
        HBox bottomBar = new HBox(10);
        bottomBar.getChildren().addAll(label, button);

        //Layout for the grid of blocks
        var blockGrid = new GridPane();


        //Layout for entire window
        var layout = new BorderPane();
        //layout.getChildren().addAll(blockGrid, bottomBar);
        layout.setCenter(blockGrid);
        layout.setBottom(bottomBar);

        var scene = new Scene(layout, 640, 480);
        window.setScene(scene);
        window.show();
    }

    public static void main(String[] args) {
        launch();
    }

}