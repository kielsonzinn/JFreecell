package com.sri.jfreecell;

import java.net.URL;
import javax.swing.ImageIcon;

public class CardImageLoader {
    private static final String IMAGE_PATH = "cardimages/";
    private static final ClassLoader CLSLDR = CardImageLoader.class.getClassLoader();

    public static ImageIcon loadCardImage(String cardFilename) {
        URL imageURL = CLSLDR.getResource(IMAGE_PATH + cardFilename);
        return new ImageIcon(imageURL);
    }
}
