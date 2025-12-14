package org.example.core;

public class SkillEffect {
    public int x, y;
    public long startTime;
    public long duration;
    public EffectType type;
    public boolean followPlayer = false;

    public SkillEffect(int x, int y, long duration, EffectType type) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.type = type;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return System.currentTimeMillis() - startTime < duration;
    }
}

