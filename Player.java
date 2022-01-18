import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JOptionPane;

import java.awt.Point;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Player {
    private BufferedImage tankBase;
    private Point pos;
    
    public Player(int x, int y) {
        // Load tank base and turret
        loadImages();
        
        pos = new Point(x, y);
    }
    
    private void loadImages() {
        try {
            tankBase = ImageIO.read(new File("assets/images/tankBase.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading player sprites: " 
                                                  + e.getMessage());
        }
    }
    
    public void draw(Graphics g, ImageObserver observer) {
        g.drawImage(
            tankBase,
            pos.x,
            pos.y,
            observer
        );
    }
}