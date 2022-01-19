import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import java.awt.Point;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;

import java.awt.geom.AffineTransform;

class Player extends Sprite {
    // Constants and instance variables
    public final double SPEED = 200 * ((double)GameSurface.FRAME_TIME / 1000); // Px/frame
    public final double TURN_SPEED = Math.toRadians(144) * ((double)GameSurface.FRAME_TIME / 1000); // rad/frame
    
    private Point aim;
    private double turretAngle;
    
    private boolean[] keysPressed;
    
    public Player(int x, int y) {
        super(new String[]{"tankBase.png", "tankTurret.png"}, x, y, 0);
        
        // Initialise aim as a default Point, which will be updated later
        aim = new Point(0,0);
        
        /*
         * Stores data on which direction is being pressed, to remove the delay
         * between initial keypress and the next. [UP, DOWN, LEFT, RIGHT]
         */
        keysPressed = new boolean[4];
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
        double turretLength = imageList.get(1).getHeight() / 2;
        int shellX = (int) (pos.x + Math.sin(turretAngle) * turretLength);
        int shellY = (int) (pos.y - Math.cos(turretAngle) * turretLength);
        
        // Adds a shell to GameSurface's arraylist
        Tanks.gameSurface.addShell(new Shell(SPEED * 2, shellX, shellY, turretAngle, 1));
    }
    
    public void update() {
        // Rotate tank
        if (keysPressed[2]) angle -= TURN_SPEED;
        if (keysPressed[3]) angle += TURN_SPEED;
        
        if (angle < -Math.PI) {
            angle += Math.PI * 2;
        } else if (angle > Math.PI) {
            angle -= Math.PI * 2;
        }
        
        //Calculate necessary x and y changes for given rotation
        double xChange = SPEED * Math.sin(angle);
        double yChange = SPEED * Math.cos(angle);
        
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
    
    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
        // Create AffineTransform object that will rotate the image.
        AffineTransform at = new AffineTransform();
        at.translate(pos.x, pos.y); // Translate to desired position
        at.rotate(turretAngle); // Rotate (No need to convert to radians as turretAngle is already in radians)
        // Translate up and left by half of width and height, to centre the image
        at.translate(-imageList.get(1).getWidth()/2, -imageList.get(1).getHeight()/2);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(imageList.get(1), at, null);
    }
}