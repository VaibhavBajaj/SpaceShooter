package com.keepkoding;


import java.awt.Graphics2D;
import java.awt.Image;

class GameBoard{

    private static final Image bgImage =
            ImageLoader.load("backgroundImage.png").getScaledInstance(SpaceShooter.screenWidth,
                    SpaceShooter.screenHeight, Image.SCALE_DEFAULT);

    void paint(Graphics2D g) {
        g.drawImage(bgImage, null, null);
    }

}
