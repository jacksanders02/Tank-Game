import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

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
    
    public GameSurface() {
        // Load asset for background image
        loadImage();
        
        // Sets JPanel size and colour
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(new Color(255, 0, 0));
        
        player = new Player(GAME_WIDTH / 2, GAME_HEIGHT / 2);
        shellList = new ArrayList<Shell>();
        
        // Set up and start game loop (timer calls actionPerformed every FRAME_TIME ms)
        gameTimer = new Timer(FRAME_TIME, this);
        gameTimer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        
        for (int i=0; i<shellList.size(); i++) {
            shellList.get(i).update();
        }
        
        repaint(); // Method of JPanel - calls paintComponent again
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call paintComponent method of JPanel

        drawBackground(g); // Draw game background
        
        player.draw(g, this); // Draw player sprite
        
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