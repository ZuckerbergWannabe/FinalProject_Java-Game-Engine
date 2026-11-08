    package edu.game.askar.builder;

import java.util.Objects;

public final class GameConfig {
    private final String title;
    private final int width;
    private final int height;
    private final int startingEnemies;
    private final boolean soundEnabled;

    private GameConfig(Builder builder) {
        this.title = builder.title;
        this.width = builder.width;
        this.height = builder.height;
        this.startingEnemies = builder.startingEnemies;
        this.soundEnabled = builder.soundEnabled;
    }

    public String title() { return title; }
    public int width() { return width; }
    public int height() { return height; }
    public int startingEnemies() { return startingEnemies; }
    public boolean soundEnabled() { return soundEnabled; }

    public static final class Builder {
        private String title = "Pattern Quest";
        private int width = 800;
        private int height = 600;
        private int startingEnemies = 2;
        private boolean soundEnabled = true;

        public Builder title(String title) { this.title = Objects.requireNonNull(title); return this; }
        public Builder resolution(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }
        public Builder startingEnemies(int count) { this.startingEnemies = count; return this; }
        public Builder soundEnabled(boolean value) { this.soundEnabled = value; return this; }
        public GameConfig build() {
            if (title.isBlank() || width <= 0 || height <= 0 || startingEnemies < 0) {
                throw new IllegalArgumentException("Invalid game configuration");
            }
            return new GameConfig(this);
        }
    }
}
