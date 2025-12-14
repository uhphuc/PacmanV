package org.example.ui.utils;

import javafx.scene.image.*;

public class SpriteUtils {

    public static Image[] slice(
            Image sheet,
            int frameCount,
            int frameW,
            int frameH
    ) {
        Image[] frames = new Image[frameCount];
        PixelReader reader = sheet.getPixelReader();

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new WritableImage(
                    reader,
                    i * frameW,
                    0,
                    frameW,
                    frameH
            );
        }
        return frames;
    }
}
