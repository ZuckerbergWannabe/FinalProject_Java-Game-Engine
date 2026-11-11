package edu.game.almanzor;

import edu.game.almanzor.bridge.RenderBackend;
import edu.game.almanzor.bridge.Renderer2D;
import edu.game.almanzor.bridge.Renderer3D;
import edu.game.core.Tile;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GraphicsRenderingTest {
    @Test void twoBackendsPaintDistinctPixelAndDepthStyles() {
        RenderBackend flat = new Renderer2D();
        RenderBackend depth = new Renderer3D();
        BufferedImage first = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        BufferedImage second = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g1 = first.createGraphics();
        Graphics2D g2 = second.createGraphics();
        flat.paintTile(g1, Tile.WALL, 0, 0, 28, 0, 0, 1000);
        depth.paintTile(g2, Tile.WALL, 0, 0, 28, 0, 0, 1000);
        g1.dispose();
        g2.dispose();
        assertNotEquals(first.getRGB(14, 22), second.getRGB(14, 22));
    }
}
