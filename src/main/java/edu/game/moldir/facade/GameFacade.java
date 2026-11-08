package edu.game.moldir.facade;

import edu.game.almanzor.adapter.LegacyAudioAdapter;
import edu.game.almanzor.adapter.LegacyAudioEngine;
import edu.game.almanzor.bridge.Renderer2D;
import edu.game.almanzor.bridge.Renderer3D;
import edu.game.askar.builder.GameConfig;
import edu.game.core.GameEngine;
import edu.game.moldir.strategy.AggressiveBehavior;
import edu.game.moldir.strategy.DefensiveBehavior;
import edu.game.moldir.strategy.PatrolBehavior;
import java.util.List;
import java.util.Locale;

public final class GameFacade {
    private final GameEngine engine;

    public GameFacade(GameConfig config) {
        engine = new GameEngine(config, new Renderer2D(),
                new LegacyAudioAdapter(new LegacyAudioEngine()));
    }

    public void startGame() { engine.start(); }
    public void updateGame() { engine.update(); }
    public List<String> drawFrame() { return engine.renderFrame(); }
    public String playEffect(String soundId) { return engine.playSound(soundId); }
    public GameEngine engine() { return engine; }

    public void switchGraphics(String mode) {
        switch (mode.toUpperCase(Locale.ROOT)) {
            case "2D" -> engine.setRenderer(new Renderer2D());
            case "3D" -> engine.setRenderer(new Renderer3D());
            default -> throw new IllegalArgumentException("Unknown graphics mode: " + mode);
        }
    }

    public void changeEnemyAI(String mode) {
        switch (mode.toUpperCase(Locale.ROOT)) {
            case "AGGRESSIVE" -> engine.setAIBehavior(new AggressiveBehavior());
            case "DEFENSIVE" -> engine.setAIBehavior(new DefensiveBehavior());
            case "PATROL" -> engine.setAIBehavior(new PatrolBehavior());
            default -> throw new IllegalArgumentException("Unknown AI mode: " + mode);
        }
    }
}
