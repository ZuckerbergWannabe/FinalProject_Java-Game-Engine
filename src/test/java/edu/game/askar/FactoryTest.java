package edu.game.askar;

import edu.game.askar.factory.EnemyFactory;
import edu.game.askar.factory.ObstacleFactory;
import edu.game.askar.factory.PlayerFactory;
import edu.game.askar.factory.SpawnService;
import edu.game.core.GameWorld;
import edu.game.core.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactoryTest {
    @Test void factorySpawnsCorrectTypes() {
        GameWorld world = new GameWorld();
        SpawnService spawner = new SpawnService(world);
        assertEquals("PLAYER", spawner.spawn(new PlayerFactory(), "p", new Position(0, 0)).type());
        assertEquals(50, spawner.spawn(new EnemyFactory(), "e", new Position(1, 0)).health());
        assertEquals("OBSTACLE", spawner.spawn(new ObstacleFactory(), "o", new Position(1, 1)).type());
        assertEquals(3, world.objects().size());
    }
    @Test void duplicateIdsAreRejected() {
        SpawnService spawner = new SpawnService(new GameWorld());
        spawner.spawn(new PlayerFactory(), "same", new Position(0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> spawner.spawn(new EnemyFactory(), "same", new Position(1, 1)));
    }
}
