package com.keepkoding;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

class GameBoard{

    private static final BufferedImage bgImage =
            ImageLoader.load("backgroundImage.png");
    private AffineTransform transform = AffineTransform.getScaleInstance(
            (double)SpaceShooter.screenWidth / bgImage.getWidth(),
            (double)SpaceShooter.screenHeight / bgImage.getHeight()
    );

    void paint(Graphics2D g) {
        g.drawRenderedImage(bgImage, transform);
    }

}
