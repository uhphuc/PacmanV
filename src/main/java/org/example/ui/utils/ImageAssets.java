package org.example.ui.utils;

import javafx.scene.image.Image;
import java.util.Objects;

import static org.example.ui.utils.SpriteUtils.slice;

public class ImageAssets {

    public Image[] monster;
    public Image[] player;
    public Image[] gold;

    public Image rock;
    public Image background;
    public Image portal;
    public Image border;

    public Image shield;
    public Image normal_attack;
    public Image freeze;

    public void load() {
        Image monsterSheet = loadImg("/images/sprites/monster/idle.png");
        Image playerSheet  = loadImg("/images/sprites/player/idle.png");
        Image goldSheet    = loadImg("/images/sprites/tiles/goldsheet.png");

        monster = slice(monsterSheet, 12, 48, 48);
        player  = slice(playerSheet, 12, 32, 32);
        gold    = slice(goldSheet, 7, 16, 16);

        rock = loadImg("/images/sprites/tiles/blue_planet.png");
        border = loadImg("/images/sprites/tiles/planets.png");
        background = loadImg("/images/sprites/tiles/background.png");
        portal = loadImg("/images/sprites/tiles/blackhole1.gif");

        shield = loadImg("/images/sprites/skills/shield.png");
        normal_attack = loadImg("/images/sprites/skills/na.png");
        freeze = loadImg("/images/sprites/skills/freeze.png");
    }

    private Image loadImg(String path) {
        return new Image(Objects.requireNonNull(
                getClass().getResource(path)
        ).toExternalForm());
    }
}

