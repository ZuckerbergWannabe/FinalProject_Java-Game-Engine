# Six design patterns in Pattern Quest

## Moldir — Strategy

**Problem:** enemies should behave differently without rewriting the dungeon or adding a long conditional block to the game update.

- `AIBehavior` — the `nextPosition(agent, target)` strategy contract.
- `AggressiveBehavior` — prefers moving toward the hero.
- `DefensiveBehavior` — prefers moving away from the hero.
- `PatrolBehavior` — walks along a simple patrol direction.
- `AIController` — delegates behavior calculation to any strategy.
- `GameEngine.update()` — selects each enemy's strategy and validates moves against walls and collisions; for aggressive AI, BFS avoids obstacles.
- During gameplay, press **T** to dynamically switch the override strategy at runtime; Mixed restores each enemy's assigned style.

**Why Strategy?** New enemy behaviors can be added by implementing `AIBehavior` without changing the interface of the rest of the game.

## Moldir — Facade

- `GameFacade` hides construction and coordination of `GameEngine`, `Renderer2D`/`Renderer3D`, and `LegacyAudioAdapter`.
- `GamePanel` uses simple commands like `facade.move(...)`, `facade.attack()`, `facade.heal()`, `facade.interact()` and `facade.switchGraphics("3D")`.

**Why Facade?** The UI does not need to understand world generation, enemy pathfinding, spawning, sound synthesis, or render implementation details.

## Askar — Factory Method

- `ObjectFactory.create(id, position)` is the creator contract.
- `PlayerFactory`, `EnemyFactory`, `BossFactory`, `LootFactory`, and `ObstacleFactory` implement this contract.
- `SpawnService.spawn(factory, id, position)` creates and registers objects in `GameWorld`.
- The dungeon uses factories to spawn enemies, the final boss, and loot, rather than directly calling `new GameObject(...)` in the engine.

**Why Factory?** Type creation, initial HP, and object categories can be changed independently of the dungeon rules.

## Askar — Builder

- `GameConfig.Builder` creates immutable `GameConfig` values.
- Example: `new GameConfig.Builder().title("Pattern Quest").resolution(1180, 800).startingEnemies(2).soundEnabled(true).build()`.
- `build()` rejects invalid dimensions, negative enemy counts and empty titles.

**Why Builder?** Optional configuration parameters remain readable, validated, and independent of constructor overloads.

## Almanzor — Adapter

- `SoundPlayer.play(soundId)` is the modern engine-facing sound interface.
- `LegacyAudioEngine.playFile(path, volume)` is a deliberately incompatible legacy-style API.
- `LegacyAudioAdapter` converts `play("hit")` into `playFile("assets/sounds/hit.wav", 80)`.
- The legacy implementation synthesizes short sounds, so no WAV files need to be installed; audio failures safely degrade into silence.

**Why Adapter?** The core can integrate an old API without depending directly on its signature.

## Almanzor — Bridge

- `GameVisual` is the abstraction, with `CharacterVisual` and `EnvironmentVisual` extensions.
- `RenderBackend` is the implementation interface.
- `Renderer2D` draws 2D pixel-art tiles and character sprites.
- `Renderer3D` renders pseudo-3D shaded tiles (still a 2D game).
- `GameEngine.paintWorld()` invokes both visual abstractions against whichever renderer is active.
- Press **V** to switch implementations while the game is running; neither characters nor dungeon state are recreated.

**Why Bridge?** Environment/character visuals and rendering methods can evolve independently. A new renderer implements `RenderBackend`, without changing gameplay logic.

## Patterns working together

`GamePanel` → `GameFacade` → `GameEngine` → (`SpawnService` + `AIController` + `CharacterVisual`/`EnvironmentVisual` + `SoundPlayer`).

The point of the project is **integration**: the six patterns are not separate toy examples. All are used in the playable game.
