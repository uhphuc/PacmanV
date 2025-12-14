package org.example.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.data.GameResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveUtils {

    private static final String FILE = "save/gold_history.json";

    public static void saveGold(int gold) {
        if (gold <= 0) return; // No need to save zero gold
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            File file = new File(FILE);
            file.getParentFile().mkdirs();

            List<GameResult> history;

            if (file.exists()) {
                history = mapper.readValue(
                        file,
                        mapper.getTypeFactory()
                              .constructCollectionType(List.class, GameResult.class)
                );
            } else {
                history = new ArrayList<>();
            }

            history.add(new GameResult(gold));
            mapper.writeValue(file, history);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void clearSave() {
        try {
            File file = new File(FILE);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
