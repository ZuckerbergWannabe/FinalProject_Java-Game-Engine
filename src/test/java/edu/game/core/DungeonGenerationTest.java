package edu.game.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DungeonGenerationTest {
    @Test void everyGeneratedFloorHasConnectedEntranceAndExit() {
        for (int seed = 0; seed < 200; seed++) {
            Dungeon dungeon = new Dungeon(seed);
            assertTrue(dungeon.roomCenters().size() >= 2, "seed " + seed);
            assertNotEquals(dungeon.entrance(), dungeon.exit());
            assertEquals(Tile.STAIRS, dungeon.tile(dungeon.exit().x(), dungeon.exit().y()));
            assertTrue(reachable(dungeon), "Disconnected level with seed " + seed);
        }
    }

    @Test void mapHasSolidOuterBoundary() {
        Dungeon dungeon = new Dungeon(1);
        for (int x = 0; x < Dungeon.WIDTH; x++) {
            assertEquals(Tile.WALL, dungeon.tile(x, 0));
            assertEquals(Tile.WALL, dungeon.tile(x, Dungeon.HEIGHT - 1));
        }
        for (int y = 0; y < Dungeon.HEIGHT; y++) {
            assertEquals(Tile.WALL, dungeon.tile(0, y));
            assertEquals(Tile.WALL, dungeon.tile(Dungeon.WIDTH - 1, y));
        }
    }

    private boolean reachable(Dungeon dungeon) {
        Set<Position> visited = new HashSet<>();
        ArrayDeque<Position> queue = new ArrayDeque<>();
        queue.add(dungeon.entrance());
        visited.add(dungeon.entrance());
        while (!queue.isEmpty()) {
            Position current = queue.remove();
            if (current.equals(dungeon.exit())) return true;
            for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                Position next = current.move(d[0], d[1]);
                if (dungeon.walkable(next) && visited.add(next)) queue.add(next);
            }
        }
        return false;
    }
}
