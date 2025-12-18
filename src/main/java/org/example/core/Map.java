package org.example.core;

public class Map {
    public static final int SIZE = 30;  

    public TileType[][] tiles = new TileType[SIZE][SIZE];

    public int playerStartX;
    public int playerStartY;

    public Map() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                tiles[y][x] = TileType.EMPTY;
            }
        }
    }
}
