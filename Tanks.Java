import java.awt.*;
import javax.swing.*;

class Tanks {
    public static JFrame gameFrame;
    
    public static void createWindow() {
        gameFrame = new JFrame("Tanks!");
        
        GameSurface gameSurface = new GameSurface();
        
        gameFrame.add(gameSurface);
        gameFrame.addKeyListener(gameSurface);
        
        gameFrame.pack();
        gameFrame.setResizable(false);
        
        // Open window in the center of the screen
        gameFrame.setLocationRelativeTo(null);
        
        gameFrame.setVisible(true);
    }
    
    public static void main(String[] args) {
        // https://stackoverflow.com/a/22534931/4655368
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createWindow();
            }
        });
    }
}