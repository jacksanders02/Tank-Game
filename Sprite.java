import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JOptionPane;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Color;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.Point;
import java.awt.Dimension;

import java.util.ArrayList;

public class Sprite {
    protected final int ANIMATION_FRAMES = 250 / GameSurface.FRAME_TIME;
    
    protected ArrayList<BufferedImage> imageList;
    
    protected Point pos;
    protected double[] realCoords;
    protected double angle;
    
    // Used for bounding box calculations
    protected double diagLength;
    protected double diagAngle; // Angle from diagonal to long side of sprite
    protected Dimension bBox;
    
    protected Rectangle baseRect;
    protected Shape hitbox;
    
    // Stores index of sprite in arraylist, for easy deletion
    protected int arrayListIndex;
    
    // Used for animation
    protected BufferedImage[] animationFrames; // Stores sprite images
    protected int currentFrame; // Stores current frame of animation
    protected int animationCounter; // Number of frames since last frame change
    protected int animationStep; // Number of frames to move forwards/back
    
    public Sprite(String[] imageArray, double x, double y, double radians) {
        imageList = new ArrayList<BufferedImage>();
        loadImages(imageArray);
        
        baseRect = new Rectangle(0, 0, imageList.get(0).getWidth(), imageList.get(0).getHeight());
        
        pos = new Point((int) x, (int) y);
        realCoords = new double[]{x, y};
        
        /* 
         * Perform speed and bounding box calculations in constructor, to avoid 
         * calculating the same thing every frame, wasting computing power.
         */
        angle = radians;
        
        
        // Pythagoras to calculate length of diagonal
        diagLength = Math.sqrt(Math.pow(imageList.get(0).getHeight(), 2) + 
                                Math.pow(imageList.get(0).getWidth(), 2));

        // tan = opp/adj so angle = atan(opp/adj)
        diagAngle = Math.atan((double) imageList.get(0).getWidth()/imageList.get(0).getHeight());
        
        bBox = new Dimension(0, 0);
        calculateBoundingRect();
        
        // Create default hitbox
        AffineTransform at = new AffineTransform();
        at.setToIdentity();
        hitbox = at.createTransformedShape(baseRect);
        
        animationFrames = new BufferedImage[4];
        animationFrames[0] = imageList.get(0);
        currentFrame = 0;
        animationCounter = 0;
        animationStep = 0;
    }
    
    protected void calculateBoundingRect() {
        // Calculate bounding box
        double theta = Math.abs(angle);
        if (theta > Math.PI / 2) {
            // Keep theta between below 90 otherwise triangle angle calcs will break
            theta = (Math.PI / 2) - (theta - (Math.PI / 2));
        }
           
        // Trigonometry magic 
        double tempAngle = Math.toRadians(90) - theta - diagAngle;
        int bBoxHalfWidth = (int) ((diagLength / 2) * Math.cos(tempAngle));
        int bBoxHalfHeight = (int) ((diagLength / 2) * Math.cos(theta - diagAngle));
        
        bBox.width = bBoxHalfWidth;
        bBox.height = bBoxHalfHeight;
    }
    
    private void loadImages(String[] images) {
        int currentImg = 0;
        try {
            for (String i : images) {
                imageList.add(ImageIO.read(new File("assets/images/" + i)));
                currentImg ++;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading sprite " +
                                                  images[currentImg] + ": " +
                                                  e.getMessage());
        }
    }
    
    public Shape getHitbox() {
        return hitbox;
    }
    
    public void setArrayListIndex(int i) {
        arrayListIndex = i;
    }
    
    public void animate() {
        if (animationStep != 0) {
            animationCounter++;
            if (animationCounter >= ANIMATION_FRAMES) {
                animationCounter = 0;
                currentFrame += animationStep;
                if (currentFrame > animationFrames.length - 1) {
                    currentFrame = 0;
                } else if (currentFrame < 0) {
                    currentFrame = animationFrames.length - 1;
                }
                imageList.set(0, animationFrames[currentFrame]);
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver observer) {
        // Create AffineTransform object that will rotate the image.
        AffineTransform at = new AffineTransform();
        
        at.translate(pos.x, pos.y); // Translate to desired position
        at.rotate(angle); // Rotate
        // Translate up and left by half of width and height, to centre the image
        at.translate(-imageList.get(0).getWidth()/2, -imageList.get(0).getHeight()/2);
        
        hitbox = at.createTransformedShape(baseRect);

        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawImage(imageList.get(0), at, null);
        
        if (Tanks.debug) {
            g2d.setColor(Color.RED);
            g2d.draw(hitbox);
        }
    }
}