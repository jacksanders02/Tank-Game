import javax.swing.*;

class Tanks {
    public static void createFrame(int x, int y) {
        JFrame gameFrame = new JFrame();
        gameFrame.setSize(x, y);
        gameFrame.setVisible(true);
    }
    
    public static void main(String[] args) {
        createFrame(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}