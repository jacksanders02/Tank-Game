import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JOptionPane;

import java.awt.Point;

import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

class Player {
    final int SPEED = 5;
    final double TURN_SPEED = 72 * ((double)GameSurface.FRAME_TIME / 1000); // Deg/frame
    private BufferedImage tankBase;
    private Point pos;
    private double angle;
    private boolean[] keysPressed;
    
    public Player(int x, int y) {
        // Load tank base and turret
        loadImages();
        
        pos = new Point(x, y);
        angle = 0;
        
        /*
         * Stores data on which direction is being pressed, to remove the delay
         * between initial keypress and the next. [UP, DOWN, LEFT, RIGHT]
         */
        keysPressed = new boolean[4];
    }
    
    private void loadImages() {
        try {
            tankBase = ImageIO.read(new File("assets/images/tankBase.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading player sprites: " 
                                                  + e.getMessage());
        }
    }
    
    public void handleKeypress(KeyEvent e) {
        int key = e.getKeyCode(); // Convert KeyEvent to integer code of key pressed
        
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_KP_UP) {
            keysPressed[0] = true;
        }
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_KP_DOWN) {
            keysPressed[1] = true;
        }
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_KP_LEFT) {
            keysPressed[2] = true;
        }
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_KP_RIGHT) {
            keysPressed[3] = true;
        }
    }
    
    public void handleKeyRelease(KeyEvent e) {
        int key = e.getKeyCode(); // Convert KeyEvent to integer code of key pressed
        
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_KP_UP) {
            keysPressed[0] = false;
        }
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_KP_DOWN) {
            keysPressed[1] = false;
        }
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_KP_LEFT) {
            keysPressed[2] = false;
        }
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_KP_RIGHT) {
            keysPressed[3] = false;
        }
    }
    
    public void update() {
        // Rotate tank
        if (keysPressed[2]) angle -= TURN_SPEED;
        if (keysPressed[3]) angle += TURN_SPEED;
        
        //Calculate necessary x and y changes for given rotation
        double radians = Math.toRadians(angle);
        int xChange = (int) (SPEED * Math.sin(radians));
        int yChange = (int) (SPEED * Math.cos(radians));
        
        if (keysPressed[0]) pos.translate(xChange, -yChange);
        if (keysPressed[1]) pos.translate(-xChange, yChange);
    }
    
    public void draw(Graphics g, ImageObserver observer) {
        // Create AffineTransform object that will rotate the image.
        AffineTransform at = new AffineTransform();
        at.translate(pos.x, pos.y); // Translate to desired position
        at.rotate(Math.toRadians(angle)); // Rotate
        // Translate up and left by half of width and height, to centre the image
        at.translate(-tankBase.getWidth()/2, -tankBase.getHeight()/2);

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(tankBase, at, null);
    }
}