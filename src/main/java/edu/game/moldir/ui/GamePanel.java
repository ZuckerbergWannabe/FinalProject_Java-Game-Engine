package edu.game.moldir.ui;

import edu.game.askar.builder.GameConfig;
import edu.game.core.Dungeon;
import edu.game.core.GameEngine;
import edu.game.core.GameStatus;
import edu.game.moldir.facade.GameFacade;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/** A playable top-down, turn-based roguelike, rendered entirely with Java2D. */
public final class GamePanel extends JPanel {
    public static final int WIDTH = 1180;
    public static final int HEIGHT = 800;
    private static final int TILE = 28;
    private static final int MAP_X = 29;
    private static final int MAP_Y = 88;
    private static final int MAP_W = Dungeon.WIDTH * TILE;
    private static final int MAP_H = Dungeon.HEIGHT * TILE;
    private static final int SIDE_X = 866;
    private static final int SIDE_W = 282;
    private static final int[] AI_MODES = {0, 1, 2, 3};
    private static final String[] AI_NAMES = {"MIXED", "AGGRESSIVE", "DEFENSIVE", "PATROL"};

    private static final Color BACKGROUND = color(12, 16, 28);
    private static final Color PANEL = color(21, 27, 44);
    private static final Color PANEL_DARK = color(17, 23, 39);
    private static final Color BORDER = color(52, 63, 90);
    private static final Color MAIN_TEXT = color(231, 235, 246);
    private static final Color MUTED = color(137, 150, 176);
    private static final Color GOLD = color(244, 196, 107);
    private static final Color CYAN = color(109, 219, 225);
    private static final Color RED = color(239, 105, 119);
    private static final Color GREEN = color(99, 206, 163);

    private final GameFacade facade;
    private boolean intro;
    private int aiModeIndex;
    private long createdAt = System.currentTimeMillis();

    public GamePanel() { this(true); }

    public GamePanel(boolean intro) {
        GameConfig config = new GameConfig.Builder()
                .title("PATTERN QUEST: THE LOST RELIC")
                .resolution(WIDTH, HEIGHT)
                .startingEnemies(2)
                .build();
        facade = new GameFacade(config);
        facade.startGame();
        this.intro = intro;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BACKGROUND);
        setFocusable(true);
        installControls();
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                requestFocusInWindow();
                if (GamePanel.this.intro) {
                    GamePanel.this.intro = false;
                } else if (facade.engine().status() == GameStatus.GAME_OVER
                        || facade.engine().status() == GameStatus.VICTORY) {
                    facade.startGame();
                    aiModeIndex = 0;
                } else if (new Rectangle(SIDE_X + 16, 409, 119, 35).contains(event.getPoint())) {
                    toggleRenderer();
                } else if (new Rectangle(SIDE_X + 145, 409, 120, 35).contains(event.getPoint())) {
                    cycleAI();
                } else if (new Rectangle(SIDE_X + 17, 715, 249, 36).contains(event.getPoint())) {
                    facade.startGame();
                    aiModeIndex = 0;
                }
                repaint();
            }
        });
        new Timer(45, event -> repaint()).start();
    }

    private void installControls() {
        bind(KeyEvent.VK_W, "up", () -> facade.move(0, -1));
        bind(KeyEvent.VK_UP, "arrow-up", () -> facade.move(0, -1));
        bind(KeyEvent.VK_S, "down", () -> facade.move(0, 1));
        bind(KeyEvent.VK_DOWN, "arrow-down", () -> facade.move(0, 1));
        bind(KeyEvent.VK_A, "left", () -> facade.move(-1, 0));
        bind(KeyEvent.VK_LEFT, "arrow-left", () -> facade.move(-1, 0));
        bind(KeyEvent.VK_D, "right", () -> facade.move(1, 0));
        bind(KeyEvent.VK_RIGHT, "arrow-right", () -> facade.move(1, 0));
        bind(KeyEvent.VK_SPACE, "attack", facade::attack);
        bind(KeyEvent.VK_H, "heal", facade::heal);
        bind(KeyEvent.VK_E, "stairs", facade::interact);
        bind(KeyEvent.VK_PERIOD, "wait", facade::waitTurn);
        bind(KeyEvent.VK_V, "graphics", this::toggleRenderer);
        bind(KeyEvent.VK_T, "strategy", this::cycleAI);
        bind(KeyEvent.VK_P, "pause", facade::pause);
        bind(KeyEvent.VK_R, "restart", () -> {
            facade.startGame();
            aiModeIndex = 0;
        });
        bind(KeyEvent.VK_ENTER, "start", () -> {
            if (facade.engine().status() == GameStatus.VICTORY
                    || facade.engine().status() == GameStatus.GAME_OVER) {
                facade.startGame();
                aiModeIndex = 0;
            }
        });
        bind(KeyEvent.VK_ESCAPE, "esc", facade::pause);
    }

    private void bind(int key, String actionName, Runnable command) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(key, 0), actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent event) {
                if (intro) {
                    intro = false;
                    repaint();
                    return;
                }
                command.run();
                repaint();
            }
        });
    }

    private void toggleRenderer() {
        facade.switchGraphics(facade.engine().rendererName().equals("PIXEL 2D") ? "3D" : "2D");
    }

    private void cycleAI() {
        aiModeIndex = (aiModeIndex + 1) % AI_MODES.length;
        facade.changeEnemyAI(AI_NAMES[aiModeIndex]);
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        drawBackdrop(g);
        drawTopBar(g);
        drawMap(g);
        drawSidebar(g);
        drawLog(g);
        if (intro) drawOverlay(g, "PATTERN QUEST", "THE LOST RELIC",
                "ENTER THE DUNGEON", "PRESS ENTER / CLICK TO PLAY", false);
        else if (facade.engine().status() == GameStatus.PAUSED) {
            drawOverlay(g, "GAME PAUSED", "TAKE A BREATH",
                    "PRESS P TO RESUME", "", false);
        } else if (facade.engine().status() == GameStatus.GAME_OVER) {
            drawOverlay(g, "YOU HAVE FALLEN", "THE DUNGEON ENDURES",
                    "PRESS ENTER TO TRY AGAIN", "YOU REACHED FLOOR " + facade.engine().floor(), false);
        } else if (facade.engine().status() == GameStatus.VICTORY) {
            drawOverlay(g, "THE RELIC IS YOURS", "DUNGEON CONQUERED",
                    "PRESS ENTER TO PLAY AGAIN", "" + facade.engine().kills()
                            + " ENEMIES DEFEATED  •  " + facade.engine().gold() + " GOLD", true);
        }
        g.dispose();
    }

    private void drawBackdrop(Graphics2D g) {
        g.setColor(new Color(27, 36, 58, 60));
        for (int i = 0; i < 110; i++) {
            int x = (i * 127 + 53) % WIDTH;
            int y = (i * 71 + 19) % HEIGHT;
            g.fillRect(x, y, i % 3 == 0 ? 2 : 1, i % 3 == 0 ? 2 : 1);
        }
        g.setColor(new Color(66, 115, 137, 45));
        g.fillOval(-260, -300, 680, 530);
    }

    private void drawTopBar(Graphics2D g) {
        g.setColor(PANEL_DARK);
        g.fillRoundRect(17, 13, 1145, 60, 16, 16);
        border(g, 17, 13, 1145, 60, 16);
        g.setColor(GOLD);
        g.fillRoundRect(29, 27, 7, 28, 4, 4);
        text(g, "PATTERN", 48, 37, 17, GOLD, true);
        text(g, "QUEST", 141, 37, 17, MAIN_TEXT, true);
        text(g, "THE LOST RELIC", 49, 56, 10, MUTED, true);
        text(g, "DUNGEON  /  FLOOR  " + facade.engine().floor() + "  OF  " + GameEngine.FINAL_FLOOR,
                517, 46, 13, MAIN_TEXT, true);
        g.setColor(new Color(38, 50, 75));
        g.fillRoundRect(898, 26, 247, 34, 12, 12);
        text(g, "◆  DESIGN PATTERNS  ×  6", 912, 48, 12, CYAN, true);
    }

    private void drawMap(Graphics2D g) {
        g.setColor(new Color(5, 8, 16, 160));
        g.fillRoundRect(MAP_X - 8, MAP_Y - 7, MAP_W + 16, MAP_H + 15, 13, 13);
        g.setColor(BORDER);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(MAP_X - 8, MAP_Y - 7, MAP_W + 16, MAP_H + 15, 13, 13);
        Graphics2D clip = (Graphics2D) g.create();
        clip.setClip(new Rectangle(MAP_X, MAP_Y, MAP_W, MAP_H));
        // No external sprite resources: Bridge picks the renderer for every tile/entity.
        facade.engine().paintWorld(clip, MAP_X, MAP_Y, TILE, System.currentTimeMillis() - createdAt);
        clip.dispose();
        g.setColor(new Color(86, 218, 209, 110));
        g.fillRect(MAP_X, MAP_Y, MAP_W, 2);
    }

    private void drawSidebar(Graphics2D g) {
        g.setColor(PANEL);
        g.fillRoundRect(SIDE_X, 82, SIDE_W, 584, 16, 16);
        border(g, SIDE_X, 82, SIDE_W, 584, 16);
        text(g, "THE ADVENTURER", SIDE_X + 18, 110, 12, GOLD, true);
        g.setColor(PANEL_DARK);
        g.fillRoundRect(SIDE_X + 17, 122, 248, 84, 12, 12);
        text(g, "HEALTH POINTS", SIDE_X + 30, 148, 11, MUTED, true);
        GameEngine engine = facade.engine();
        String hp = engine.player().health() + " / " + engine.player().maxHealth();
        rightText(g, hp, SIDE_X + 250, 149, 13, MAIN_TEXT, true);
        g.setColor(new Color(57, 42, 58));
        g.fillRoundRect(SIDE_X + 30, 161, 221, 15, 7, 7);
        g.setColor(RED);
        int bar = (int) Math.round(221.0 * engine.player().health() / engine.player().maxHealth());
        g.fillRoundRect(SIDE_X + 30, 161, bar, 15, 7, 7);
        g.setColor(new Color(255, 255, 255, 45));
        g.fillRoundRect(SIDE_X + 31, 161, Math.max(0, bar - 2), 5, 5, 5);
        text(g, "◆  " + (engine.player().health() > 50 ? "READY FOR BATTLE" : "LOW HEALTH"),
                SIDE_X + 30, 194, 10, engine.player().health() > 50 ? GREEN : RED, true);

        text(g, "INVENTORY", SIDE_X + 18, 232, 12, GOLD, true);
        infoCell(g, SIDE_X + 17, 244, 119, "COINS", String.valueOf(engine.gold()), GOLD);
        infoCell(g, SIDE_X + 146, 244, 119, "POTIONS", String.valueOf(engine.potions()), CYAN);
        infoCell(g, SIDE_X + 17, 307, 119, "CRYSTALS", String.valueOf(engine.crystals()), new Color(203, 158, 246));
        infoCell(g, SIDE_X + 146, 307, 119, "KILLS", String.valueOf(engine.kills()), RED);
        text(g, "SYSTEM SWITCHES", SIDE_X + 18, 394, 12, GOLD, true);
        button(g, SIDE_X + 16, 409, 119, 35, engine.rendererName(), CYAN);
        button(g, SIDE_X + 145, 409, 120, 35, "AI: " + engine.aiMode(), GREEN);
        g.setColor(BORDER);
        g.fillRect(SIDE_X + 17, 460, 248, 1);
        text(g, "CONTROLS", SIDE_X + 18, 485, 12, GOLD, true);
        controlLine(g, "W A S D / ↑↓←→", "MOVE", 509);
        controlLine(g, "SPACE", "ATTACK", 532);
        controlLine(g, "H", "HEAL", 555);
        controlLine(g, "E", "STAIRS", 578);
        controlLine(g, "T / V", "AI / VIEW", 601);
        controlLine(g, "P / R", "PAUSE / RESTART", 624);
        text(g, "STEP INTO ENEMIES TO ATTACK", SIDE_X + 17, 650, 9, MUTED, true);

        g.setColor(PANEL);
        g.fillRoundRect(SIDE_X, 681, SIDE_W, 100, 14, 14);
        border(g, SIDE_X, 681, SIDE_W, 100, 14);
        text(g, "RUN STATUS", SIDE_X + 17, 702, 11, GOLD, true);
        rightText(g, "TURN  " + engine.turns(), SIDE_X + 264, 702, 10, MUTED, true);
        g.setColor(new Color(42, 52, 74));
        g.fillRoundRect(SIDE_X + 17, 715, 249, 36, 8, 8);
        text(g, "↻    NEW RUN  (R)", SIDE_X + 76, 738, 12, MAIN_TEXT, true);
        text(g, "TEAM  •  MOLDIR  /  ASKAR  /  ALMANZOR",
                SIDE_X + 14, 770, 9, MUTED, true);
    }

    private void infoCell(Graphics2D g, int x, int y, int width, String label, String value, Color accent) {
        g.setColor(PANEL_DARK);
        g.fillRoundRect(x, y, width, 53, 11, 11);
        g.setColor(accent);
        g.fillRoundRect(x + 10, y + 14, 4, 25, 3, 3);
        text(g, label, x + 22, y + 20, 10, MUTED, true);
        text(g, value, x + 22, y + 41, 18, MAIN_TEXT, true);
    }

    private void controlLine(Graphics2D g, String key, String action, int y) {
        text(g, key, SIDE_X + 18, y, 11, MAIN_TEXT, true);
        rightText(g, action, SIDE_X + SIDE_W - 18, y, 10, MUTED, true);
    }

    private void button(Graphics2D g, int x, int y, int width, int height, String label, Color color) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        g.fillRoundRect(x, y, width, height, 9, 9);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g.drawRoundRect(x, y, width, height, 9, 9);
        centerText(g, label, x + width / 2, y + 22, 10, color, true);
    }

    private void drawLog(Graphics2D g) {
        g.setColor(PANEL);
        g.fillRoundRect(21, 686, 829, 95, 14, 14);
        border(g, 21, 686, 829, 95, 14);
        text(g, "✦  ADVENTURE LOG", 38, 711, 11, GOLD, true);
        text(g, "CURRENT", 600, 711, 10, MUTED, true);
        String action = facade.engine().lastAction();
        text(g, action.length() > 38 ? action.substring(0, 38) + "..." : action,
                600, 730, 10, CYAN, false);
        var messages = facade.engine().messages();
        int display = Math.min(2, messages.size());
        for (int i = 0; i < display; i++) {
            String message = messages.get(messages.size() - display + i);
            text(g, "• " + message, 39, 732 + i * 20, 11, MUTED, false);
        }
    }

    private void drawOverlay(Graphics2D g, String headline, String subhead,
                             String action, String detail, boolean win) {
        g.setColor(new Color(8, 12, 24, 212));
        g.fillRoundRect(MAP_X, MAP_Y, MAP_W, MAP_H, 4, 4);
        int centerX = MAP_X + MAP_W / 2;
        int centerY = MAP_Y + MAP_H / 2;
        g.setColor(new Color(46, 63, 91));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(centerX - 305, centerY - 166, 610, 329, 21, 21);
        g.setColor(new Color(20, 29, 47, 245));
        g.fillRoundRect(centerX - 302, centerY - 163, 604, 323, 20, 20);
        g.setColor(win ? GREEN : GOLD);
        g.fillRoundRect(centerX - 85, centerY - 105, 170, 3, 3, 3);
        centerText(g, "✦  A PIXEL ROGUELIKE  ✦", centerX, centerY - 127, 11, CYAN, true);
        centerText(g, headline, centerX, centerY - 48, 35, win ? GREEN : MAIN_TEXT, true);
        centerText(g, subhead, centerX, centerY - 16, 13, GOLD, true);
        centerText(g, "FIVE FLOORS  •  THREE ENEMY AI MODES  •  SIX PATTERNS",
                centerX, centerY + 30, 11, MUTED, true);
        g.setColor(new Color(44, 85, 102));
        g.fillRoundRect(centerX - 168, centerY + 53, 336, 48, 11, 11);
        centerText(g, action, centerX, centerY + 84, 14, MAIN_TEXT, true);
        centerText(g, detail, centerX, centerY + 124, 10, MUTED, true);
    }

    private static void border(Graphics2D g, int x, int y, int width, int height, int radius) {
        g.setColor(BORDER);
        g.drawRoundRect(x, y, width, height, radius, radius);
    }

    private static void text(Graphics2D g, String value, int x, int y,
                             int size, Color color, boolean bold) {
        g.setColor(color);
        g.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        g.drawString(value, x, y);
    }

    private static void centerText(Graphics2D g, String value, int x, int y,
                                   int size, Color color, boolean bold) {
        g.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        FontMetrics metrics = g.getFontMetrics();
        text(g, value, x - metrics.stringWidth(value) / 2, y, size, color, bold);
    }

    private static void rightText(Graphics2D g, String value, int rightX, int y,
                                  int size, Color color, boolean bold) {
        g.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        int width = g.getFontMetrics().stringWidth(value);
        text(g, value, rightX - width, y, size, color, bold);
    }

    private static Color color(int red, int green, int blue) { return new Color(red, green, blue); }

    /** Render a real gameplay frame to PNG, also usable in headless CI. */
    public BufferedImage preview() {
        intro = false;
        setSize(WIDTH, HEIGHT);
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        paint(graphics);
        graphics.dispose();
        return image;
    }
}
