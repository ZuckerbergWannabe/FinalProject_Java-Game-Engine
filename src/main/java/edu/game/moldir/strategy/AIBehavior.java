package edu.game.moldir.strategy;

import edu.game.core.GameObject;
import edu.game.core.Position;

public interface AIBehavior {
    Position nextPosition(GameObject agent, Position target);
    String name();
}
