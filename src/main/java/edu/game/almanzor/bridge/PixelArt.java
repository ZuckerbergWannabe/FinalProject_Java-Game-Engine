package edu.game.almanzor.bridge;

import edu.game.core.GameObject;
import edu.game.core.Tile;

import java.awt.*;

final class PixelArt {
    private static final Color VOID = new Color(13, 18, 30);
    private static final Color STONE = new Color(38, 46, 67);
    private static final Color STONE_LIGHT = new Color(61, 70, 93);
    private static final Color FLOOR = new Color(25, 32, 49);
    private static final Color FLOOR_LIGHT = new Color(32, 41, 61);
    private static final Color GOLD = new Color(247, 196, 94);
    private static final Color CYAN = new Color(92, 223, 225);
    private static final Color VIOLET = new Color(193, 146, 250);

    private PixelArt() { }

    static void tile(Graphics2D graphics, Tile tile, int x, int y, int size,
                     int mapX, int mapY, boolean depth, long time) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        double scale = size / 28.0;
        g.scale(scale, scale);
        long noise = ((long) mapX * 73856093L) ^ ((long) mapY * 19349663L);
        int pattern = (int) (Math.abs(noise) % 6);
        g.setColor(VOID);
        g.fillRect(0, 0, 28, 28);
        if (tile == Tile.WALL) {
            g.setColor(new Color(17, 23, 37));
            g.fillRect(0, 23, 28, 5);
            g.setColor(STONE);
            g.fillRect(0, 1, 28, depth ? 18 : 23);
            g.setColor(new Color(47, 56, 78));
            g.fillRect(1, 2, 26, 4);
            g.setColor(new Color(27, 34, 51));
            g.fillRect(0, 12, 28, 2);
            g.fillRect(12, 0, 2, 12);
            g.fillRect(21, 14, 2, 10);
            g.setColor(STONE_LIGHT);
            g.fillRect(2, 2, 10, 1);
            g.fillRect(15, 15, 6, 1);
            if (pattern == 2) {
                g.setColor(new Color(80, 88, 111));
                g.fillRect(7, 17, 1, 4);
                g.fillRect(8, 20, 3, 1);
            }
            if (depth) {
                g.setColor(new Color(26, 30, 49));
                g.fillRect(0, 19, 28, 8);
                g.setColor(new Color(67, 74, 101));
                g.fillRect(0, 18, 28, 2);
            } else {
                g.setColor(new Color(21, 27, 43));
                g.fillRect(0, 24, 28, 4);
            }
        } else {
            g.setColor(pattern % 3 == 0 ? FLOOR_LIGHT : FLOOR);
            g.fillRect(0, 0, 28, 28);
            g.setColor(new Color(30, 39, 56));
            g.drawRect(0, 0, 27, 27);
            g.setColor(new Color(43, 52, 71));
            if (pattern == 0) {
                g.fillRect(4, 5, 7, 1);
                g.fillRect(3, 6, 1, 3);
                g.fillRect(20, 18, 4, 1);
            } else if (pattern == 1) {
                g.fillRect(17, 5, 5, 1);
                g.fillRect(10, 21, 6, 1);
            } else if (pattern == 3) {
                g.fillRect(6, 19, 5, 1);
                g.fillRect(19, 8, 2, 2);
            }
            if (depth) {
                g.setColor(new Color(54, 59, 77));
                g.fillRect(1, 1, 26, 2);
                g.setColor(new Color(13, 19, 34));
                g.fillRect(1, 25, 26, 2);
            }
        }
        if (tile == Tile.STAIRS) {
            int shine = 120 + (int) (35 * (1 + Math.sin(time / 350.0)));
            g.setColor(new Color(43, 91, 104, 90));
            g.fillOval(0, 0, 28, 28);
            g.setColor(new Color(30, 102, 124));
            g.fillRoundRect(4, 4, 20, 20, 5, 5);
            g.setColor(new Color(42, shine, 194));
            g.drawRoundRect(4, 4, 19, 19, 4, 4);
            g.setColor(new Color(15, 43, 61));
            g.fillRect(7, 8, 14, 4);
            g.fillRect(9, 13, 12, 4);
            g.fillRect(11, 18, 10, 3);
            g.setColor(CYAN);
            g.fillRect(7, 8, 14, 1);
            g.fillRect(9, 13, 12, 1);
            g.fillRect(11, 18, 10, 1);
        }
        g.dispose();
    }

    static void object(Graphics2D graphics, GameObject object, int x, int y, int size,
                       boolean depth, long time) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.translate(x, y);
        g.scale(size / 28.0, size / 28.0);
        if (depth) g.translate(0, -3);
        int bounce = (int) Math.round(Math.sin(time / 280.0 + object.position().x()) * 1.0);
        switch (object.type()) {
            case "PLAYER" -> player(g, bounce);
            case "ENEMY" -> enemy(g, bounce);
            case "BOSS" -> boss(g, bounce);
            case "GOLD" -> coin(g, time);
            case "POTION" -> potion(g, time);
            case "CRYSTAL" -> crystal(g, time);
            default -> { }
        }
        if ((object.type().equals("ENEMY") || object.type().equals("BOSS"))
                && object.health() < object.maxHealth()) {
            g.setColor(new Color(18, 22, 34));
            g.fillRoundRect(3, 1, 22, 4, 1, 1);
            g.setColor(object.type().equals("BOSS") ? VIOLET : new Color(242, 109, 115));
            g.fillRect(4, 2, Math.max(0, 20 * object.health() / object.maxHealth()), 2);
        }
        g.dispose();
    }

    private static void player(Graphics2D g, int offset) {
        g.setColor(new Color(8, 12, 24, 150));
        g.fillOval(4, 23, 20, 4);
        g.translate(0, offset);
        g.setColor(new Color(43, 51, 85));
        g.fillRect(9, 21, 4, 5);
        g.fillRect(17, 21, 4, 5);
        g.setColor(new Color(76, 133, 178));
        g.fillRect(7, 11, 15, 12);
        g.setColor(new Color(44, 75, 123));
        g.fillRect(7, 17, 4, 6);
        g.fillRect(19, 17, 4, 6);
        g.setColor(new Color(234, 177, 131));
        g.fillRect(10, 7, 11, 9);
        g.setColor(new Color(48, 65, 124));
        g.fillRect(8, 4, 14, 5);
        g.fillRect(7, 6, 4, 8);
        g.fillRect(19, 6, 4, 8);
        g.setColor(new Color(104, 185, 218));
        g.fillRect(9, 4, 11, 2);
        g.setColor(new Color(23, 33, 61));
        g.fillRect(12, 11, 2, 2);
        g.fillRect(18, 11, 2, 2);
        g.setColor(GOLD);
        g.fillRect(13, 17, 6, 2);
        g.setColor(new Color(196, 208, 234));
        g.fillRect(23, 10, 2, 13);
        g.setColor(CYAN);
        g.fillRect(23, 9, 2, 3);
        g.setColor(new Color(92, 106, 138));
        g.fillRect(22, 19, 4, 2);
    }

    private static void enemy(Graphics2D g, int offset) {
        g.setColor(new Color(5, 10, 18, 140));
        g.fillOval(3, 23, 22, 4);
        g.translate(0, offset);
        g.setColor(new Color(51, 94, 70));
        g.fillOval(5, 9, 19, 16);
        g.setColor(new Color(79, 144, 88));
        g.fillOval(5, 7, 19, 14);
        g.setColor(new Color(49, 104, 69));
        g.fillRect(2, 12, 5, 6);
        g.fillRect(22, 12, 5, 6);
        g.setColor(new Color(154, 194, 103));
        g.fillRect(7, 8, 15, 3);
        g.setColor(new Color(238, 116, 99));
        g.fillRect(9, 14, 4, 3);
        g.fillRect(17, 14, 4, 3);
        g.setColor(new Color(36, 34, 42));
        g.fillRect(10, 14, 2, 2);
        g.fillRect(18, 14, 2, 2);
        g.fillRect(12, 20, 6, 1);
        g.setColor(new Color(217, 213, 173));
        g.fillRect(13, 21, 2, 3);
        g.fillRect(17, 21, 2, 3);
    }

    private static void boss(Graphics2D g, int offset) {
        g.setColor(new Color(18, 9, 30, 170));
        g.fillOval(1, 23, 27, 5);
        g.translate(0, offset);
        g.setColor(new Color(75, 43, 108));
        g.fillRoundRect(4, 10, 21, 15, 5, 5);
        g.setColor(new Color(119, 71, 153));
        g.fillRoundRect(6, 8, 17, 15, 4, 4);
        g.setColor(new Color(191, 152, 211));
        g.fillPolygon(new int[]{7, 8, 12}, new int[]{12, 2, 11}, 3);
        g.fillPolygon(new int[]{20, 21, 16}, new int[]{12, 2, 11}, 3);
        g.setColor(new Color(36, 24, 56));
        g.fillRect(10, 14, 5, 4);
        g.fillRect(17, 14, 5, 4);
        g.setColor(new Color(242, 102, 183));
        g.fillRect(11, 15, 3, 2);
        g.fillRect(18, 15, 3, 2);
        g.setColor(new Color(220, 204, 227));
        g.fillRect(13, 20, 2, 4);
        g.fillRect(19, 20, 2, 4);
    }

    private static void coin(Graphics2D g, long time) {
        int blink = (int) (2 * Math.sin(time / 300.0));
        g.setColor(new Color(201, 133, 51, 70));
        g.fillOval(4, 4 + blink, 20, 20);
        g.setColor(new Color(151, 91, 30));
        g.fillOval(7, 7 + blink, 14, 15);
        g.setColor(GOLD);
        g.fillOval(8, 6 + blink, 12, 14);
        g.setColor(new Color(255, 227, 152));
        g.fillRect(13, 9 + blink, 2, 9);
        g.fillRect(11, 11 + blink, 6, 2);
    }

    private static void potion(Graphics2D g, long time) {
        int blink = (int) Math.sin(time / 320.0);
        g.setColor(new Color(36, 101, 113, 90));
        g.fillOval(4, 5, 20, 21);
        g.setColor(new Color(181, 203, 219));
        g.fillRect(11, 5 + blink, 7, 5);
        g.setColor(new Color(122, 88, 72));
        g.fillRect(10, 4 + blink, 9, 3);
        g.setColor(new Color(60, 152, 181));
        g.fillRoundRect(7, 10 + blink, 15, 15, 5, 5);
        g.setColor(CYAN);
        g.fillRect(10, 15 + blink, 10, 7);
        g.setColor(new Color(204, 252, 241));
        g.fillRect(10, 12 + blink, 2, 6);
    }

    private static void crystal(Graphics2D g, long time) {
        int blink = (int) Math.sin(time / 350.0);
        g.setColor(new Color(119, 79, 163, 80));
        g.fillOval(3, 4, 22, 22);
        Polygon diamond = new Polygon(new int[]{14, 23, 14, 5},
                new int[]{4 + blink, 14 + blink, 25 + blink, 14 + blink}, 4);
        g.setColor(new Color(98, 65, 163));
        g.fillPolygon(diamond);
        g.setColor(VIOLET);
        g.fillPolygon(new int[]{14, 22, 14}, new int[]{4 + blink, 14 + blink, 25 + blink}, 3);
        g.setColor(new Color(230, 200, 251));
        g.fillRect(13, 8 + blink, 2, 10);
    }
}
