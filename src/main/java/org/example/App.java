package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(App.class.getResource("/game/menu.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.setTitle("RoboVerse Menu");
        stage.getIcons().add(new javafx.scene.image.Image(
            App.class.getResourceAsStream("/images/logo.png")
        ));
        stage.show();
    }
}