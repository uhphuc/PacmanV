package org.example.core;

public class Monster {
    public int x, y;
    public int spawnX, spawnY;
    public boolean active = false;
    public long lastMoveAt = 0;
    public boolean alive = true;
    public int rowIndex; 
    public Direction idleDir = Direction.RIGHT;
    public IdleMode idleMode = IdleMode.HORIZONTAL;
    public int hp = 3;
    public Monster(int x, int y, int hp) {
        this.x = x;
        this.y = y;
        this.rowIndex = (int)(Math.random() * 4);
        this.spawnY = y;
        this.spawnX = x;
        this.hp = hp;
    }
}