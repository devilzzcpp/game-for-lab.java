import java.awt.*;
import javax.swing.ImageIcon;

public class Enemy extends Thread {
    private int x, y;
    private int[][] map;
    private GamePanel gamePanel;
    private boolean running = true;
    private int patrolDirection = 1;

    private Image enemyRightImage = new ImageIcon(lab7.enemyImgRight).getImage();
    private Image enemyLeftImage = new ImageIcon(lab7.enemyImgLeft).getImage();

    public Enemy(int x, int y, int[][] map, GamePanel gamePanel) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.gamePanel = gamePanel;
    }

    public void stopEnemy() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            move();
            try {
                Thread.sleep(lab7.enemySpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void move() {
        int playerX = gamePanel.getPlayerX();
        int playerY = gamePanel.getPlayerY();

        int newX = x;
        int newY = y;

        if (Math.abs(this.x - playerX) < 5 && Math.abs(this.y - playerY) < 5) {
            if (this.x < playerX) newX++;
            else if (this.x > playerX) newX--;
            if (this.y < playerY) newY++;
            else if (this.y > playerY) newY--;
        } else {
            if (patrolDirection == 1) {
                newX++;
                if (newX >= map[0].length - 1 || map[y][newX] == 1) patrolDirection = -1;
            } else {
                newX--;
                if (newX <= 0 || map[y][newX] == 1) patrolDirection = 1;
            }
        }

        if (map[newY][newX] == 0) {
            x = newX;
            y = newY;
        }

        gamePanel.handlePlayerCaught(x, y);
        gamePanel.repaint();
    }

    public void draw(Graphics g) {
        Image image = (patrolDirection == 1) ? enemyRightImage : enemyLeftImage;
        g.drawImage(image, x * lab7.tileSize, y * lab7.tileSize, lab7.tileSize, lab7.tileSize, null);
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }
}
