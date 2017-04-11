package com.keepkoding;

class PlayerShip extends GameObj {
    
    private static final Description playerDescription =
        new Description("playerShip.png", 0.08)
        .setMaxVelocity(11.1)
        .setCollisionDetection(.5, .58, 0.38);
    
    private static final double acc = 0.4;

    PlayerShip() {
        super(
            playerDescription,
            SpaceShooter.screenWidth * 0.5,
            SpaceShooter.screenHeight * 0.8,
            0,
            0
        );
    }

    void update(boolean incXVel, boolean decXVel, boolean incYVel, boolean decYVel) {
        if(incXVel) {
            setXVel(getXVel() + acc);
        }
        if(decXVel) {
            setXVel(getXVel() - acc);
        }
        if(incYVel) {
            setYVel(getYVel() - acc);
        }
        if(decYVel) {
            setYVel(getYVel() + acc);
        }

        // Check if the player ship is starting to get off the screen.
        // If it is, zero the x or y velocity depending on which edge it
        // is touching, and move it back on screen.
        double halfWidth = getWidth() * 0.5;
        double halfHeight = getHeight() * 0.5;
        double thisX = getX(), thisY = getY();
        
        if (thisX < halfWidth) {
            setXVel(0.0);
            setX(halfWidth);
        } else if (thisX > SpaceShooter.screenWidth - halfWidth) {
            setXVel(0.0);
            setX(SpaceShooter.screenWidth - halfWidth);
        }
        
        if (thisY < halfHeight) {
            setYVel(0.0);
            setY(halfHeight);
        } else if (thisY > SpaceShooter.screenHeight - halfHeight) {
            setYVel(0.0);
            setY(SpaceShooter.screenHeight - halfHeight);
        }
        
        super.updateBase();
    }
}
