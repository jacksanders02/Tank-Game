import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Tanks {
    public static JFrame gameFrame;
    
    public static void createWindow() {
        gameFrame = new JFrame("Tanks!");
        
        GameSurface gameSurface = new GameSurface();
        
        gameFrame.add(gameSurface);
        gameFrame.addKeyListener(gameSurface);
        
        gameFrame.pack();
        gameFrame.setResizable(false);
        
        // Open window in the center of the screen
        gameFrame.setLocationRelativeTo(null);
        
        // Set crosshair as custom cursor
        BufferedImage crosshair = loadCrosshair();
        Cursor crosshairCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            crosshair, new Point(0, 0), "Crosshair Cursor");
        gameFrame.getContentPane().setCursor(crosshairCursor);
        
        gameFrame.setVisible(true);
    }
    
    private static BufferedImage loadCrosshair() {
        try {
            return ImageIO.read(new File("assets/images/crosshair.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading background image: " 
                                                  + e.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args) {
        // https://stackoverflow.com/a/22534931/4655368
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createWindow();
            }
        });
    }
}