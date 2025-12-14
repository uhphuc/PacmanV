package org.example.ui.utils;


import org.example.core.Map;
import org.example.core.Player;

public class CameraUtils {

    public static int camX(Player p, int viewSize) {
        return Math.max(0,
                Math.min(p.x - viewSize / 2, Map.SIZE - viewSize));
    }

    public static int camY(Player p, int viewSize) {
        return Math.max(0,
                Math.min(p.y - viewSize / 2, Map.SIZE - viewSize));
    }
}
