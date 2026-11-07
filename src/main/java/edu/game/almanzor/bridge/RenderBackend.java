package edu.game.almanzor.bridge;

import edu.game.core.GameObject;
import edu.game.core.Position;
import edu.game.core.Tile;
import java.awt.Graphics2D;

/** Bridge implementation interface: logical visuals are independent of rendering style. */
public interface RenderBackend {
    String draw(String type, Position position);
    void paintTile(Graphics2D graphics, Tile tile, int x, int y, int size,
                   int mapX, int mapY, long time);
    void paintObject(Graphics2D graphics, GameObject object, int x, int y, int size, long time);
}
