package org.example.ui.utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import org.example.core.*;

public class GameRenderer {

    public static void renderMap(
            GraphicsContext gc,
            GameState state,
            int camX,
            int camY,
            int tileSize,
            int viewSize,
            ImageAssets assets,
            int goldFrame
    ) {
        for (int y = 0; y < viewSize; y++) {
            for (int x = 0; x < viewSize; x++) {

                gc.drawImage(
                        assets.background,
                        x * tileSize,
                        y * tileSize,
                        tileSize,
                        tileSize
                );

                int mapX = camX + x;
                int mapY = camY + y;
                if (mapX < 0 || mapY < 0 ||
                    mapX >= Map.SIZE || mapY >= Map.SIZE) continue;

                switch (state.map.tiles[mapY][mapX]) {
                    case WALL -> gc.drawImage(assets.rock,
                            x * tileSize, y * tileSize, tileSize, tileSize);
                    case GOLD -> gc.drawImage(assets.gold[goldFrame],
                            x * tileSize, y * tileSize, tileSize, tileSize);
                    case PORTAL -> gc.drawImage(assets.portal,
                            x * tileSize, y * tileSize, tileSize, tileSize);
                    case BORDER -> gc.drawImage(assets.border,
                            x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    public static void renderPlayer(
            GraphicsContext gc,
            GameState state,
            int camX,
            int camY,
            int tileSize,
            ImageAssets assets,
            int frame
    ) {
        gc.drawImage(
                assets.player[frame],
                (state.player.x - camX) * tileSize,
                (state.player.y - camY) * tileSize,
                tileSize,
                tileSize
        );
    }

    public static void renderMonsters(
            GraphicsContext gc,
            GameState state,
            int camX,
            int camY,
            int tileSize,
            int viewSize,
            ImageAssets assets,
            int frame
    ) {
        for (Monster m : state.monsters) {
            if (!m.alive) continue;
            if (m.x < camX || m.y < camY ||
                m.x >= camX + viewSize ||
                m.y >= camY + viewSize) continue;

            gc.drawImage(
                    assets.monster[frame],
                    (m.x - camX) * tileSize,
                    (m.y - camY) * tileSize,
                    tileSize,
                    tileSize
            );

            gc.setFill(Color.LIMEGREEN);
            gc.fillRect(
                    (m.x - camX) * tileSize,
                    (m.y - camY) * tileSize - 6,
                    (tileSize * m.hp) / 3.0,
                    4
            );
        }
    }

    public static void renderEffects(
            GraphicsContext gc,
            GameState state,
            int camX,
            int camY,
            ImageAssets assets,
            int tileSize
    ) {
        long now = System.currentTimeMillis();

        for (SkillEffect e : state.effects) {
            double t = (now - e.startTime) / (double) e.duration;

            if (e.type == EffectType.SLASH) {
                // Draw normal attack effect 3x3 from center
                gc.drawImage(
                        assets.normal_attack,
                        (e.x - camX - 1) * tileSize,
                        (e.y - camY - 1) * tileSize,
                        tileSize * 3,
                        tileSize * 3
                );
            }
            if (e.type == EffectType.SHIELD) {
                if (e.followPlayer) {
                    e.x = state.player.x;
                    e.y = state.player.y;
                }
                gc.drawImage(
                        assets.shield,
                        (e.x - camX) * tileSize,
                        (e.y - camY) * tileSize,
                        tileSize,
                        tileSize
                );
            }
            if (e.type == EffectType.FREEZE) {
                // Draw freeze effect covering the whole screen
                gc.setGlobalAlpha(1.0 - t);
                gc.drawImage(
                        assets.freeze,
                        0,
                        0,
                        tileSize * GameState.MAP_VIEW_SIZE,
                        tileSize * GameState.MAP_VIEW_SIZE
                );
                gc.setGlobalAlpha(1.0);
            }
        }
    }
}
