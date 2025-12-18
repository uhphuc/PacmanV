package org.example.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.LinkedHashMap;

public class SaveUtils {

    private static final String FILE = "save/gold_history.json";

    public static void saveGold(int gold) {
        if (gold <= 0) return;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            File file = new File(FILE);
            file.getParentFile().mkdirs();

            LinkedHashMap<Long, Integer> map;

            if (file.exists()) {
                map = mapper.readValue(
                        file,
                        mapper.getTypeFactory()
                                .constructMapType(
                                        LinkedHashMap.class,
                                        Long.class,
                                        Integer.class
                                )
                );
            } else {
                map = new LinkedHashMap<>();
            }

            map.put(System.currentTimeMillis(), gold);
            mapper.writeValue(file, map);

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
 