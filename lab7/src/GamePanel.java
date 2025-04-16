import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

public class GamePanel extends JPanel {
    private int[][] map;
    private int playerX = 1, playerY = 1;
    private List<Enemy> enemies;
    private int totalTreasures = 0;
    private int treasuresCollected = 0;
    private boolean exitAvailable = false;
    private int exitX = -1, exitY = -1;
    private List<String> levelFiles;
    private int currentLevelIndex = 0;

    public GamePanel(List<String> levelFiles, List<Enemy> enemies) {
        this.levelFiles = levelFiles;
        this.enemies = enemies;
        setBackground(Color.BLACK);
        setFocusable(true);

        loadLevel(levelFiles.get(currentLevelIndex));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int newX = playerX, newY = playerY;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> newY--;
                    case KeyEvent.VK_S -> newY++;
                    case KeyEvent.VK_A -> newX--;
                    case KeyEvent.VK_D -> newX++;
                }

                if (isWalkable(newX, newY)) {
                    playerX = newX;
                    playerY = newY;

                    if (map[playerY][playerX] == 2) {
                        synchronized (this) {
                            map[playerY][playerX] = 0;
                            treasuresCollected++;

                            if (treasuresCollected == totalTreasures && !exitAvailable) {
                                exitAvailable = true;
                                map[exitY][exitX] = 4;
                            }
                        }
                    }

                    if (exitAvailable && playerX == exitX && playerY == exitY) {
                        loadNextLevel();
                    }

                    repaint();
                }
            }
        });

        new Thread(() -> {
            while (true) {
                repaint();
                try {
                    Thread.sleep(lab7.refreshDelay);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void loadLevel(String mapFile) {
        playerX = 1;
        playerY = 1;
        stopAllEnemies(); // Останавливаем врагов перед загрузкой нового уровня
        enemies.clear();
        treasuresCollected = 0;

        map = LoadMap.loadMap(new File(mapFile), enemies);

        scanMap();

        for (Enemy enemy : enemies) {
            enemy.setGamePanel(this);
            enemy.setMap(map);
            enemy.start();
        }

        repaint();
    }

    private void loadNextLevel() {
        if (currentLevelIndex < levelFiles.size() - 1) {
            currentLevelIndex++;
            loadLevel(levelFiles.get(currentLevelIndex));
        } else {
            JOptionPane.showMessageDialog(this, "Поздравляем! Вы прошли все уровни!", "Игра завершена", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void scanMap() {
        totalTreasures = 0;
        exitAvailable = false;
        exitX = -1;
        exitY = -1;

        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[j].length; i++) {
                if (map[j][i] == 2) {
                    totalTreasures++;
                }
                if (map[j][i] == 4) {
                    exitX = i;
                    exitY = j;
                }
            }
        }
    }

    private synchronized boolean isWalkable(int x, int y) {
        return x >= 0 && x < map[0].length && y >= 0 && y < map.length
                && (map[y][x] == 0 || map[y][x] == 2 || (exitAvailable && map[y][x] == 4));
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        LoadMap.drawMap(g, map);
        Image exitImg = new ImageIcon(lab7.exitImage).getImage();

        g.drawImage(new ImageIcon(lab7.heror).getImage(), playerX * lab7.tileSize, playerY * lab7.tileSize,
                lab7.tileSize, lab7.tileSize, null);

        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        if (exitAvailable && map[exitY][exitX] == 4) {
            g.drawImage(exitImg, exitX * lab7.tileSize, exitY * lab7.tileSize,
                    lab7.tileSize, lab7.tileSize, null);
        }
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void restartGame() {
        SwingUtilities.invokeLater(() -> {
            stopAllEnemies(); // Останавливаем всех врагов
            currentLevelIndex = 0; // Сбрасываем на первый уровень
            loadLevel(levelFiles.get(currentLevelIndex)); // Загружаем первый уровень
        });
    }

    private void stopAllEnemies() {
        for (Enemy enemy : enemies) {
            enemy.stopEnemy(); // Останавливаем поток врага
        }
    }

    public void handlePlayerCaught(int x, int y) {
        synchronized (this) {
            if (x == playerX && y == playerY) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Вас поймала полиция! Игра начинается заново.", "Игра окончена", JOptionPane.WARNING_MESSAGE);
                    restartGame();
                });
            }
        }
    }
}
