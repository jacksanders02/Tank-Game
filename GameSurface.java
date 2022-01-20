import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Area;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.ArrayList;

/*
 * This class extends JPanel as it has similar function. Also implements the 
 * ActionListener and KeyListener interfaces as they are both required for the 
 * running of the game (Action for game loop, Key to handle keypresses).
 */
 
class GameSurface extends JPanel implements ActionListener, KeyListener, MouseListener{
    // Constants and instance variables
    public static final int GAME_WIDTH = 1600;
    public static final int GAME_HEIGHT = 900;
    
    public static final int FRAME_TIME = 25; // Time between frames (in ms)
    
    private BufferedImage bgImage;
    
    private Player player;
    
    private Timer gameTimer;
    
    private ArrayList<Shell> shellList;
    private ArrayList<Tank> tankList;
    
    public GameSurface() {
        // Load asset for background image
        loadImage();
        
        // Sets JPanel size and colour
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(new Color(255, 0, 0));
        
        player = new Player(GAME_WIDTH / 2, GAME_HEIGHT / 2);
        player.setArrayListIndex(0);
        
        shellList = new ArrayList<Shell>();
        tankList = new ArrayList<Tank>();
        tankList.add(player);
        
        // Set up and start game loop (timer calls actionPerformed every FRAME_TIME ms)
        gameTimer = new Timer(FRAME_TIME, this);
        gameTimer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i=0; i<tankList.size(); i++) {
            tankList.get(i).update();
        }
        
        for (int i=0; i<shellList.size(); i++) {
            shellList.get(i).update();
            
            // Break loop if last shell was destroyed in update
            if (i >= shellList.size()) break;
            
            Area currentShellHitbox = new Area(shellList.get(i).getHitbox());
            
            boolean destroyed = false;
            // Check if shell has hit another shell
            for (int j=0; j<shellList.size(); j++) {
                currentShellHitbox.intersect(new Area(shellList.get(j).getHitbox()));
                if (!currentShellHitbox.isEmpty() && i != j) {
                    shellList.get(i).destroy();
                    if (j > i) j-= 1; // Subtract one from j to adjust for removed shell
                    shellList.get(j).destroy();
                    destroyed = true;
                }
            }
            if (destroyed) continue;
            
            // Regen currentShellHitbox as intersect (previous loop) mutates it
            currentShellHitbox = new Area(shellList.get(i).getHitbox());
            
            // Check if shell has hit a tank
            for (int j=0; j<tankList.size(); j++) {
                currentShellHitbox.intersect(new Area(tankList.get(j).getHitbox()));
                if (!currentShellHitbox.isEmpty()) {
                    shellList.get(i).destroy();
                    tankList.get(j).kill();
                }
            }
        }
        
        repaint(); // Method of JPanel - calls paintComponent again
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call paintComponent method of JPanel

        drawBackground(g); // Draw game background
        
        for (int i=0; i<tankList.size(); i++) {
            tankList.get(i).draw(g, this);
        }
        
        for (int i=0; i<shellList.size(); i++) {
            shellList.get(i).draw(g, this);
        }
        
        // Smooths animations
        Toolkit.getDefaultToolkit().sync();
    }

    /*
     * All abstract methods of KeyListener and MouseListener are required to be 
     * overridden, whether used or not.
     */
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyPressed(KeyEvent e) {
        player.handleKeypress(e);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        player.handleKeyRelease(e);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // Using mouseReleased instead of mouseClicked as the latter doesn't work if the mouse is moving when clicked.
        player.handleMouseClick(e);
    }
    
    public void addShell(Shell s) {
        shellList.add(s);
        shellList.get(shellList.size() - 1).setArrayListIndex(shellList.size() - 1);
    }
    
    public void deleteShell(int index) {
        // Subtract 1 from the index of all subsequent shells
        if (index != shellList.size() - 1) {
            for (int i=index+1; i<shellList.size(); i++) {
                shellList.get(i).setArrayListIndex(i - 1);
            }
        }
        shellList.remove(index);
    }
    
    public void clearShells() {
        shellList.clear();
    }
    
    public void deleteTank(int index) {
        // Subtract 1 from the index of all subsequent shells
        if (index != tankList.size() - 1) {
            for (int i=index+1; i<tankList.size(); i++) {
                tankList.get(i).setArrayListIndex(i - 1);
            }
        }
        tankList.remove(index);
    }
    
    public void resetPlayer() {
        // Resets player tank after death
        player = new Player(GAME_WIDTH / 2, GAME_HEIGHT / 2);
        player.setArrayListIndex(0);
        
        tankList.add(0, player);
    }
    
    private void drawBackground(Graphics g) {
        // Draw tileable background image 32 times in an 8x4 rectangle 
        for (int x=0; x<8; x++) {
            for (int y=0; y<4; y++) {
                // Use 'this' (Extension of JPanel) as the ImageObserver
                g.drawImage(
                    bgImage,
                    GAME_WIDTH * x/8,
                    GAME_HEIGHT * y/4,
                    this
                );
            }
        }
    }
    
    private void loadImage() {
        try {
            bgImage = ImageIO.read(new File("assets/images/BG.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading background image: " 
                                                  + e.getMessage());
        }
    }

}