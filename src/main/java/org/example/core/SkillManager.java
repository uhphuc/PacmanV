package org.example.core;

import java.util.function.Consumer;

public class SkillManager {
    private static final int SKILL_RANGE = 1;
    private static final int GOLD_PER_MONSTER = 50;
    public static void useQ(GameState state) {
        if (!state.player.canUseQ()) return;


        for (Monster m : state.monsters) {
            m.lastMoveAt = System.currentTimeMillis() + 3000;
        }

        state.player.lastQ = System.currentTimeMillis();
        System.out.println("❄ Freeze monsters!");
    }

    public static void useE(GameState state) {
        if (!state.player.canUseE()) return;

        state.player.shieldActive = true;
        state.player.shieldStart = System.currentTimeMillis();
        state.player.lastE = System.currentTimeMillis();

        state.effects.add(
            new SkillEffect(
                state.player.x,
                state.player.y,
                Player.SHIELD_DURATION,
                EffectType.SHIELD
            )
        );
    }

    public static void useLeftClick(GameState state, Consumer<Monster> killFn) {
        if (!state.player.canClick()) return;
        int px = state.player.x;
        int py = state.player.y;

        state.effects.add(
                new SkillEffect(
                        state.player.x,
                        state.player.y,
                        200,
                        EffectType.SLASH
                )
        );
        state.player.lastClick = System.currentTimeMillis();

        for (Monster m : state.monsters) {
            if (!m.alive) continue;
            int dx = Math.abs(m.x - px);
            int dy = Math.abs(m.y - py);
            if (dx <= SKILL_RANGE && dy <= SKILL_RANGE) {
                m.hp--;
                if (m.hp <= 0) {
                    killFn.accept(m);
                    state.player.gold += 50;
                }
                return;
            }
        }

    }
    
}
