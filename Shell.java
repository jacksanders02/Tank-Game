import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JOptionPane;

import java.awt.Point;
import java.awt.Dimension;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

public class Shell extends Sprite {
    private double speed;
    
    private double xChange;
    private double yChange;
    
    private double diagAngle;
    private double diagLength;
    
    // Used to control how many times the shell can bounce off of walls before exploding
    private int safeBounces;
    private int currentBounce;
    
    private BufferedImage shellImg;
    
    // Stores index of shell in arraylist, for easy deletion
    private int arrayListIndex;
    
    public Shell(double shellSpeed, double x, double y, double radians, int bounceNum) {
        super(x, y, radians);
        
        loadImages();
        // Pythagoras to calculate length of diagonal
        diagLength = Math.sqrt(Math.pow(shellImg.getHeight(), 2) + 
                                Math.pow(shellImg.getWidth(), 2));

        // tan = opp/adj so angle = atan(opp/adj)
        diagAngle = Math.atan((double) shellImg.getWidth()/shellImg.getHeight());
        
        calculateBoundingRect();
        
        speed = shellSpeed;
        xChange = speed * Math.sin(angle);
        yChange = speed * Math.cos(angle);
        
        safeBounces = bounceNum;
        currentBounce = 0;
    }
    
    private void loadImages() {
        try {
            shellImg = ImageIO.read(new File("assets/images/shell.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading shell sprite: " 
                                                  + e.getMessage());
        }
    }
    
    private void bounce(int surface) {
        if (currentBounce >= safeBounces) {
            destroy();
        }
        
        // 0 - left or right wall; 1 - top or bottom wall.
        switch (surface) {
            case 0 -> angle *= -1;
            case 1 -> angle = Math.PI - angle;
            default -> System.out.println("Something broke :/");
        }
        
        
        xChange = speed * Math.sin(angle);
        yChange = speed * Math.cos(angle);
        
        calculateBoundingRect();
        
        currentBounce ++;
    }
    
    private void destroy() {
        Tanks.gameSurface.deleteShell(arrayListIndex);
    }
    
    public void setArrayListIndex(int i) {
        arrayListIndex = i;
    }
    
    public void update() {
        realCoords[0] += xChange;
        realCoords[1] -= yChange;
        
        pos.x = (int) realCoords[0];
        pos.y = (int) realCoords[1];
        
        // Check in both x and y dimensions to see if the bounding box intersects with the edge of the frame
        if (realCoords[0] < bBox.width || 
                realCoords[0] + bBox.width > GameSurface.GAME_WIDTH) {
            // Hit the left or right walls
            bounce(0);
        }
        
        if (realCoords[1] < bBox.height || 
                realCoords[1] + bBox.height > GameSurface.GAME_HEIGHT) {
            // Hit the upper or lower walls
            bounce(1);
        }
    }
    
    public void draw(Graphics g, ImageObserver observer) {
        // Create AffineTransform object that will rotate the image.
        AffineTransform at = new AffineTransform();
        at.translate(pos.x, pos.y); // Translate to desired position
        at.rotate(angle); // Rotate
        // Translate up and left by half of width and height, to centre the image
        at.translate(-shellImg.getWidth()/2, -shellImg.getHeight()/2);

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(shellImg, at, null);
    }
}