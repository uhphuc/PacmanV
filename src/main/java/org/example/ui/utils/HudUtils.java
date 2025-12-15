package org.example.ui.utils;

import javafx.scene.control.Label;
import org.example.core.GameState;

public class HudUtils {

    public static void update(
            GameState state,
            Label hp,
            Label gold,
            Label level,
            Label monster,
            Label qLabel,
            Label eLabel,
            Label naLabel,
            Label portalLabel
    ) {
        hp.setText("HP: " + state.player.hp);
        gold.setText("Gold: " + state.player.gold);
        level.setText("Level: " + state.level);

        long alive = state.monsters.stream().filter(m -> m.alive).count();
        monster.setText("Monsters: " + alive);
        long now = System.currentTimeMillis();

        long qCd = Math.max(0,
                10000 - (now - state.player.lastQ));
        qLabel.setText(
                qCd == 0 ? "Q: Ready"
                        : "Q: " + (qCd / 1000) + "s"
        );

        long eCd = Math.max(0,
                7000 - (now - state.player.lastE));
        eLabel.setText(
                eCd == 0 ? "E: Ready"
                        : "E: " + (eCd / 1000) + "s"
        );
        long naCd = Math.max(0,
                1500 - (now - state.player.lastClick));
        naLabel.setText(
                naCd == 0 ? "NA: Ready"
                        : "NA: " + (naCd / 1000) + "s"
        );
        if (state.monsters.stream().noneMatch(m -> m.alive)) {
            portalLabel.setText("Portal: Unlock at (" +
                    state.portalX + "," +
                    state.portalY + ")");
        } else {
            portalLabel.setText("Portal: Locked");
        }
    }
}
