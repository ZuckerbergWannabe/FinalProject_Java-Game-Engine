package edu.game.almanzor;

import edu.game.almanzor.bridge.CharacterVisual;
import edu.game.almanzor.bridge.EnvironmentVisual;
import edu.game.almanzor.bridge.Renderer2D;
import edu.game.almanzor.bridge.Renderer3D;
import edu.game.core.GameObject;
import edu.game.core.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BridgeTest {
    @Test void sameAbstractionWorksWith2dAnd3d() {
        GameObject object = new GameObject("hero", "PLAYER", new Position(2, 3), 100);
        assertTrue(new CharacterVisual(new Renderer2D()).render(object).contains("sprite"));
        assertTrue(new CharacterVisual(new Renderer3D()).render(object).contains("mesh"));
        assertTrue(new EnvironmentVisual(new Renderer2D()).render(object).contains("environment"));
    }
}
