package commonTools;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {

    public static void playSound(String filePath) {
        try {
            Clip clip = loadClip(filePath);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Clip playLoopingSound(String filePath) {
        try {
            Clip clip = loadClip(filePath);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Clip loadClip(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String resourcePath = filePath.replace('\\', '/');
        while (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        URL resource = SoundPlayer.class.getClassLoader().getResource(resourcePath);
        if (resource == null && resourcePath.startsWith("resources/")) {
            resource = SoundPlayer.class.getClassLoader().getResource(resourcePath.substring("resources/".length()));
        }

        AudioInputStream audioStream = resource != null ? AudioSystem.getAudioInputStream(resource) : AudioSystem.getAudioInputStream(new File(filePath));
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        audioStream.close();
        return clip;
    }

    public static void stopSound(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}

