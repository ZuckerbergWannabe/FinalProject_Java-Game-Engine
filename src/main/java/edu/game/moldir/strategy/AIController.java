package edu.game.moldir.strategy;

import java.util.Objects;

import edu.game.core.GameObject;
import edu.game.core.Position;

public final class AIController {
    private AIBehavior behavior;

    public AIController(AIBehavior behavior) { setBehavior(behavior); }
    public void setBehavior(AIBehavior behavior) { this.behavior = Objects.requireNonNull(behavior); }
    public String behaviorName() { return behavior.name(); }
    public void update(GameObject agent, Position target) {
        Objects.requireNonNull(agent);
        Objects.requireNonNull(target);
        agent.setPosition(behavior.nextPosition(agent, target));
    }
    public Position suggest(GameObject agent, Position target) {
        return behavior.nextPosition(Objects.requireNonNull(agent), Objects.requireNonNull(target));
    }
}
