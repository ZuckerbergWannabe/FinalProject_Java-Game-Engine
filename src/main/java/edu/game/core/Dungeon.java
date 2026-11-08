package edu.game.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Generates a connected, room-and-corridor dungeon for each floor. */
public final class Dungeon {
    public static final int WIDTH = 29;
    public static final int HEIGHT = 21;

    private final Tile[][] tiles = new Tile[HEIGHT][WIDTH];
    private final List<Room> rooms = new ArrayList<>();
    private final Random random;
    private final Position entrance;
    private final Position exit;

    public Dungeon(long seed) {
        random = new Random(seed);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tiles[y][x] = Tile.WALL;
            }
        }
        generateRooms();
        entrance = rooms.get(0).center();
        Position farthest = entrance;
        int best = -1;
        for (Room room : rooms) {
            Position center = room.center();
            int distance = distance(entrance, center);
            if (distance > best) {
                best = distance;
                farthest = center;
            }
        }
        exit = farthest;
        tiles[exit.y()][exit.x()] = Tile.STAIRS;
    }

    private void generateRooms() {
        for (int attempt = 0; attempt < 240 && rooms.size() < 9; attempt++) {
            int width = 4 + random.nextInt(5);
            int height = 4 + random.nextInt(3);
            int x = 1 + random.nextInt(WIDTH - width - 2);
            int y = 1 + random.nextInt(HEIGHT - height - 2);
            Room room = new Room(x, y, width, height);
            boolean collision = false;
            for (Room other : rooms) {
                if (room.overlapsWithPadding(other)) {
                    collision = true;
                    break;
                }
            }
            if (collision) continue;
            carveRoom(room);
            if (!rooms.isEmpty()) connect(rooms.get(rooms.size() - 1).center(), room.center());
            rooms.add(room);
        }
        if (rooms.size() < 2) {
            // A deterministic, fully playable fallback if random placement was unusually sparse.
            rooms.clear();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) tiles[y][x] = Tile.WALL;
            }
            Room left = new Room(2, 3, 8, 7);
            Room right = new Room(19, 11, 8, 7);
            rooms.add(left);
            rooms.add(right);
            carveRoom(left);
            carveRoom(right);
            connect(left.center(), right.center());
        }
    }

    private void carveRoom(Room room) {
        for (int y = room.y(); y < room.y() + room.height(); y++) {
            for (int x = room.x(); x < room.x() + room.width(); x++) tiles[y][x] = Tile.FLOOR;
        }
    }

    private void connect(Position a, Position b) {
        if (random.nextBoolean()) {
            carveHorizontal(a.x(), b.x(), a.y());
            carveVertical(a.y(), b.y(), b.x());
        } else {
            carveVertical(a.y(), b.y(), a.x());
            carveHorizontal(a.x(), b.x(), b.y());
        }
    }

    private void carveHorizontal(int from, int to, int y) {
        for (int x = Math.min(from, to); x <= Math.max(from, to); x++) tiles[y][x] = Tile.FLOOR;
    }

    private void carveVertical(int from, int to, int x) {
        for (int y = Math.min(from, to); y <= Math.max(from, to); y++) tiles[y][x] = Tile.FLOOR;
    }

    public Tile tile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return Tile.WALL;
        return tiles[y][x];
    }

    public boolean walkable(Position position) {
        return tile(position.x(), position.y()) != Tile.WALL;
    }

    public Position entrance() { return entrance; }
    public Position exit() { return exit; }
    public List<Position> roomCenters() {
        List<Position> centers = new ArrayList<>();
        for (Room room : rooms) centers.add(room.center());
        return List.copyOf(centers);
    }

    public List<Position> allFloorPositions() {
        List<Position> floor = new ArrayList<>();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (walkable(new Position(x, y))) floor.add(new Position(x, y));
            }
        }
        return floor;
    }

    public static int distance(Position first, Position second) {
        return Math.abs(first.x() - second.x()) + Math.abs(first.y() - second.y());
    }

    private record Room(int x, int y, int width, int height) {
        Position center() { return new Position(x + width / 2, y + height / 2); }
        boolean overlapsWithPadding(Room other) {
            return x <= other.x + other.width + 1 && x + width + 1 >= other.x
                    && y <= other.y + other.height + 1 && y + height + 1 >= other.y;
        }
    }
}
