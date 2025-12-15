package org.example.ui.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Random;

import org.example.core.*;
import org.example.ui.managers.InputManager;
import org.example.ui.managers.MonsterManager;
import org.example.ui.utils.*;

public class GameController {

    @FXML private Canvas canvas;
    @FXML private VBox hudBox;
    @FXML private Label hpLabel;
    @FXML private Label goldLabel;
    @FXML private Label levelLabel;
    @FXML private Label monsterLabel;
    @FXML private Label qLabel;
    @FXML private Label eLabel;
    @FXML private Label naLabel;

    private AnimationTimer gameLoop;
    private GraphicsContext gc;
    private GameState state;
    private ImageAssets assets;

    private final int TILE_SIZE = 50;
    private final int VIEW_SIZE = 10;

    private boolean isGameOver = false;
    private int monsterFrame = 0;
    private int playerFrame = 0;
    private int goldFrame = 0;
    private long lastAnimAt = 0;

    @FXML
    public void initialize() {
        canvas.setWidth(VIEW_SIZE * TILE_SIZE);
        canvas.setHeight(VIEW_SIZE * TILE_SIZE);
        gc = canvas.getGraphicsContext2D();

        state = new GameState();
        state.map = MapGenerator.generate(state.level);

        assets = new ImageAssets();
        assets.load();

        MonsterManager.spawn(state);

        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null){
                newScene.setOnKeyPressed(this::setupInput);
                newScene.setOnMousePressed(e -> {
                    if (e.isPrimaryButtonDown()) {
                        SkillManager.useLeftClick(state, this::killMonster);
                        canvas.requestFocus();
                    }
                });
                Platform.runLater(canvas::requestFocus);
            }
        });
        startGameLoop();
    }
        
    private void setupInput(KeyEvent e) {
        InputManager.handleKey(
                e,
                state,
                this::nextLevel,
                () -> SkillManager.useQ(state),
                () -> SkillManager.useE(state)
        );
        canvas.requestFocus();
    }

    private void killMonster(Monster m) {
        m.alive = false;
        state.map.tiles[m.y][m.x] = TileType.EMPTY;
    }

    private void nextLevel() {
        state.level++;
        // reset map
        state.map = MapGenerator.generate(state.level);
        // reset player pos
        state.player.x = state.map.playerStartX;
        state.player.y = state.map.playerStartY;
        // spawn quái mới
        state.monsters.clear();
        MonsterManager.spawn(state);
        // reset portal
        state.portalSpawned = false;
        state.portalX = -1;
        state.portalY = -1;
    }

    private void startGameLoop(){
        gameLoop = new AnimationTimer(){
            @Override
            public void handle(long l) {
                update();
                render();
            }
        };
        gameLoop.start();
    }
    private void gameOver() {
        if (isGameOver) return; 
        isGameOver = true;

        SaveUtils.saveGold(state.player.gold);

        if (gameLoop != null) {
            gameLoop.stop();
        }

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/menu.fxml")
                );
                Parent root = loader.load();

                Stage stage = (Stage) canvas.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Hero Infinity");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void update() {
        MonsterManager.update(state, this::gameOver);
        updateIdleAnimation();
        checkPortal();
        HudUtils.update(state, hpLabel, goldLabel, levelLabel, monsterLabel, qLabel, eLabel, naLabel);
        state.effects.removeIf(e -> !e.isAlive());
    }

    private void updateIdleAnimation() {
        long now = System.currentTimeMillis();
        if (now - lastAnimAt > 200) {
            monsterFrame = (monsterFrame + 1) % assets.monster.length;
            playerFrame  = (playerFrame + 1) % assets.player.length;
            goldFrame = (goldFrame + 1) % assets.gold.length;
            lastAnimAt = now;
        }
    }
    private void render() {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        int camX = CameraUtils.camX(state.player, VIEW_SIZE);
        int camY = CameraUtils.camY(state.player, VIEW_SIZE);

        GameRenderer.renderMap(gc, state, camX, camY, TILE_SIZE, VIEW_SIZE, assets, goldFrame);
        GameRenderer.renderPlayer(gc, state, camX, camY, TILE_SIZE, assets, playerFrame);
        GameRenderer.renderEffects(gc, state, camX, camY, assets, TILE_SIZE);
        GameRenderer.renderMonsters(gc, state, camX, camY, TILE_SIZE, VIEW_SIZE, assets, monsterFrame);
    }
    
    private void checkPortal() {
        if (state.portalSpawned) return;

        boolean allDead = state.monsters.stream()
                .noneMatch(m -> m.alive);

        if (!allDead) return;

        Random rand = new Random();
        int x, y;

        do {
            x = rand.nextInt(Map.SIZE);
            y = rand.nextInt(Map.SIZE);
        } while (state.map.tiles[y][x] != TileType.EMPTY
                || (x == state.player.x && y == state.player.y));

        state.map.tiles[y][x] = TileType.PORTAL;
        state.portalX = x;
        state.portalY = y;
        state.portalSpawned = true;

        System.out.println("🚪 Portal spawned at " + x + "," + y);
    }
}