import javax.swing.*;

public class App extends JFrame {
    int boardWidth = 360;
    int boardHeight = 640;

    public App() {
        setTitle("Flappy Chicken");
        setSize(boardWidth, boardHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        add(flappyBird);
        setVisible(true);
        setResizable(false);

        ImageIcon image = new ImageIcon("C:\\Users\\lenovo\\Documents\\falooda\\src\\iconImage.png");
        setIconImage(image.getImage());
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}