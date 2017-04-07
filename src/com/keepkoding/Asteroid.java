package com.keepkoding;

public class Asteroid extends Ship{
    private static final Description asteroidDescription = 
        new Description("asteroidPic1.png", .12)
        .setMaxVelocity(4.5)
        .setCollisionDetection(.5, .51, .39);

    Asteroid() {
        super(asteroidDescription);
    }

    void update() {
        super.updateBase();
    }
}
