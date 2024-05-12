import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.*;


public class StartScreen extends JFrame implements ActionListener {
    JButton button;

    Clip soundClip2 = loadSoundClip("./chicken_start.wav");
    Clip soundClip5 = loadSoundClip("./chicken_theme.wav");

    StartScreen() {

        setTitle("Flappy Chicken");
       ImageIcon image = new ImageIcon("C:\\Users\\lenovo\\Documents\\falooda\\src\\startscreen.png");
       JLabel label = new JLabel(image);
       setContentPane(label);

       ImageIcon imageIcon = new ImageIcon("C:\\Users\\lenovo\\Documents\\falooda\\src\\iconImage.png");
        setIconImage(imageIcon.getImage());

        // try {
        //     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("./chicken_start.wav"));
        //     soundClip2 = AudioSystem.getClip();
        //     soundClip2.open(audioInputStream);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }

        // try {
        //     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("./chicken_theme.wav"));
        //     soundClip5 = AudioSystem.getClip();
        //     soundClip5.open(audioInputStream);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }

       button = new JButton();
       ImageIcon icon = new ImageIcon("C:\\Users\\lenovo\\Documents\\falooda\\src\\play.png");
       button.setBounds(110,360,120,80);
       button.setFocusable(false);
       button.setIcon(icon);
       button.addActionListener(this);

        add(button);
        setSize(360, 640);
        setResizable(false);

        soundClip5.setFramePosition(0);
        soundClip5.start();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            soundClip2.setFramePosition(0);
            soundClip2.start();
            soundClip5.stop();
            new App();
        } else {
            JOptionPane.showMessageDialog(null, "Something went wrong!");
        }
    }
}