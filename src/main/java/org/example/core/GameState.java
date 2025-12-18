package org.example.core;

import java.util.ArrayList;

public class GameState {
    public ArrayList<Monster> monsters = new ArrayList<>();
    public ArrayList<SkillEffect> effects = new ArrayList<>();
    
    public Player player;
    public Map map;
    public int level = 1;
    public static final int MAP_VIEW_SIZE = 10;

    public boolean portalSpawned = false;
    public int portalX = -1;
    public int portalY = -1;
}
