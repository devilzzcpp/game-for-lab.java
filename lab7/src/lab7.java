import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class lab7 {
    public static final int tileSize = 50;
    public static final int WINDOW_WIDTH = 1510;
    public static final int WINDOW_HEIGHT = 820;

    public static final String wallTexture = "wall2.jpg";
    public static final String heror = "heror.png";
    public static final String coin = "devilzzcoin.gif";

    public static final String enemyImgLeft = "enemyl.png";
    public static final String enemyImgRight = "enemyr.png";
    public static final String exitImage = "luke.png";
    public static final int enemySpeed = 250;
    public static final int refreshDelay = 30;

    public static void main(String[] args) {
        List<String> levelFiles = List.of("map1.txt", "map2.txt", "map3.txt");
        ArrayList<Enemy> enemies = new ArrayList<>();

        JFrame frame = new JFrame("game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        GamePanel panel = new GamePanel(levelFiles, enemies);
        panel.setBackground(Color.BLACK);
        panel.setOpaque(true);

        frame.add(panel);
        frame.setVisible(true);
    }
}
