package edu.game.askar.factory;

import edu.game.core.GameObject;
import edu.game.core.GameWorld;
import edu.game.core.Position;
import java.util.Objects;

public final class SpawnService {
    private final GameWorld world;

    public SpawnService(GameWorld world) { this.world = Objects.requireNonNull(world); }
    public GameObject spawn(ObjectFactory factory, String id, Position position) {
        GameObject object = Objects.requireNonNull(factory).create(id, position);
        world.add(object);
        return object;
    }
}
