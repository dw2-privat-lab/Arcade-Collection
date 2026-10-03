package commonTools;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {

    public static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();

            clip.open(audioStream);
            clip.start();


        } catch (UnsupportedAudioFileException e) {
            System.err.println("The specified audio file format is not supported.");
        } catch (IOException e) {
            System.err.println("Error reading the audio file.");
        } catch (LineUnavailableException e) {
            System.err.println("Audio line for playback is unavailable.");
        }
    }
}
