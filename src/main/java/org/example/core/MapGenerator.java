package org.example.core;


import java.util.Random;

public class MapGenerator {

    public static Map generate(int level) {
        Map map = new Map();
        Random rand = new Random();

        int size = Map.SIZE;

        // 1️⃣ fill empty
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                map.tiles[y][x] = TileType.EMPTY;
            }
        }

        // 2️⃣ border wall (đỡ chạy ra ngoài)
        for (int i = 0; i < size; i++) {
            map.tiles[0][i] = TileType.WALL;
            map.tiles[size - 1][i] = TileType.WALL;
            map.tiles[i][0] = TileType.WALL;
            map.tiles[i][size - 1] = TileType.WALL;
        }

        // 3️⃣ wall clusters
        int clusterCount = 10 + level * 2;
        for (int i = 0; i < clusterCount; i++) {
            int cx = rand.nextInt(size - 2) + 1;
            int cy = rand.nextInt(size - 2) + 1;
            int clusterSize = 3 + rand.nextInt(5);

            for (int j = 0; j < clusterSize; j++) {
                int nx = cx + rand.nextInt(3) - 1;
                int ny = cy + rand.nextInt(3) - 1;

                if (nx > 0 && ny > 0 && nx < size - 1 && ny < size - 1) {
                    map.tiles[ny][nx] = TileType.WALL;
                }
            }
        }

        // 4️⃣ player spawn
        map.playerStartX = size / 2;
        map.playerStartY = size / 2;
        map.tiles[map.playerStartY][map.playerStartX] = TileType.EMPTY;

        // 5️⃣ flood-fill check (đảm bảo đi được)
        boolean[][] visited = new boolean[size][size];
        floodFill(map, visited, map.playerStartX, map.playerStartY);

        for (int y = 1; y < size - 1; y++) {
            for (int x = 1; x < size - 1; x++) {
                if (!visited[y][x]) {
                    map.tiles[y][x] = TileType.WALL;
                }
            }
        }

        // 6️⃣ spawn gold
        int goldCount = 20 + level * 3;
        for (int i = 0; i < goldCount; i++) {
            int x, y;
            do {
                x = rand.nextInt(size);
                y = rand.nextInt(size);
            } while (map.tiles[y][x] != TileType.EMPTY);

            map.tiles[y][x] = TileType.GOLD;
        }

        return map;
    }
    private static void floodFill(Map map, boolean[][] visited, int x, int y) {
        if (x < 0 || y < 0 || x >= Map.SIZE || y >= Map.SIZE) return;
        if (visited[y][x]) return;
        if (map.tiles[y][x] == TileType.WALL) return;

        visited[y][x] = true;

        floodFill(map, visited, x + 1, y);
        floodFill(map, visited, x - 1, y);
        floodFill(map, visited, x, y + 1);
        floodFill(map, visited, x, y - 1);
    }


}
