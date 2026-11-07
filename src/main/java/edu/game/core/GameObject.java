package edu.game.core;

import java.util.Objects;

public class GameObject {
    private final String id;
    private final String type;
    private Position position;
    private int health;
    private final int maxHealth;

    public GameObject(String id, String type, Position position, int health) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.position = Objects.requireNonNull(position);
        if (health < 0) throw new IllegalArgumentException("health must be nonnegative");
        this.health = health;
        this.maxHealth = health;
    }

    public String id() { return id; }
    public String type() { return type; }
    public Position position() { return position; }
    public int health() { return health; }
    public int maxHealth() { return maxHealth; }
    public boolean alive() { return health > 0; }
    public void setPosition(Position position) { this.position = Objects.requireNonNull(position); }
    public void damage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("damage must be nonnegative");
        health = Math.max(0, health - amount);
    }
    public void heal(int amount) {
        if (amount < 0) throw new IllegalArgumentException("heal must be nonnegative");
        health = Math.min(maxHealth, health + amount);
    }
}
