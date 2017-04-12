package com.keepkoding;

public class Asteroid extends GameObj {
    private static final Description asteroidDescription = 
        new Description("asteroidPic1.png", .12)
        .setCollisionDetection(.5, .51, .39);
    
    private static double asteroidPixelRadius = Math.hypot(
        asteroidDescription.getSpriteWidth() / 2.0,
        asteroidDescription.getSpriteHeight() / 2.0
    );
    
    private static final double minSpeed = 3.0;
    private static final double maxSpeed = 8.0;
    
    Asteroid() {
        super(asteroidDescription);
        this.setSpeed(minSpeed + Math.random() * (maxSpeed - minSpeed));
    }

    void update() {
        super.updateBase();
    }
    
    /** Return true if the asteroid is off the screen, and its  velocity  is
     *  such  that  it  would never return to the screen again. Assumes that
     *  the asteroid does not have zero speed.
     */
    boolean exitingScreen() {
        double x = getX();
        double y = getY();
        
        // Positive if the asteroid is to the right of the screen.
        double xPositive = x - SpaceShooter.screenWidth - asteroidPixelRadius;
        // Negative if the asteroid is to the left of the screen.
        double xNegative = asteroidPixelRadius + x;
        // Positive if the asteroid is below the bottom of the screen.
        double yPositive = y - SpaceShooter.screenHeight - asteroidPixelRadius;
        // Negative if the asteroid is above the top of the screen.
        double yNegative = asteroidPixelRadius + y;
        
        double xVel = getXVel();
        double yVel = getYVel();
        
        boolean result = 
          (((xVel <= 0) & (xNegative < 0)) | ((xVel >= 0) & (xPositive > 0)))
        | (((yVel <= 0) & (yNegative < 0)) | ((yVel >= 0) & (yPositive > 0)));
        
        return result;
    }
}
