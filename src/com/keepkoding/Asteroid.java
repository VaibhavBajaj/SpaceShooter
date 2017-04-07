package com.keepkoding;

import java.awt.image.BufferedImage;

public class Asteroid extends Ship{
    private static final Description asteroidDescription = 
        new Description("asteroidPic1.png", .12)
        .setMaxVelocity(4.5)
        .setCollisionDetection(.5, .5, .3);

    Asteroid(double x, double y) {
        super(
            asteroidDescription,
            x,
            y,
            0.0,
            0.0
        );
    }
}
