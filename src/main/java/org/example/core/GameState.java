package org.example.core;

import java.util.ArrayList;

public class GameState {
    public Player player = new Player(1,1);
    public ArrayList<Monster> monsters = new ArrayList<>();
    public ArrayList<SkillEffect> effects = new ArrayList<>();
    public Map map;
    public int level = 1;

    public boolean portalSpawned = false;
    public int portalX = -1;
    public int portalY = -1;
}
