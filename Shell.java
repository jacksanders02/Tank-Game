public class Shell extends Sprite {
    public final double BASE_SPEED = 400 * ((double)GameSurface.FRAME_TIME / 1000); // Px/frame
    
    private double speed;
    
    private double xChange;
    private double yChange;
    
    // Used to control how many times the shell can bounce off of walls before exploding
    private int safeBounces;
    private int currentBounce;
    
    // Stores index of shell in arraylist, for easy deletion
    private int arrayListIndex;
    
    public Shell(int speedMult, double x, double y, double radians, int bounceNum) {
        super(new String[]{"shell.png"}, x, y, radians);
        
        speed = BASE_SPEED * speedMult;
        xChange = speed * Math.sin(angle);
        yChange = speed * Math.cos(angle);
        
        safeBounces = bounceNum;
        currentBounce = 0;
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
}