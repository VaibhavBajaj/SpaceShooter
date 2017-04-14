package com.keepkoding;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

class GameBoard{

    private static final BufferedImage
            bgImage = ImageLoader.load("backgroundImage.png"),
            gameOverImage = ImageLoader.load("gameOver.png");
    private static final AffineTransform
            transformBackground = AffineTransform.getScaleInstance(
            (double)SpaceShooter.screenWidth / bgImage.getWidth(),
            (double)SpaceShooter.screenHeight / bgImage.getHeight()
            ),
            transformGameOver = AffineTransform.getTranslateInstance(
                    SpaceShooter.screenWidth / 3,
                    SpaceShooter.screenHeight / 3
            );
    static {
        transformGameOver.scale(
                (double)SpaceShooter.screenWidth / (3 * gameOverImage.getWidth()),
                (double)SpaceShooter.screenHeight / (3 * gameOverImage.getHeight())
        );
    }

    void paintField(Graphics2D g) {
        g.drawRenderedImage(bgImage, transformBackground);
    }

    void paintGameOver(Graphics2D g) {
        g.drawRenderedImage(gameOverImage, transformGameOver);
    }

}
