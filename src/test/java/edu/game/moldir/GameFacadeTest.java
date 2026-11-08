package edu.game.moldir;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.game.askar.builder.GameConfig;
import edu.game.moldir.facade.GameFacade;

class GameFacadeTest {

    @Test void coordinatesSubsystemsThroughSimpleApi() {
        GameFacade facade = new GameFacade(new GameConfig.Builder().startingEnemies(1).build());
        facade.startGame();
        
        // Verifies the facade hides the underlying setup (Player + 1 Enemy)
        assertEquals(2, facade.drawFrame().size());
        
        facade.changeEnemyAI("PATROL");
        facade.updateGame();
        
        // Verifies the facade correctly routes to the Bridge implementation
        facade.switchGraphics("3D");
        assertTrue(facade.drawFrame().get(0).startsWith("[3D]"));
        
        // Verifies the facade correctly routes to the Adapter implementation
        assertTrue(facade.playEffect("spawn").contains("spawn.wav"));
    }

    @Test void switchingGraphicsUpdatesRenderEngine() {
        GameFacade facade = new GameFacade(new GameConfig.Builder().startingEnemies(1).build());
        facade.startGame();
        
        // Starts as 2D by default in the constructor
        assertTrue(facade.drawFrame().get(0).startsWith("[2D]"));
        
        // Switch to 3D
        facade.switchGraphics("3D");
        assertTrue(facade.drawFrame().get(0).startsWith("[3D]"));
        
        // Switch back to 2D
        facade.switchGraphics("2D");
        assertTrue(facade.drawFrame().get(0).startsWith("[2D]"));
    }

    @Test void invalidGraphicsModeThrowsException() {
        GameFacade facade = new GameFacade(new GameConfig.Builder().startingEnemies(1).build());
        facade.startGame();
        
        // Tests the default case in the switchGraphics method
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facade.switchGraphics("HOLOGRAM");
        });
        assertTrue(exception.getMessage().contains("Unknown graphics mode"));
    }

    @Test void changingAIModeUpdatesEnemyBehavior() {
        GameFacade facade = new GameFacade(new GameConfig.Builder().startingEnemies(1).build());
        facade.startGame();
        
        // Change to Defensive
        facade.changeEnemyAI("DEFENSIVE");
        facade.updateGame(); // Enemies move away
        
        // Change to Aggressive
        facade.changeEnemyAI("AGGRESSIVE");
        facade.updateGame(); // Enemies move toward
        
        // Verify the engine accepts multiple strategy swaps without crashing
        assertDoesNotThrow(() -> facade.updateGame());
    }

    @Test void invalidAIModeThrowsException() {
        GameFacade facade = new GameFacade(new GameConfig.Builder().startingEnemies(1).build());
        facade.startGame();
        
        // Tests the default case in the changeEnemyAI method
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facade.changeEnemyAI("SUICIDAL");
        });
        assertTrue(exception.getMessage().contains("Unknown AI mode"));
    }
}