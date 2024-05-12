import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    
    // Sounds effects
    Clip soundClip1 = loadSoundClip("./chicken_moan.wav");
    Clip soundClip3 = loadSoundClip("./chicken_jump.wav");
    Clip soundClip4 = loadSoundClip("./chicken_pass.wav");



    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird class
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 54;
    int birdHeight = 44;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; //move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    double highScore = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images
        birdImg = new ImageIcon(getClass().getResource("./chickenn12.png")).getImage();
        backgroundImg = new ImageIcon(getClass().getResource("./background.jpg")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toptree.jpg")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./tree.jpg")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to be executed
                placePipes();
            }
        });
        placePipeTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();


        //Load Sound Effects
        // try {
        //     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("./chicken_moan.wav"));
        //     soundClip1 = AudioSystem.getClip();
        //     soundClip1.open(audioInputStream);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }

        // try {
        //     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("./chicken_jump.wav"));
        //     soundClip3 = AudioSystem.getClip();
        //     soundClip3.open(audioInputStream);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }

        // try {
        //     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("./chicken_pass.wav"));
        //     soundClip4 = AudioSystem.getClip();
        //     soundClip4.open(audioInputStream);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }


    }

    void placePipes() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Pixelify Sans", Font.PLAIN, 32));
        

        if (gameOver) {
            g.drawString("Game Over!", 80, 300);
            g.drawString("Your score: " + String.valueOf((int) score), 65, 330);
            g.drawString("HI: " + String.valueOf((int) highScore), 55, 35);
            g.drawString(String.valueOf((int) score), 10, 35);

        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
            g.drawString("HI: " + String.valueOf((int) highScore), 55, 35);
        }
        

    }

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //apply gravity to current bird.y, limit the bird.y to top of the canvas

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;

                soundClip4.setFramePosition(0);
                soundClip4.start();
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
            if (score > highScore) {
                highScore = score;
            }
            // System.out.println("High score: " + String.valueOf((int) highScore));
            soundClip1.setFramePosition(0);
            soundClip1.start();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // System.out.println("JUMP!");
            velocityY = -9;

            soundClip3.setFramePosition(0);
            soundClip3.start();


            if (gameOver) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    private Clip loadSoundClip(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            Clip clip = AudioSystem.getClip();
            if (clip != null) {
                clip.open(audioInputStream);
                return clip;
            } else {
                System.err.println("Failed to load sound clip: Clip is null.");
                return null;
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
            System.err.println("Error occurred while loading sound clip from " + filePath + ": " + ex.getMessage());
            return null;
        }
    }


    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}