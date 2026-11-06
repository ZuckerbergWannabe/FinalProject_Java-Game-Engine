package edu.game.almanzor.bridge;

import edu.game.core.GameObject;
import java.util.Objects;

public abstract class GameVisual {
    protected final RenderBackend renderer;

    protected GameVisual(RenderBackend renderer) { this.renderer = Objects.requireNonNull(renderer); }
    public abstract String render(GameObject object);
}
