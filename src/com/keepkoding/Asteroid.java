package com.keepkoding;

import java.awt.image.BufferedImage;

public class Asteroid extends Ship{

    private static final BufferedImage sprite =
            ImageLoader.load("asteroidPic1.png");
    private static final int errorRange = 200;

    Asteroid(double x, double y) {
        super(
                1440,
                0,
                x + errorRange - (Math.random() * errorRange),
                y + 100 - (Math.random() * 200),
                10,
                SpaceShooter.screenWidth / 45 * 4,
                sprite
        );

    }
}
