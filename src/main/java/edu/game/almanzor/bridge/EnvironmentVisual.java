package edu.game.almanzor.bridge;

import edu.game.core.GameObject;
import edu.game.core.Tile;
import java.awt.*;

public final class EnvironmentVisual extends GameVisual {
    public EnvironmentVisual(RenderBackend renderer) { super(renderer); }

    @Override public String render(GameObject object) {
        return renderer.draw("environment/" + object.type(), object.position());
    }

    public void paintTile(Graphics2D graphics, Tile tile, int x, int y, int size,
                          int mapX, int mapY, long time) {
        renderer.paintTile(graphics, tile, x, y, size, mapX, mapY, time);
    }
}
