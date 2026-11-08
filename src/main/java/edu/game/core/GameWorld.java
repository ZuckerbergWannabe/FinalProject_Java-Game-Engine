package edu.game.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameWorld {
    private final List<GameObject> objects = new ArrayList<>();

    public void add(GameObject object) {
        Objects.requireNonNull(object);
        if (objects.stream().anyMatch(existing -> existing.id().equals(object.id()))) {
            throw new IllegalArgumentException("Duplicate object id: " + object.id());
        }
        objects.add(object);
    }

    public void remove(GameObject object) { objects.remove(object); }
    public List<GameObject> objects() { return Collections.unmodifiableList(objects); }
    public void clear() { objects.clear(); }
}
