package org.example.core;

public class Player {
    public int x, y;
    public int hp = 5;
    public long lastE = 0;
    public long lastQ = 0;
    public long lastClick = 0;
    public int gold = 0;
    public Direction direction = Direction.DOWN;
    public boolean moving = false;
    public boolean shieldActive = false;
    public long shieldStart = 0;
    public static final long SHIELD_DURATION = 3000; // 3s


    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean canUseE() {
        return System.currentTimeMillis() - lastE >= 7000;
    }
    public boolean canUseQ() {
        return System.currentTimeMillis() - lastQ >= 10000;
    }
    public boolean canClick(){
        return System.currentTimeMillis() - lastClick >= 1500;
    }
}