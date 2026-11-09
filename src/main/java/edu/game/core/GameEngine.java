package edu.game.core;

import edu.game.almanzor.adapter.SoundPlayer;
import edu.game.almanzor.bridge.CharacterVisual;
import edu.game.almanzor.bridge.EnvironmentVisual;
import edu.game.almanzor.bridge.RenderBackend;
import edu.game.askar.builder.GameConfig;
import edu.game.askar.factory.*;
import edu.game.moldir.strategy.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public final class GameEngine {
    public static final int FINAL_FLOOR = 5;
    private static final int[][] DIRECTIONS = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    private final GameConfig config;
    private final GameWorld world = new GameWorld();
    private final SpawnService spawner = new SpawnService(world);
    private final SoundPlayer soundPlayer;
    private final List<GameObject> enemies = new ArrayList<>();
    private final Map<String, AIBehavior> enemyBehaviors = new HashMap<>();
    private final Deque<String> messages = new ArrayDeque<>();
    private final Random random;
    private final AIBehavior aggressive = new AggressiveBehavior();
    private final AIBehavior defensive = new DefensiveBehavior();
    private final AIBehavior patrol = new PatrolBehavior();

    private AIBehavior overrideBehavior;
    private GameObject player;
    private RenderBackend renderer;
    private Dungeon dungeon;
    private GameStatus status = GameStatus.MENU;
    private int floor = 1;
    private int turns;
    private int kills;
    private int gold;
    private int crystals;
    private int potions = 2;
    private int facingX = 0;
    private int facingY = 1;
    private String lastAction = "Explore the dungeon";

    public GameEngine(GameConfig config, RenderBackend renderer, SoundPlayer soundPlayer) {
        this(config, renderer, soundPlayer, System.nanoTime());
    }

    public GameEngine(GameConfig config, RenderBackend renderer, SoundPlayer soundPlayer, long seed) {
        this.config = Objects.requireNonNull(config);
        this.renderer = Objects.requireNonNull(renderer);
        this.soundPlayer = Objects.requireNonNull(soundPlayer);
        this.random = new Random(seed);
    }

    public void start() {
        floor = 1;
        turns = 0;
        kills = 0;
        gold = 0;
        crystals = 0;
        potions = 2;
        overrideBehavior = null;
        messages.clear();
        newLevel(100);
        status = GameStatus.PLAYING;
        log("Welcome, adventurer. Find the glowing stairs!");
        log("Defeat the Warden on floor 5 to win.");
    }

    private void newLevel(int healthToKeep) {
        world.clear();
        enemies.clear();
        enemyBehaviors.clear();
        dungeon = new Dungeon(random.nextLong());
        player = spawner.spawn(new PlayerFactory(), "hero", dungeon.entrance());
        player.damage(player.maxHealth() - Math.max(1, Math.min(healthToKeep, player.maxHealth())));
        List<Position> empty = new ArrayList<>(dungeon.allFloorPositions());
        Collections.shuffle(empty, random);
        int count = Math.min(2 + floor, Math.max(0, config.startingEnemies() + floor - 1));
        for (int i = 0; i < count; i++) {
            Position pos = pickSpawn(empty, 6);
            if (pos == null) break;
            GameObject enemy = spawner.spawn(new EnemyFactory(), "enemy-" + floor + "-" + i, pos);
            enemies.add(enemy);
            AIBehavior behavior = switch (i % 3) {
                case 0 -> aggressive;
                case 1 -> defensive;
                default -> patrol;
            };
            enemyBehaviors.put(enemy.id(), behavior);
        }
        if (floor == FINAL_FLOOR) {
            Position bossPos = dungeon.exit();
            GameObject boss = spawner.spawn(new BossFactory(), "the-warden", bossPos);
            enemies.add(boss);
            enemyBehaviors.put(boss.id(), aggressive);
        }
        for (int i = 0; i < 7 + floor; i++) {
            Position pos = pickSpawn(empty, 3);
            if (pos == null) break;
            String loot = i % 6 == 0 ? "POTION" : i % 4 == 0 ? "CRYSTAL" : "GOLD";
            spawner.spawn(new LootFactory(loot), "loot-" + floor + "-" + i, pos);
        }
    }

    private Position pickSpawn(List<Position> positions, int minimumDistance) {
        for (int i = 0; i < positions.size(); i++) {
            Position candidate = positions.get(i);
            if (Dungeon.distance(candidate, player.position()) >= minimumDistance
                    && !candidate.equals(dungeon.exit()) && objectAt(candidate, "ENEMY", "BOSS") == null
                    && objectAt(candidate, "POTION", "GOLD", "CRYSTAL") == null) {
                positions.remove(i);
                return candidate;
            }
        }
        return null;
    }

    public void movePlayer(int dx, int dy) {
        if (status != GameStatus.PLAYING || Math.abs(dx) + Math.abs(dy) != 1) return;
        facingX = dx;
        facingY = dy;
        Position target = player.position().move(dx, dy);
        if (!dungeon.walkable(target)) {
            lastAction = "A wall blocks the path";
            return;
        }
        GameObject enemy = objectAt(target, "ENEMY", "BOSS");
        if (enemy != null) {
            strike(enemy);
        } else {
            player.setPosition(target);
            collectLoot();
            lastAction = dungeon.tile(target.x(), target.y()) == Tile.STAIRS
                    ? "Press E to descend the staircase" : "Exploring...";
        }
        finishTurn();
    }

    public void attack() {
        if (status != GameStatus.PLAYING) return;
        GameObject chosen = objectAt(player.position().move(facingX, facingY), "ENEMY", "BOSS");
        if (chosen == null) {
            for (int[] direction : DIRECTIONS) {
                chosen = objectAt(player.position().move(direction[0], direction[1]), "ENEMY", "BOSS");
                if (chosen != null) break;
            }
        }
        if (chosen == null) {
            lastAction = "Your blade cuts through empty air";
            return;
        }
        strike(chosen);
        finishTurn();
    }

    private void strike(GameObject enemy) {
        int damage = 21 + random.nextInt(11) + floor * 2;
        enemy.damage(damage);
        lastAction = "You strike for " + damage + " damage";
        playSound("hit");
        if (!enemy.alive()) {
            enemies.remove(enemy);
            world.remove(enemy);
            enemyBehaviors.remove(enemy.id());
            kills++;
            gold += enemy.type().equals("BOSS") ? 100 : 5;
            log(enemy.type().equals("BOSS") ? "The Warden has fallen!" : "Enemy defeated! +5 gold");
            if (enemy.type().equals("BOSS")) {
                status = GameStatus.VICTORY;
                log("YOU WON! The dungeon is free of darkness.");
                playSound("victory");
            }
        }
    }

    public void drinkPotion() {
        if (status != GameStatus.PLAYING) return;
        if (potions == 0) {
            lastAction = "No potions left";
            return;
        }
        if (player.health() >= player.maxHealth()) {
            lastAction = "Your health is already full";
            return;
        }
        potions--;
        int before = player.health();
        player.heal(40);
        lastAction = "Potion restored " + (player.health() - before) + " HP";
        playSound("potion");
        finishTurn();
    }

    public void interact() {
        if (status != GameStatus.PLAYING) return;
        if (!player.position().equals(dungeon.exit())) {
            lastAction = "Stand on the glowing stairs, then press E";
            return;
        }
        if (floor == FINAL_FLOOR) {
            lastAction = "Defeat the Warden to escape";
            return;
        }
        int hp = player.health();
        floor++;
        newLevel(hp);
        lastAction = "Descended to floor " + floor;
        log("FLOOR " + floor + " — deeper into the ruins");
        if (floor == FINAL_FLOOR) log("The Warden guards the final chamber!");
        playSound("stairs");
    }

    public void waitTurn() {
        if (status != GameStatus.PLAYING) return;
        lastAction = "You wait in silence";
        finishTurn();
    }

    private void collectLoot() {
        List<GameObject> found = new ArrayList<>();
        for (GameObject object : world.objects()) {
            if (!object.position().equals(player.position())) continue;
            switch (object.type()) {
                case "GOLD" -> { gold += 15; lastAction = "Collected 15 gold"; found.add(object); }
                case "CRYSTAL" -> { crystals++; gold += 25; lastAction = "Found a rare crystal (+25 gold)"; found.add(object); }
                case "POTION" -> { potions++; lastAction = "Picked up a healing potion"; found.add(object); }
                default -> { }
            }
        }
        for (GameObject object : found) world.remove(object);
        if (!found.isEmpty()) playSound("pickup");
    }

    private void finishTurn() {
        if (status != GameStatus.PLAYING) return;
        turns++;
        update();
    }

    public void update() {
        ensureStarted();
        if (status != GameStatus.PLAYING) return;
        for (GameObject enemy : List.copyOf(enemies)) {
            int distance = Dungeon.distance(enemy.position(), player.position());
            if (distance == 1) {
                int damage = enemy.type().equals("BOSS") ? 10 + random.nextInt(6)
                        : 3 + random.nextInt(5) + floor;
                player.damage(damage);
                lastAction = enemy.type().equals("BOSS") ? "WARDEN hits you for " + damage
                        : "An enemy hits you for " + damage;
                playSound("hurt");
                if (!player.alive()) {
                    status = GameStatus.GAME_OVER;
                    log("You have fallen on floor " + floor + ".");
                    return;
                }
                continue;
            }
            AIBehavior behavior = overrideBehavior != null ? overrideBehavior
                    : enemyBehaviors.getOrDefault(enemy.id(), aggressive);
            if (behavior == defensive && distance > 5) continue;
            if (behavior == aggressive && distance > 8 && random.nextInt(3) != 0) continue;
            if (behavior == patrol && random.nextInt(3) == 0) continue;
            AIController controller = new AIController(behavior);
            Position preferred = controller.suggest(enemy, player.position());
            Position next = chooseValidEnemyStep(enemy, preferred, behavior);
            if (next != null) enemy.setPosition(next);
        }
    }

    private Position chooseValidEnemyStep(GameObject enemy, Position preferred, AIBehavior behavior) {
        Position current = enemy.position();
        List<Position> candidates = new ArrayList<>();
        for (int[] delta : DIRECTIONS) {
            Position position = current.move(delta[0], delta[1]);
            if (dungeon.walkable(position) && !position.equals(player.position())
                    && !occupiedByOtherEnemy(enemy, position)) candidates.add(position);
        }
        if (candidates.isEmpty()) return null;
        if (behavior == aggressive) {
            Map<Position, Integer> route = distancesFromPlayer();
            candidates.sort(Comparator.comparingInt(p -> route.getOrDefault(p, Integer.MAX_VALUE)));
            Position closest = candidates.get(0);
            if (route.getOrDefault(closest, Integer.MAX_VALUE)
                    < route.getOrDefault(current, Integer.MAX_VALUE)) return closest;
            return null;
        }
        candidates.sort(Comparator.comparingInt(p -> Dungeon.distance(p, preferred)));
        if (behavior == defensive) {
            candidates.sort(Comparator.comparingInt((Position p) -> Dungeon.distance(p, player.position())).reversed());
            return Dungeon.distance(candidates.get(0), player.position()) > Dungeon.distance(current, player.position())
                    ? candidates.get(0) : null;
        }
        return candidates.get(0);
    }

    private Map<Position, Integer> distancesFromPlayer() {
        Map<Position, Integer> distances = new HashMap<>();
        ArrayDeque<Position> queue = new ArrayDeque<>();
        queue.add(player.position());
        distances.put(player.position(), 0);
        while (!queue.isEmpty()) {
            Position current = queue.remove();
            for (int[] delta : DIRECTIONS) {
                Position next = current.move(delta[0], delta[1]);
                if (!dungeon.walkable(next) || distances.containsKey(next)) continue;
                distances.put(next, distances.get(current) + 1);
                queue.add(next);
            }
        }
        return distances;
    }

    private boolean occupiedByOtherEnemy(GameObject actor, Position position) {
        for (GameObject other : enemies) {
            if (other != actor && other.position().equals(position)) return true;
        }
        return false;
    }

    private GameObject objectAt(Position position, String... types) {
        for (GameObject object : world.objects()) {
            if (!object.position().equals(position)) continue;
            for (String type : types) {
                if (object.type().equals(type)) return object;
            }
        }
        return null;
    }

    public void paintWorld(Graphics2D graphics, int offsetX, int offsetY, int tileSize, long time) {
        ensureStarted();
        EnvironmentVisual environment = new EnvironmentVisual(renderer);
        CharacterVisual characters = new CharacterVisual(renderer);
        for (int y = 0; y < Dungeon.HEIGHT; y++) {
            for (int x = 0; x < Dungeon.WIDTH; x++) {
                environment.paintTile(graphics, dungeon.tile(x, y), offsetX + x * tileSize,
                        offsetY + y * tileSize, tileSize, x, y, time);
            }
        }
        for (GameObject object : world.objects()) {
            int x = offsetX + object.position().x() * tileSize;
            int y = offsetY + object.position().y() * tileSize;
            characters.paintObject(graphics, object, x, y, tileSize, time);
        }
    }

    public List<String> renderFrame() {
        ensureStarted();
        List<String> frame = new ArrayList<>();
        CharacterVisual characters = new CharacterVisual(renderer);
        for (GameObject object : world.objects()) {
            if (object.type().equals("PLAYER") || object.type().equals("ENEMY") || object.type().equals("BOSS")) {
                frame.add(characters.render(object));
            }
        }
        return frame;
    }

    public void pauseOrResume() {
        if (status == GameStatus.PLAYING) status = GameStatus.PAUSED;
        else if (status == GameStatus.PAUSED) status = GameStatus.PLAYING;
    }

    public void setRenderer(RenderBackend renderer) { this.renderer = Objects.requireNonNull(renderer); }
    public void setAIBehavior(AIBehavior behavior) { overrideBehavior = Objects.requireNonNull(behavior); }
    public void resetAIBehavior() { overrideBehavior = null; }
    public String aiMode() { return overrideBehavior == null ? "MIXED" : overrideBehavior.name().toUpperCase(); }
    public String rendererName() { return renderer.getClass().getSimpleName().equals("Renderer2D") ? "PIXEL 2D" : "DEPTH MODE"; }
    public String playSound(String id) { return config.soundEnabled() ? soundPlayer.play(id) : "Sound disabled"; }
    public GameWorld world() { return world; }
    public GameObject player() { ensureStarted(); return player; }
    public List<GameObject> enemies() { return List.copyOf(enemies); }
    public GameConfig config() { return config; }
    public Dungeon dungeon() { ensureStarted(); return dungeon; }
    public GameStatus status() { return status; }
    public int floor() { return floor; }
    public int turns() { return turns; }
    public int kills() { return kills; }
    public int gold() { return gold; }
    public int crystals() { return crystals; }
    public int potions() { return potions; }
    public String lastAction() { return lastAction; }
    public List<String> messages() { return List.copyOf(messages); }
    public int facingX() { return facingX; }
    public int facingY() { return facingY; }
    private void log(String message) {
        if (messages.size() == 5) messages.removeFirst();
        messages.addLast(message);
    }
    private void ensureStarted() {
        if (player == null) throw new IllegalStateException("Call start() first");
    }
}
