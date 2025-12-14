package org.example.ui.managers;

import org.example.core.Direction;
import org.example.core.GameState;
import org.example.core.Map;
import org.example.core.TileType;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class InputManager {
    public static void handleKey(
            KeyEvent e,
            GameState state,
            Runnable onNextLevel,
            Runnable useQ,
            Runnable useE
    ) {
        KeyCode code = e.getCode();

        switch (code) {
            case UP, W -> movePlayer(state, 0, -1, Direction.UP, onNextLevel);
            case DOWN, S -> movePlayer(state, 0, 1, Direction.DOWN, onNextLevel);
            case LEFT, A -> movePlayer(state, -1, 0, Direction.LEFT, onNextLevel);
            case RIGHT, D -> movePlayer(state, 1, 0, Direction.RIGHT, onNextLevel);
            case Q -> useQ.run();
            case E -> useE.run();
        }
    }

    private static void movePlayer(
            GameState state,
            int dx, int dy,
            Direction dir,
            Runnable onNextLevel
        ){
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
            onNextLevel.run();
            return;
        }
        state.player.x = nx;
        state.player.y = ny;
    }
}
