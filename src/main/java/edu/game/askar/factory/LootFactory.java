package edu.game.askar.factory;

import edu.game.core.GameObject;
import edu.game.core.Position;
import java.util.Locale;

public final class LootFactory implements ObjectFactory {
    private final String kind;

    public LootFactory(String kind) {
        kind = kind.toUpperCase(Locale.ROOT);
        if (!kind.equals("GOLD") && !kind.equals("POTION") && !kind.equals("CRYSTAL")) {
            throw new IllegalArgumentException("Unknown loot kind: " + kind);
        }
        this.kind = kind;
    }

    @Override public GameObject create(String id, Position position) {
        return new GameObject(id, kind, position, 1);
    }
}
