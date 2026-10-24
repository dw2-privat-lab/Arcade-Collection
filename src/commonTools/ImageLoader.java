package commonTools;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {
    public static Image loadImage(Class inputClass, String path) {
        URL url = inputClass.getClassLoader().getResource(path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        } else {
            System.err.println("Resource not found: " + path);
            return null;
        }
    }
}
