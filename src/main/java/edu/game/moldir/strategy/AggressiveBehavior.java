package edu.game.moldir.strategy;

import edu.game.core.GameObject;
import edu.game.core.Position;

public final class AggressiveBehavior implements AIBehavior {
    @Override
    public Position nextPosition(GameObject agent, Position target) {
        Position current = agent.position();
        return current.move(Integer.compare(target.x(), current.x()),
                Integer.compare(target.y(), current.y()));
    }

    @Override public String name() { return "Aggressive"; }
}
