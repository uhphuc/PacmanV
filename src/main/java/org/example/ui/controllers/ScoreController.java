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

import org.example.core.SortMode;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Button;

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
    private Button newestButton;

    @FXML
    private Button highestButton;

    private SortMode currentSort = SortMode.NEWEST;
    private LinkedHashMap<Long, Integer> scoreMap;
    private boolean isNewestActive = true;

    @FXML
    public void initialize() {
        loadScores();
        // Thiết lập trạng thái ban đầu cho các nút
        updateButtonStyles();
    }

    private void loadScores() {
        try {
            File file = new File("save/gold_history.json");
            if (!file.exists()) {
                // Nếu file không tồn tại, tạo thư mục và file mới
                file.getParentFile().mkdirs();
                scoreMap = new LinkedHashMap<>();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            scoreMap = mapper.readValue(
                    file,
                    mapper.getTypeFactory().constructMapType(
                            LinkedHashMap.class,
                            Long.class,
                            Integer.class
                    )
            );

            renderScores();

        } catch (Exception e) {
            e.printStackTrace();
            scoreMap = new LinkedHashMap<>(); // Khởi tạo map rỗng nếu có lỗi
        }
    }

    private void renderScores() {
        cardsContainer.getChildren().clear();

        if (scoreMap == null || scoreMap.isEmpty()) {
            // Hiển thị thông báo khi không có dữ liệu
            Label noDataLabel = new Label("📊 No game history yet");
            noDataLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 16px; -fx-font-weight: bold;");
            cardsContainer.getChildren().add(noDataLabel);
            return;
        }

        List<Map.Entry<Long, Integer>> entries =
                new ArrayList<>(scoreMap.entrySet());

        if (currentSort == SortMode.NEWEST) {
            // Thời gian mới nhất lên trên
            entries.sort((a, b) -> Long.compare(b.getKey(), a.getKey()));
        } else {
            // Điểm cao nhất lên trên
            entries.sort((a, b) -> {
                int scoreCompare = Integer.compare(b.getValue(), a.getValue());
                if (scoreCompare == 0) {
                    // Nếu điểm bằng nhau, sắp xếp theo thời gian mới nhất
                    return Long.compare(b.getKey(), a.getKey());
                }
                return scoreCompare;
            });
        }

        int total = 0;
        int highest = 0;

        for (int i = 0; i < entries.size(); i++) {
            var e = entries.get(i);
            long time = e.getKey();
            int gold = e.getValue();

            cardsContainer.getChildren().add(
                    createScoreCard(time, gold, i + 1)
            );

            total += gold;
            highest = Math.max(highest, gold);
        }

        totalGamesLabel.setText(String.valueOf(entries.size()));
        highestLabel.setText(String.valueOf(highest));
        averageLabel.setText(
                entries.isEmpty() ? "0" : String.valueOf(total / entries.size())
        );
    }

    private HBox createScoreCard(long time, int gold, int rank) {


        Label goldLabel = new Label("💰 " + gold);
        goldLabel.setStyle(
                "-fx-text-fill: #FFD700; -fx-font-size: 18px; -fx-font-weight: bold;"
        );

        Label timeLabel = new Label(
                Instant.ofEpochMilli(time)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        timeLabel.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 12px;");

        VBox content = new VBox(goldLabel, timeLabel);
        content.setSpacing(6);

        HBox card = new HBox(content);
        card.setPrefWidth(360);
        card.setStyle("""
            -fx-background-color: #2D3748;
            -fx-padding: 15;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: #4A5568;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
        """);

        // Thêm hiệu ứng hover
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle().replace("#2D3748", "#4A5568") +
                    " -fx-border-color: #718096;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("""
                -fx-background-color: #2D3748;
                -fx-padding: 15;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #4A5568;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
            """);
        });

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
            scoreMap = new LinkedHashMap<>();
            renderScores();

            highestLabel.setText("0");
            totalGamesLabel.setText("0");
            averageLabel.setText("0");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sortNewest() {
        if (!isNewestActive) { // SỬA: chỉ thực hiện khi không phải đang active
            isNewestActive = true;
            currentSort = SortMode.NEWEST;
            updateButtonStyles();
            renderScores();
        }
    }

    @FXML
    private void sortHighest() {
        if (isNewestActive) { // SỬA: chỉ thực hiện khi đang không phải active
            isNewestActive = false;
            currentSort = SortMode.HIGHEST;
            updateButtonStyles();
            renderScores();
        }
    }

    private void updateButtonStyles() {
        // Xóa tất cả các style class cũ
        newestButton.getStyleClass().removeAll("toggle-active", "toggle-inactive");
        highestButton.getStyleClass().removeAll("toggle-active", "toggle-inactive");

        // Thêm style class phù hợp
        if (isNewestActive) {
            newestButton.getStyleClass().add("toggle-active");
            highestButton.getStyleClass().add("toggle-inactive");
            
            // Cập nhật style inline nếu cần
            newestButton.setStyle(newestButton.getStyle() + 
                "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049);" +
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, rgba(76,175,80,0.4), 8, 0, 0, 2);");
            
            highestButton.setStyle(highestButton.getStyle() + 
                "-fx-background-color: #2D3748;" +
                "-fx-text-fill: #A0AEC0;" +
                "-fx-effect: none;");
        } else {
            highestButton.getStyleClass().add("toggle-active");
            newestButton.getStyleClass().add("toggle-inactive");
            
            highestButton.setStyle(highestButton.getStyle() + 
                "-fx-background-color: linear-gradient(to right, #FFC107, #FF9800);" +
                "-fx-text-fill: #1A202C;" +
                "-fx-effect: dropshadow(gaussian, rgba(255,193,7,0.4), 8, 0, 0, 2);");
            
            newestButton.setStyle(newestButton.getStyle() + 
                "-fx-background-color: #2D3748;" +
                "-fx-text-fill: #A0AEC0;" +
                "-fx-effect: none;");
        }
    }
}