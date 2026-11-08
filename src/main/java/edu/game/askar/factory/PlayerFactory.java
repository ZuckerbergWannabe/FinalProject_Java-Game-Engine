package edu.game.askar.factory;

import edu.game.core.GameObject;
import edu.game.core.Position;

public final class PlayerFactory implements ObjectFactory {
    @Override public GameObject create(String id, Position position) {
        return new GameObject(id, "PLAYER", position, 100);
    }
}
