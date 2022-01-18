import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class GameSurface extends JPanel implements ActionListener {
    // Constants and instance variables
    public static final int GAME_WIDTH = 1600;
    public static final int GAME_HEIGHT = 900;
    
    private static final int FRAME_TIME = 25; // Time between frames (in ms)
    
    private BufferedImage bgImage;
    
    private Player player;
    
    private Timer gameTimer;
    
    public GameSurface() {
        // Load asset for background image
        loadImage();
        
        // Sets JPanel size and colour
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(new Color(255, 0, 0));
        
        player = new Player(GAME_WIDTH / 2, GAME_HEIGHT / 2);
        
        // Set up and start game loop (timer calls actionPerformed every FRAME_TIME ms)
        gameTimer = new Timer(FRAME_TIME, this);
        gameTimer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call paintComponent method of JPanel

        drawBackground(g); // Draw game background
        
        player.draw(g, this); // Draw player sprite
        
        // Smooths animations
        Toolkit.getDefaultToolkit().sync();
    }
    
    private void drawBackground(Graphics g) {
        // Draw tileable background image 32 times in an 8x4 rectangle 
        for (int x=0; x<8; x++) {
            for (int y=0; y<4; y++) {
                // Use 'this' (Extension of JPanel) as the ImageObserver
                g.drawImage(
                    bgImage,
                    GAME_WIDTH * x/8,
                    GAME_HEIGHT * y/4,
                    this
                );
            }
        }
    }
    
    private void loadImage() {
        try {
            bgImage = ImageIO.read(new File("assets/images/BG.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading background image: " 
                                                  + e.getMessage());
        }
    }

}