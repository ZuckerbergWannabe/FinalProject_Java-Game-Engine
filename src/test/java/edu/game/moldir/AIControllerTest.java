package edu.game.moldir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import edu.game.core.GameObject;
import edu.game.core.Position;
import edu.game.moldir.strategy.AIController;
import edu.game.moldir.strategy.AggressiveBehavior;
import edu.game.moldir.strategy.DefensiveBehavior;
import edu.game.moldir.strategy.PatrolBehavior;

class AIControllerTest {
    @Test void aggressiveMovesTowardTarget() {
        GameObject enemy = new GameObject("e", "ENEMY", new Position(0, 0), 50);
        AIController controller = new AIController(new AggressiveBehavior());
        controller.update(enemy, new Position(5, 5));
        assertEquals(new Position(1, 1), enemy.position());
    }

    @Test void defensiveMovesAwayFromTarget() {
        GameObject enemy = new GameObject("e", "ENEMY", new Position(0, 0), 50);
        AIController controller = new AIController(new DefensiveBehavior());
        controller.update(enemy, new Position(5, 5));
        assertEquals(new Position(-1, -1), enemy.position());
    }

    @Test void patrolAlwaysMovesRight() {
        GameObject enemy = new GameObject("e", "ENEMY", new Position(0, 0), 50);
        AIController controller = new AIController(new PatrolBehavior());
        // Patrol ignores target and just moves right
        controller.update(enemy, new Position(10, 10)); 
        assertEquals(new Position(1, 0), enemy.position());
    }
}
