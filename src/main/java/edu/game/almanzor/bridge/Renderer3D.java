package edu.game.almanzor.bridge;

import edu.game.core.GameObject;
import edu.game.core.Position;
import edu.game.core.Tile;

import java.awt.*;

public final class Renderer3D implements RenderBackend {
    @Override public String draw(String type, Position position) {
        return "[3D] mesh " + type + " at (" + position.x() + ", " + position.y() + ", 0)";
    }

    @Override public void paintTile(Graphics2D graphics, Tile tile, int x, int y, int size,
                                    int mapX, int mapY, long time) {
        PixelArt.tile(graphics, tile, x, y, size, mapX, mapY, true, time);
    }

    @Override public void paintObject(Graphics2D graphics, GameObject object, int x, int y, int size, long time) {
        PixelArt.object(graphics, object, x, y, size, true, time);
    }
}
