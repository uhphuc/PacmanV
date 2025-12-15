package org.example.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.example.data.GameResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MenuController {
    @FXML
    private Button startButton;

    @FXML 
    private Button scoreButton;

    @FXML
    private Label highScoreLabel;

    @FXML
    private void handleStartGame() {
        try {
            System.out.println("Loading game scene...");

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
            System.err.println("Error load game scene: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error Unexpected: " + e.getMessage());
        }
    }
    @FXML
    private void handleShowScores() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/score.fxml")
            );
            Parent scoreRoot = loader.load();

            Stage stage = (Stage) scoreButton.getScene().getWindow();
            Scene scoreScene = new Scene(scoreRoot);
            stage.setScene(scoreScene);
            stage.setTitle("Hero Infinity - Scores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading score scene: " + e.getMessage());
        }
    }
    @FXML
    public void initialize() {
        loadHighScore();
    }

    private void loadHighScore() {
        try {
        File file = new File("save/gold_history.json");
        if (!file.exists()) {
            highScoreLabel.setText("High Score: 0");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        LinkedHashMap<Long, Integer> map = mapper.readValue(
                file,
                mapper.getTypeFactory()
                        .constructMapType(
                                LinkedHashMap.class,
                                Long.class,
                                Integer.class
                        )
        );
        int maxGold = map.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        highScoreLabel.setText("High Score: " + maxGold);

        } catch (Exception e) {
            e.printStackTrace();
            highScoreLabel.setText("High Score: ?");
        }
    }
}
