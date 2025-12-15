package org.example.ui.managers;


import org.example.core.*;

import java.util.Random;

public class MonsterManager {
    private static final int IDLE_RANGE = 3;
    private static final int MAX_MONSTERS = 10;

    public static void spawn(GameState state) {
        Random rand = new Random();
        int count = Math.min(2 + state.level, MAX_MONSTERS);
        state.monsters.clear();

        for (int i = 0; i < count; i++) {
            int x, y;
            do {
                x = rand.nextInt(Map.SIZE);
                y = rand.nextInt(Map.SIZE);
            } while (state.map.tiles[y][x] != TileType.EMPTY);

            state.monsters.add(new Monster(x, y, 2 + state.level / 2));
        }
    }

    public static void update(GameState state, Runnable onPlayerDead) {
        long now = System.currentTimeMillis();

        for (Monster m : state.monsters) {
            if (!m.alive) continue;

            // ✅ DAMAGE CHECK 
            int dx = Math.abs(state.player.x - m.x);
            int dy = Math.abs(state.player.y - m.y);

            if (dx == 0 && dy == 0) {
                if (!state.player.isShieldActive() &&
                    now - state.player.lastHitAt > Player.HIT_COOLDOWN) {

                    state.player.hp--;
                    state.player.lastHitAt = now;
                }

                if (state.player.hp <= 0) {
                    onPlayerDead.run();
                    return;
                }
            }

            // MOVE COOLDOWN
            if (now < m.lastMoveAt) continue;

            // CHASE PLAYER
            if (dx <= 4 && dy <= 4) {
                var path = PathFinding.bfs(
                        state.map,
                        m.x, m.y,
                        state.player.x, state.player.y
                );

                if (!path.isEmpty()) {
                    int[] next = path.get(0);
                    m.x = next[0];
                    m.y = next[1];
                }
            }
            // IDLE MOVEMENT
            else {
                if (Math.random() < 0.05) {
                    m.idleMode = (m.idleMode == IdleMode.HORIZONTAL)
                            ? IdleMode.VERTICAL
                            : IdleMode.HORIZONTAL;
                }

                int nx = m.x;
                int ny = m.y;

                if (m.idleMode == IdleMode.HORIZONTAL) {
                    nx += (m.idleDir == Direction.RIGHT) ? 1 : -1;

                    if (Math.abs(nx - m.spawnX) > IDLE_RANGE ||
                        nx < 0 || nx >= Map.SIZE ||
                        state.map.tiles[m.y][nx] == TileType.WALL) {

                        m.idleDir = (m.idleDir == Direction.RIGHT)
                                ? Direction.LEFT
                                : Direction.RIGHT;
                        continue;
                    }
                } else {
                    ny += (m.idleDir == Direction.DOWN) ? 1 : -1;

                    if (Math.abs(ny - m.spawnY) > IDLE_RANGE ||
                        ny < 0 || ny >= Map.SIZE ||
                        state.map.tiles[ny][m.x] == TileType.WALL) {

                        m.idleDir = (m.idleDir == Direction.DOWN)
                                ? Direction.UP
                                : Direction.DOWN;
                        continue;
                    }
                }
                m.x = nx;
                m.y = ny;
            }
            m.lastMoveAt = now + 400;
        }
    }

}

