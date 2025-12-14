package org.example.core;

import java.util.*;

public class PathFinding {

    private static final int[][] DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<int[]> bfs(
            Map map,
            int startX, int startY,
            int targetX, int targetY
    ) {
        boolean[][] visited = new boolean[Map.SIZE][Map.SIZE];
        int[][] prevX = new int[Map.SIZE][Map.SIZE];
        int[][] prevY = new int[Map.SIZE][Map.SIZE];

        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0];
            int y = cur[1];

            if (x == targetX && y == targetY)
                break;

            for (int[] d : DIRS) {
                int nx = x + d[0];
                int ny = y + d[1];

                if (nx < 0 || ny < 0 || nx >= Map.SIZE || ny >= Map.SIZE)
                    continue;

                if (visited[ny][nx]) continue;
                if (map.tiles[ny][nx] == TileType.WALL) continue;

                visited[ny][nx] = true;
                prevX[ny][nx] = x;
                prevY[ny][nx] = y;
                q.add(new int[]{nx, ny});
            }
        }

        // reconstruct path
        List<int[]> path = new ArrayList<>();
        int cx = targetX;
        int cy = targetY;

        if (!visited[cy][cx]) return path; // no path

        while (cx != startX || cy != startY) {
            path.add(0, new int[]{cx, cy});
            int px = prevX[cy][cx];
            int py = prevY[cy][cx];
            cx = px;
            cy = py;
        }

        return path;
    }
}
