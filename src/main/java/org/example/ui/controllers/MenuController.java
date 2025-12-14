package org.example.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Button startButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button continueButton;

    @FXML
    private Label highScoreLabel;

    @FXML
    private void handleStartGame() {
        try {
            System.out.println("Đang tải game scene...");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/game.fxml")
            );
            Parent gameRoot = loader.load();

            Stage stage = (Stage) startButton.getScene().getWindow();
            Scene gameScene = new Scene(gameRoot);
            stage.setScene(gameScene);
            stage.setTitle("Hero Infinity - Game");
            stage.show();

            System.out.println("Game scene loaded successfully");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải game scene: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi không xác định: " + e.getMessage());
        }
    }
}
