package edu.game.askar.factory;

import edu.game.core.GameObject;
import edu.game.core.Position;

public interface ObjectFactory {
    GameObject create(String id, Position position);
}
