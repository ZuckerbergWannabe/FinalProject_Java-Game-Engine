package edu.game.askar;

import edu.game.askar.builder.GameConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuilderTest {
    @Test void constructsOptionalConfiguration() {
        GameConfig config = new GameConfig.Builder().title("Demo")
                .resolution(1920, 1080).startingEnemies(3).soundEnabled(false).build();
        assertEquals("Demo", config.title());
        assertEquals(1920, config.width());
        assertEquals(3, config.startingEnemies());
        assertFalse(config.soundEnabled());
    }
    @Test void rejectsInvalidResolution() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameConfig.Builder().resolution(0, 600).build());
    }
}
