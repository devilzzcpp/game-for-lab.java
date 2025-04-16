import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class LoadMap {

    public static int[][] loadMap(File file, List<Enemy> enemies) {
        int rows = 0;
        int cols = 0;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                rows++;
                Scanner lineScanner = new Scanner(line);
                int tempCols = 0;
                while (lineScanner.hasNextInt()) {
                    tempCols++;
                    lineScanner.nextInt();
                }
                if (rows == 1) cols = tempCols;
                lineScanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[][] arr = new int[rows][cols];
        try (Scanner sc = new Scanner(file)) {
            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++) {
                    arr[j][i] = sc.nextInt();

                    if (arr[j][i] == 3) {
                        enemies.add(new Enemy(i, j, arr, null));
                        arr[j][i] = 0; 
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return arr;
    }

    public static void drawMap(Graphics g, int[][] arr) {
        Image texture = new ImageIcon(lab7.wallTexture).getImage();
        Image coinGif = new ImageIcon(lab7.coin).getImage();
        for (int j = 0; j < arr.length; j++) {
            for (int i = 0; i < arr[j].length; i++) {
                if (arr[j][i] == 1) {
                    g.drawImage(texture, i * lab7.tileSize, j * lab7.tileSize, lab7.tileSize, lab7.tileSize, null);
                } else if (arr[j][i] == 2) {
                    g.drawImage(coinGif, i * lab7.tileSize, j * lab7.tileSize, lab7.tileSize, lab7.tileSize, null);
                }
            }
        }
    }
}
