package edu.game.askar.factory;

import edu.game.core.GameObject;
import edu.game.core.Position;

public final class EnemyFactory implements ObjectFactory {
    @Override public GameObject create(String id, Position position) {
        return new GameObject(id, "ENEMY", position, 50);
    }
}
