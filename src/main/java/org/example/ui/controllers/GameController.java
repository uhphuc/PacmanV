package org.example.ui.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import org.example.core.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;


public class GameController {

    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private GameState state;

    private final int TILE_SIZE = 50;
    private final int VIEW_SIZE = 10;
    private final Set<KeyCode> pressedKey = new HashSet<>();

    private static final int FRAME_W = 64;
    private static final int FRAME_H = 64;
    private static final int COLS = 4;
    private static final int ROWS = 4;
    private int frameIndex = 0;
    private long lastFrameTime = 0;
    private static final int MAX_MONSTERS = 10;
    private static final int IDLE_RANGE = 3;



    private Image[] monster;
    private Image monsterIdlesheet;
    private Image[] player;
    private Image playerIdlesheet;
    private Image[] gold;
    private Image goldIdlesheet;
    private Image rock;
    private Image background;
    private Image portal;
    private long lastIdleFrameAt = 0;
    private AnimationTimer gameLoop;
    private boolean isGameOver = false;
    private int monsterFrame = 0;
    private int playerFrame = 0;
    private int goldFrame = 0;
    private long lastAnimAt = 0;



    @FXML private VBox hudBox;
    @FXML private Label hpLabel;
    @FXML private Label goldLabel;
    @FXML private Label levelLabel;
    @FXML private Label monsterLabel;
    @FXML private Label qLabel;
    @FXML private Label eLabel;


    @FXML
    public void initialize() {
        canvas.setWidth(VIEW_SIZE * TILE_SIZE);
        canvas.setHeight(VIEW_SIZE * TILE_SIZE);
        gc = canvas.getGraphicsContext2D();

        // init game
        state = new GameState();
        state.map = MapGenerator.generate(state.level);
        monsterIdlesheet = new Image(Objects.requireNonNull(
                getClass().getResource("/images/sprites/monster/idle.png")
        ).toExternalForm());
        monster = sliceSprite(monsterIdlesheet, 12, 48, 48);

        playerIdlesheet = new Image(Objects.requireNonNull(
                getClass().getResource("/images/sprites/player/idle.png")
        ).toExternalForm());
        player = sliceSprite(playerIdlesheet, 12, 32, 32);
        rock = new Image(Objects.requireNonNull(
                getClass().getResource("/images/sprites/tiles/box.png")
        ).toExternalForm());
        background = new Image(Objects.requireNonNull(
                getClass().getResource("/images/sprites/tiles/grass.png")
        ).toExternalForm());
        
        portal = new Image(Objects.requireNonNull(
                getClass().getResource("/images/sprites/tiles/portal.png")
        ).toExternalForm());
        goldIdlesheet = new Image(Objects.requireNonNull(
                getClass().getResource("/images/sprites/tiles/goldsheet.png")
        ).toExternalForm());
        gold = sliceSprite(goldIdlesheet, 7, 16, 16);

        // spawn monster
        spawnMonstersForLevel(state.level);

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
    private int getCamX() {
        return Math.max(0,
                Math.min(
                        state.player.x - VIEW_SIZE / 2,
                        Map.SIZE - VIEW_SIZE
                ));
    }

    private int getCamY() {
        return Math.max(0,
                Math.min(
                        state.player.y - VIEW_SIZE / 2,
                        Map.SIZE - VIEW_SIZE
                ));
    }
    private Image[] sliceSprite(Image sheet, int frameCount, int frameW, int frameH) {
        Image[] frames = new Image[frameCount];

        PixelReader reader = sheet.getPixelReader();

        for (int i = 0; i < frameCount; i++) {
            WritableImage frame = new WritableImage(
                reader,
                i * frameW, // x
                0,          // y
                frameW,
                frameH
            );
            frames[i] = frame;
        }
        return frames;
    }


    private void setupInput(KeyEvent e){
        KeyCode code = e.getCode();

        switch (code){
            case UP, W -> {
                movePlayer(0, -1);
                state.player.direction = Direction.UP;
                state.player.moving = true;
            }
            case DOWN, S -> {
                movePlayer(0, 1);
                state.player.direction = Direction.DOWN;
                state.player.moving = true;
            }
            case LEFT, A -> {
                movePlayer(-1, 0);
                state.player.direction = Direction.LEFT;
                state.player.moving = true;
            }
            case RIGHT, D -> {
                movePlayer(1, 0);
                state.player.direction = Direction.RIGHT;
                state.player.moving = true;
            }
            case Q -> useQ();
            case E -> useE();

        }
        canvas.requestFocus();
        
    }
    
    private void useQ() {
        SkillManager.useQ(state);
    }

    private void useE() {
        SkillManager.useE(state);
    }
    private void killMonster(Monster m) {
        m.alive = false;

        state.map.tiles[m.y][m.x] = TileType.EMPTY;
    }

    private void movePlayer(int dx, int dy){
        int nx = state.player.x + dx;
        int ny = state.player.y + dy;

        if (nx < 0 || ny < 0 || nx >= Map.SIZE || ny >= Map.SIZE){
            return;
        }

        TileType tile = state.map.tiles[ny][nx];
        if (tile == TileType.WALL) return;
        if (tile == TileType.GOLD){
            state.player.gold++;
            state.map.tiles[ny][nx] = TileType.EMPTY;
        }
        if (tile == TileType.PORTAL) {
            nextLevel();
            return;
        }
        state.player.x = nx;
        state.player.y = ny;
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
        spawnMonstersForLevel(state.level);

        // reset portal
        state.portalSpawned = false;
        state.portalX = -1;
        state.portalY = -1;

        System.out.println("🔥 NEXT LEVEL: " + state.level);
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

    private void update(){
        updateMonsters();
        updateIdleAnimation();
        checkPortal();
        updateHUD();
        updateShield();
        state.effects.removeIf(e -> !e.isAlive());
    }
    private void updateShield() {
        if (!state.player.shieldActive) return;

        long now = System.currentTimeMillis();
        if (now - state.player.shieldStart >= Player.SHIELD_DURATION) {
            state.player.shieldActive = false;
        }
    }
    private void updateIdleAnimation() {
        long now = System.currentTimeMillis();

        if (now - lastAnimAt > 200) {
            monsterFrame = (monsterFrame + 1) % monster.length;
            playerFrame  = (playerFrame + 1) % player.length;
            goldFrame = (goldFrame + 1) % gold.length;
            lastAnimAt = now;
        }
    }
    private void updateMonsters(){
        long now = System.currentTimeMillis();
        Random rand = new Random();
        for (Monster m : state.monsters ) {
            if (!m.alive) continue;
            if (now < m.lastMoveAt) continue;
            int dx = Math.abs(state.player.x - m.x);
            int dy = Math.abs(state.player.y - m.y);

            if (dx <= 4 && dy <= 4) {
                var path = PathFinding.bfs(
                        state.map,
                        m.x, m.y,
                        state.player.x, state.player.y
                );

                if (!path.isEmpty()) {
                    int[] next = path.get(0);

                    if (next[0] == state.player.x && next[1] == state.player.y) {
                        if (!state.player.shieldActive) {
                            state.player.hp--;
                        } 
                        if (state.player.hp <= 0) {
                            gameOver();
                            return;
                        }
                    } else {
                        m.x = next[0];
                        m.y = next[1];
                    }
                }
            } else {
                if (Math.random() < 0.05) {
                // 🔄 thỉnh thoảng đổi mode
                    m.idleMode = (m.idleMode == IdleMode.HORIZONTAL)
                            ? IdleMode.VERTICAL
                            : IdleMode.HORIZONTAL;
                }

                int nx = m.x;
                int ny = m.y;

                if (m.idleMode == IdleMode.HORIZONTAL) {
                    int step = (m.idleDir == Direction.RIGHT) ? 1 : -1;
                    nx += step;

                    if (Math.abs(nx - m.spawnX) > IDLE_RANGE ||
                        nx < 0 || nx >= Map.SIZE ||
                        state.map.tiles[m.y][nx] == TileType.WALL) {

                        m.idleDir = (m.idleDir == Direction.RIGHT)
                                ? Direction.LEFT
                                : Direction.RIGHT;
                        return;
                    }
                } else {
                    int step = (m.idleDir == Direction.DOWN) ? 1 : -1;
                    ny += step;

                    if (Math.abs(ny - m.spawnY) > IDLE_RANGE ||
                        ny < 0 || ny >= Map.SIZE ||
                        state.map.tiles[ny][m.x] == TileType.WALL) {

                        m.idleDir = (m.idleDir == Direction.DOWN)
                                ? Direction.UP
                                : Direction.DOWN;
                        return;
                    }
                }
                m.x = nx;
                m.y = ny;
            }
            m.lastMoveAt = now + 400;
        }
    }
    private void spawnMonstersForLevel(int level) {
        Random rand = new Random();

        int monsterCount = Math.min(2 + level, MAX_MONSTERS);

        state.monsters.clear();

        for (int i = 0; i < monsterCount; i++) {
            int x, y;
            int safety = 0;

            do {
                x = rand.nextInt(Map.SIZE);
                y = rand.nextInt(Map.SIZE);
                safety++;
            } while (
                    (state.map.tiles[y][x] != TileType.EMPTY) ||
                            (x == state.player.x && y == state.player.y)
                                    && safety < 100
            );
            int hp = 2 + level/2;
            state.monsters.add(new Monster(x, y, hp ));
        }
    }
    private void render() {
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());   

        int camX = getCamX();
        int camY = getCamY();

        // draw map
        for (int y = 0; y < VIEW_SIZE; y++) {
            for (int x = 0; x < VIEW_SIZE; x++) {
                    gc.drawImage(
                            background,
                            x * TILE_SIZE,
                            y * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );

                int mapX = camX + x;
                int mapY = camY + y;

                if (mapX < 0 || mapY < 0 ||
                    mapX >= Map.SIZE || mapY >= Map.SIZE) continue;

                switch (state.map.tiles[mapY][mapX]) {
                    case WALL -> {
                        gc.drawImage(
                                rock,
                                x * TILE_SIZE,
                                y * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE
                        );
                    }
                    case GOLD -> {
                        gc.drawImage(
                                gold[goldFrame],
                                x * TILE_SIZE,
                                y * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE
                        );
                    }
                    case PORTAL -> {
                        gc.drawImage(
                                portal,
                                x * TILE_SIZE,
                                y * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE
                        );
                    }
                }
            }
        }

        // draw player
        gc.drawImage(
                player[playerFrame],
                (state.player.x - camX) * TILE_SIZE,
                (state.player.y - camY) * TILE_SIZE,
                TILE_SIZE,
                TILE_SIZE
        );
        renderEffects(camX, camY);
        if (state.player.shieldActive) {

        }

        // draw monsters
        for (Monster m : state.monsters) {
            if (!m.alive) continue;

            if (m.x < camX || m.y < camY ||
                m.x >= camX + VIEW_SIZE ||
                m.y >= camY + VIEW_SIZE) continue;

            gc.drawImage(
                    monster[monsterFrame],
                    (m.x - camX) * TILE_SIZE,
                    (m.y - camY) * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
            );

            // HP bar
            gc.setFill(Color.LIMEGREEN);
            gc.fillRect(
                    (m.x - camX) * TILE_SIZE,
                    (m.y - camY) * TILE_SIZE - 6,
                    (TILE_SIZE * m.hp) / 3.0,
                    4
            );
        }
    }
    private void renderEffects(int camX, int camY) {
        long now = System.currentTimeMillis();

        for (SkillEffect e : state.effects) {
            double t = (now - e.startTime) / (double) e.duration;

            if (e.type == EffectType.SLASH) {
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(4 * (1 - t));

                gc.strokeArc(
                        (e.x - camX) * TILE_SIZE - 10,
                        (e.y - camY) * TILE_SIZE - 10,
                        TILE_SIZE + 20,
                        TILE_SIZE + 20,
                        45,
                        90,
                        ArcType.OPEN
                );
            }

            if (e.type == EffectType.SHIELD) {
                gc.setStroke(Color.CYAN);
                gc.setLineWidth(3);
                gc.strokeOval(
                        (e.x - camX) * TILE_SIZE - 5,
                        (e.y - camY) * TILE_SIZE - 5,
                        TILE_SIZE + 10,
                        TILE_SIZE + 10
                );
            }
        }
    }
    


    private void updateHUD() {
        hpLabel.setText("❤️ HP: " + state.player.hp);
        goldLabel.setText("💰 Gold: " + state.player.gold);
        levelLabel.setText("🧠 Level: " + state.level);

        long aliveCount = state.monsters.stream()
                .filter(m -> m.alive)
                .count();
        monsterLabel.setText("👾 Monsters: " + aliveCount);

        long now = System.currentTimeMillis();

        long qCd = Math.max(0,
                10000 - (now - state.player.lastQ));
        qLabel.setText(
                qCd == 0 ? "🧨 Q: Ready"
                        : "🧨 Q: " + (qCd / 1000) + "s"
        );

        long eCd = Math.max(0,
                4000 - (now - state.player.lastE));
        eLabel.setText(
                eCd == 0 ? "⚡ E: Ready"
                        : "⚡ E: " + (eCd / 1000) + "s"
        );
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