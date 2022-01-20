import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.awt.geom.AffineTransform;

public class Shell extends Sprite {
    /* 
     * smokeFrames is static as it is the same for every shell. This saves 
     * having to reload all 4 smoke images every time a new shell is instantiated
     */
    public static BufferedImage[] smokeFrames = new BufferedImage[4];
    
    public final double BASE_SPEED = 400 * ((double)GameSurface.FRAME_TIME / 1000); // Px/frame
    
    private double speed;
    
    private double xChange;
    private double yChange;
    
    // Used to control how many times the shell can bounce off of walls before exploding
    private int safeBounces;
    private int currentBounce;
    
    private Tank firedBy;
    
    public Shell(int speedMult, double x, double y, double radians, int bounceNum,
                 Tank tank) {
        super(new String[]{"shell.png", "shellSmoke0.png"}, x, y, radians);
        
        speed = BASE_SPEED * speedMult;
        xChange = speed * Math.sin(angle);
        yChange = speed * Math.cos(angle);
        
        safeBounces = bounceNum;
        currentBounce = 0;
        
        firedBy = tank; // So that tanks can keep track of how many shells they've fired
        
        animationStep = 1;
        
        for (int i=0; i<animationFrames.length; i++) {
            animationFrames[i] = smokeFrames[i];
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
    
    public void destroy() {
        Tanks.gameSurface.deleteShell(arrayListIndex);
        firedBy.shellDestroyed();
    }
    
    public void update() {
        super.animate(4, 1);
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
    
    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
        // Create AffineTransform object that will rotate the image.
        AffineTransform at = new AffineTransform();
        at.translate(pos.x, pos.y); // Translate to desired position
        at.rotate(angle); // Rotate (No need to convert to radians as turretAngle is already in radians)
        // Translate up and left by half of width and height, to centre the image
        at.translate(-imageList.get(1).getWidth()/2, -imageList.get(1).getHeight()/2);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(imageList.get(1), at, null);
    }
    
    public static void loadSmokeFrames() {
        // Turret animation and tank animation have the same number of frames, so no need for second loop
        for (int i=0; i<smokeFrames.length; i++) {
            try {
                smokeFrames[i] = ImageIO.read(new File("assets/images/" +  
                                                            "shellSmoke" + i + 
                                                            ".png"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading shell smoke " +
                                                     "sprites: " + e.getMessage());
            }
        }
    }
}