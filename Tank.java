import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import java.awt.Point;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;

import java.awt.geom.AffineTransform;

class Tank extends Sprite {
    // Constants and instance variables
    public final double BASE_SPEED = 200 * ((double)GameSurface.FRAME_TIME / 1000); // Px/frame
    public final double TURN_SPEED = Math.toRadians(144) * ((double)GameSurface.FRAME_TIME / 1000); // rad/frame
    
    protected Point aim;
    protected double turretAngle;
    protected double speed;
    
    protected double xChange;
    protected double yChange;
    
    public Tank(String type, int x, int y, double radians, int speedMult) {
        super(new String[]{type+"tankBase.png", type+"tankTurret.png"}, x, y, radians);
        
        // Initialise aim as a default Point, which will be updated later
        aim = new Point(0,0);
        
        speed = BASE_SPEED * speedMult;
    }
    
    protected void fireShell(int shellSpeedMult, int bounceNum) {
        // Calculate shell position to be at the end of the turret
        double turretLength = (imageList.get(1).getHeight() / 2) + 10;
        int shellX = (int) (pos.x + Math.sin(turretAngle) * turretLength);
        int shellY = (int) (pos.y - Math.cos(turretAngle) * turretLength);
        
        // Adds a shell to GameSurface's arraylist
        Tanks.gameSurface.addShell(new Shell(shellSpeedMult, shellX, shellY, 
                                             turretAngle, bounceNum));
    }
    
    public void update() {
        if (angle < -Math.PI) {
            angle += Math.PI * 2;
        } else if (angle > Math.PI) {
            angle -= Math.PI * 2;
        }
        
        //Calculate necessary x and y changes for given rotation
        xChange = speed * Math.sin(angle);
        yChange = speed * Math.cos(angle);
        
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