import java.awt.Point;
import java.awt.Dimension;

public class Sprite {
    protected Point pos;
    protected double[] realCoords;
    protected double angle;
    
    // Used for bounding box calculations
    protected double diagLength;
    protected double diagAngle; // Angle from diagonal to long side of sprite
    protected Dimension bBox;
    
    public Sprite(double x, double y, double radians) {
        pos = new Point((int) x, (int) y);
        realCoords = new double[]{x, y};
        
        /* 
         * Perform speed and bounding box calculations in constructor, to avoid 
         * calculating the same thing every frame, wasting computing power.
         */
        angle = radians;
        
        bBox = new Dimension(0, 0);
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
}