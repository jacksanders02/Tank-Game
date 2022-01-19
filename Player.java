import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;

import javax.swing.JOptionPane;

class Player extends Tank {
    
    private boolean[] keysPressed;
    
    public Player(int x, int y) {
        super("player", x, y, 0, 1);
        
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
        
        if (keysPressed[1]) {
            animationStep = -1;
        } else {
            for (boolean k : keysPressed) {
                if (k) animationStep = 1;
            }
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
        
        // If any key is still pressed, set animationStep back to true
        animationStep = 0;
        if (keysPressed[1]) {
            animationStep = -1;
        } else {
            for (boolean k : keysPressed) {
                if (k) animationStep = 1;
            }
        }
    }
    
    public void handleMouseClick(MouseEvent e) {
        int button = e.getButton();
        
        // LMB to shoot
        if (button == MouseEvent.BUTTON1) {
            fireShell(1, 1);
        }
        
        // RMB to lay a mine
        if (button == MouseEvent.BUTTON3) {
            System.out.println("mine");
        }
    }
    
    @Override
    public void kill() {
        super.kill();
        Tanks.gameSurface.clearShells();
        
        int playAgain = JOptionPane.showOptionDialog(Tanks.gameFrame, 
                                                     "You died!", "lol", 
                                                     JOptionPane.OK_CANCEL_OPTION, 
                                                     JOptionPane.INFORMATION_MESSAGE, 
                                                     null, null, null);
        if (playAgain == JOptionPane.OK_OPTION) {
            Tanks.gameSurface.resetPlayer();
        } else {
            System.exit(0);
        }
    }
    
    @Override
    public void update() {
        super.update();
        
        // Rotate tank
        if (keysPressed[2]) angle -= TURN_SPEED;
        if (keysPressed[3]) angle += TURN_SPEED;
        
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
    }
}