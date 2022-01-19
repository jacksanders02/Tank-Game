import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JOptionPane;

import java.awt.Point;
import java.awt.Dimension;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

class Player {
    // Constants and instance variables
    public final double SPEED = 200 * ((double)GameSurface.FRAME_TIME / 1000); // Px/frame
    public final double TURN_SPEED = 144 * ((double)GameSurface.FRAME_TIME / 1000); // Deg/frame
    
    private BufferedImage tankBase;
    private BufferedImage tankTurret;
    
    private Point pos;
    private Point aim;
    private double[] realCoords;
    private double angle;
    private double turretAngle;
    
    // Used for bounding box calculations
    private double diagLength;
    private double diagAngle; // Angle from diagonal to long side of sprite
    private Dimension bBox;
    
    private boolean[] keysPressed;
    
    public Player(int x, int y) {
        // Load tank base and turret
        loadImages();
        
        // Pythagoras to calculate length of diagonal
        diagLength = Math.sqrt(Math.pow(tankBase.getHeight(), 2) + 
                                Math.pow(tankBase.getWidth(), 2));

        // tan = opp/adj so angle = atan(opp/adj)
        diagAngle = Math.atan((double) tankBase.getWidth()/tankBase.getHeight());
        
        /* Store coordinates in relevant instance variables. realCoords is used 
         * to store the true non-integer values of the coordinates, so that the
         * player can move at a shallow angle and not be forced into a straight line
         */
        pos = new Point(x, y);
        aim = pos;
        realCoords = new double[]{x, y};
        angle = 0;
        
        bBox = new Dimension(0, 0);
        calculateBoundingRect();
        
        /*
         * Stores data on which direction is being pressed, to remove the delay
         * between initial keypress and the next. [UP, DOWN, LEFT, RIGHT]
         */
        keysPressed = new boolean[4];
    }
    
    private void loadImages() {
        try {
            tankBase = ImageIO.read(new File("assets/images/tankBase.png"));
            tankTurret = ImageIO.read(new File("assets/images/tankTurret.png"));
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
    
    public void handleMouseClick(MouseEvent e) {
        int button = e.getButton();
        
        // LMB to shoot
        if (button == MouseEvent.BUTTON1) {
            fireShell();
        }
        
        // RMB to lay a mine
        if (button == MouseEvent.BUTTON3) {
            System.out.println("mine");
        }
    }
    
    private void fireShell() {
        // Calculate shell position to be at the end of the turret
        double turretLength = tankTurret.getHeight() / 2;
        int shellX = (int) (pos.x + Math.sin(turretAngle) * turretLength);
        int shellY = (int) (pos.y - Math.cos(turretAngle) * turretLength);
        
        // Adds a shell to GameSurface's arraylist
        Tanks.gameSurface.addShell(new Shell(SPEED * 2, shellX, shellY, turretAngle, 1));
    }
    
    private void calculateBoundingRect() {
        // Calculate bounding box
        double theta = Math.abs(angle);
        if (theta > 90) {
            // Keep theta between below 90 otherwise triangle angle calcs will break
            theta = 90 - (theta - 90);
        }
           
        // Trigonometry magic 
        double tempAngle = Math.toRadians(90 - theta) - diagAngle;
        int bBoxHalfWidth = (int) ((diagLength / 2) * Math.cos(tempAngle));
        int bBoxHalfHeight = (int) ((diagLength / 2) * Math.cos(Math.toRadians(theta) - diagAngle));
        
        bBox.width = bBoxHalfWidth;
        bBox.height = bBoxHalfHeight;
    }
    
    public void update() {
        // Rotate tank
        if (keysPressed[2]) angle -= TURN_SPEED;
        if (keysPressed[3]) angle += TURN_SPEED;
        
        if (angle < -179) {
            angle += 360;
        } else if (angle > 180) {
            angle -= 360;
        }
        
        //Calculate necessary x and y changes for given rotation
        double radians = Math.toRadians(angle);
        double xChange = SPEED * Math.sin(radians);
        double yChange = SPEED * Math.cos(radians);
        
        /* If necessary, update realCoords by the exact decimal value, and then 
         * cast them to ints to be used by pos.
         */
        if (keysPressed[0]) {
            realCoords[0] += xChange;
            realCoords[1] -= yChange;
            pos.x = (int) realCoords[0];
            pos.y = (int) realCoords[1];
        }
        if (keysPressed[1]) {
            realCoords[0] -= xChange;
            realCoords[1] += yChange;
            pos.x = (int) realCoords[0];
            pos.y = (int) realCoords[1];
        }
        
        // Update aim to match mouse coordinates.
        aim = MouseInfo.getPointerInfo().getLocation();
        aim.translate(-Tanks.gameFrame.getLocationOnScreen().x, -Tanks.gameFrame.getLocationOnScreen().y);
        
        // Update turret angle to point towards cursor
        turretAngle = Math.atan2((aim.x - pos.x), -(aim.y - pos.y));
        
        calculateBoundingRect();
        
        // Check in both x and y dimensions to see if the bounding box intersects with the edge of the frame
        if (realCoords[0] - bBox.width < 0) {
            realCoords[0] = 0 + bBox.width;
            pos.x = 0 + bBox.width;
        } else if (realCoords[0] + bBox.width > GameSurface.GAME_WIDTH) {
            realCoords[0] = GameSurface.GAME_WIDTH - bBox.width;
            pos.x = GameSurface.GAME_WIDTH - bBox.width;
        }
        
        if (realCoords[1] - bBox.height < 0) {
            realCoords[1] = 0 + bBox.height;
            pos.y = 0 + bBox.height;
        } else if (realCoords[1] + bBox.height > GameSurface.GAME_HEIGHT) {
            realCoords[1] = GameSurface.GAME_HEIGHT - bBox.height;
            pos.y = GameSurface.GAME_HEIGHT - bBox.height;
        }
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
        
        at.setToIdentity();
        at.translate(pos.x, pos.y); // Translate to desired position
        at.rotate(turretAngle); // Rotate (No need to convert to radians as turretAngle is already in radians)
        // Translate up and left by half of width and height, to centre the image
        at.translate(-tankTurret.getWidth()/2, -tankTurret.getHeight()/2);
        g2d.drawImage(tankTurret, at, null);
    }
}