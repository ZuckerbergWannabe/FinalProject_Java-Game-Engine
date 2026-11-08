package edu.game.almanzor.bridge;

import edu.game.core.GameObject;
import java.awt.Graphics2D;

public final class CharacterVisual extends GameVisual {
    public CharacterVisual(RenderBackend renderer) { super(renderer); }

    @Override public String render(GameObject object) {
        return renderer.draw("character/" + object.type(), object.position());
    }

    public void paintObject(Graphics2D graphics, GameObject object, int x, int y, int size, long time) {
        renderer.paintObject(graphics, object, x, y, size, time);
    }
}