import javax.swing.*;

class Tanks {
    public static void createFrame(int x, int y) {
        JFrame gameFrame = new JFrame("Tanks!");
        gameFrame.setSize(x, y);
        gameFrame.setVisible(true);
    }
    
    public static void main(String[] args) {
       createFrame(1600, 900);
    }
}