package edu.game.moldir.strategy;

import edu.game.core.GameObject;
import edu.game.core.Position;

public final class PatrolBehavior implements AIBehavior {
    @Override
    public Position nextPosition(GameObject agent, Position target) {
        return agent.position().move(1, 0);
    }

    @Override public String name() { return "Patrol"; }
}
