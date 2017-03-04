package com.keepkoding;

import java.io.IOException;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageLoader {

    public static BufferedImage load(String name) {
        URL url = ImageLoader.class.getResource("assets/" + name);
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException("Could not load image '"
                + name + "': '" + e.getMessage() + ".");
        }
    }
}


