package com.keepkoding;

import java.awt.image.BufferedImage;

public class Asteroid extends Ship{

    private static final BufferedImage sprite =
            ImageLoader.load("asteroidPic1.png");
    private static final int
            slowVel = 3,
            fastVel = 7;

    Asteroid(double x, double y) {
        super(
                0,
                0,
                randCoord(slowVel, fastVel),
                randCoord(slowVel, fastVel),
                randCoord(slowVel,fastVel),
                SpaceShooter.screenWidth / 45 * 4,
                sprite
        );

    }
}
