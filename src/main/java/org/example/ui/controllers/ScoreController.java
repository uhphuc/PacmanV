package org.example.ui.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.data.GameResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScoreController {

    @FXML
    private VBox cardsContainer;

    @FXML
    private Label highestLabel;

    @FXML
    private Label totalGamesLabel;

    @FXML
    private Label averageLabel;

    @FXML
    public void initialize() {
        loadScores();
    }

    private void loadScores() {
        cardsContainer.getChildren().clear();

        try {
            File file = new File("save/gold_history.json");
            if (!file.exists()) return;

            ObjectMapper mapper = new ObjectMapper();
            List<GameResult> history = mapper.readValue(
                    file,
                    mapper.getTypeFactory()
                            .constructCollectionType(List.class, GameResult.class)
            );

            int totalGold = 0;
            int highest = 0;

            // newest on top
            for (int i = history.size() - 1; i >= 0; i--) {
                GameResult r = history.get(i);
                cardsContainer.getChildren().add(createScoreCard(r));

                totalGold += r.gold;
                highest = Math.max(highest, r.gold);
            }

            // stats
            totalGamesLabel.setText(String.valueOf(history.size()));
            highestLabel.setText(String.valueOf(highest));
            averageLabel.setText(
                    history.isEmpty() ? "0" : String.valueOf(totalGold / history.size())
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createScoreCard(GameResult r) {
        Label goldLabel = new Label("💰 " + r.gold);
        goldLabel.setStyle(
                "-fx-text-fill: gold; -fx-font-size: 18px; -fx-font-weight: bold;"
        );

        Label timeLabel = new Label(r.getFormattedTime());
        timeLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        VBox content = new VBox(goldLabel, timeLabel);
        content.setSpacing(6);

        HBox card = new HBox(content);
        card.setPrefWidth(360);
        card.setStyle("""
            -fx-background-color: #16213e;
            -fx-padding: 15;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: #FFD700;
        """);

        return card;
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/game/menu.fxml")
        );
        Parent root = loader.load();

        Stage stage = (Stage) cardsContainer.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleClearScores() {
        try {
            File file = new File("save/gold_history.json");
            if (file.exists()) {
                file.delete();
            }
            cardsContainer.getChildren().clear();

            highestLabel.setText("0");
            totalGamesLabel.setText("0");
            averageLabel.setText("0");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
