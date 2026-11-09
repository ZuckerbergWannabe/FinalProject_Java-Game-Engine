package edu.game;

import java.awt.GraphicsEnvironment;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.game.almanzor.adapter.LegacyAudioAdapter;
import edu.game.almanzor.adapter.LegacyAudioEngine;
import edu.game.almanzor.bridge.Renderer2D;
import edu.game.askar.builder.GameConfig;
import edu.game.core.Dungeon;
import edu.game.core.GameEngine;
import edu.game.core.GameStatus;
import edu.game.core.Position;
import edu.game.moldir.facade.GameFacade;
import edu.game.moldir.ui.GamePanel;

public final class Main {
    private Main() { }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--smoke-test")) {
            smokeTest();
            return;
        }
        if (args.length > 0 && args[0].equals("--preview")) {
            String filename = args.length > 1 ? args[1] : "pattern-quest-preview.png";
            ImageIO.write(new GamePanel(false).preview(), "png", new File(filename));
            System.out.println("Wrote screenshot: " + new File(filename).getAbsolutePath());
            System.exit(0);
        }
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("No graphical desktop is available. Run on a computer with a display.");
            System.err.println("You can still use: java -jar PatternQuest.jar --smoke-test");
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pattern Quest — The Lost Relic");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            GamePanel panel = new GamePanel();
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }

    private static void smokeTest() {
        GameConfig config = new GameConfig.Builder().soundEnabled(false).build();
        GameEngine game = new GameEngine(config, new Renderer2D(),
                new LegacyAudioAdapter(new LegacyAudioEngine()), 12345L);
        game.start();
        require(game.dungeon().walkable(game.player().position()), "player spawn");
        require(game.dungeon().roomCenters().size() >= 2, "procedural rooms");
        require(game.enemies().size() >= 2, "enemies spawned");
        require(reachable(game.dungeon(), game.player().position(), game.dungeon().exit()), "stairs reachable");
        for (int i = 0; i < 18 && game.status() == GameStatus.PLAYING; i++) {
            game.movePlayer(i % 4 == 0 ? 1 : i % 4 == 2 ? -1 : 0,
                    i % 4 == 1 ? 1 : i % 4 == 3 ? -1 : 0);
        }
        require(game.player().health() >= 0, "valid hitpoints");
        require(game.renderFrame().get(0).contains("sprite"), "render backend");
        GameFacade facade = new GameFacade(config);
        facade.startGame();
        facade.switchGraphics("3D");
        require(facade.drawFrame().get(0).contains("mesh"), "Bridge graphics switch");
        facade.changeEnemyAI("DEFENSIVE");
        require(facade.engine().aiMode().equals("DEFENSIVE"), "Strategy switching");
        System.out.println("PASS: playable dungeon generation, connected rooms, enemies, turns, combat bounds, Bridge, Strategy");
    }

    private static boolean reachable(Dungeon dungeon, Position start, Position finish) {
        java.util.Set<Position> visited = new java.util.HashSet<>();
        java.util.ArrayDeque<Position> queue = new java.util.ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Position current = queue.remove();
            if (current.equals(finish)) return true;
            for (int[] direction : new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}}) {
                Position next = current.move(direction[0], direction[1]);
                if (dungeon.walkable(next) && visited.add(next)) queue.add(next);
            }
        }
        return false;
    }

    private static void require(boolean condition, String label) {
        if (!condition) throw new IllegalStateException("Smoke test failed: " + label);
    }
}
