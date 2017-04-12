package com.keepkoding;

class PlayerShip extends GameObj {
    
    private static final Description playerDescription =
        new Description("playerShip.png", 0.08)
        .setMaxVelocity(20.0)
        .setCollisionDetection(.5, .58, 0.38);
    
    private static final double
            speedAcc = 1,
            angularAcc = 0.15,
            bounceDampener = -0.7;

    PlayerShip() {
        super(
            playerDescription,
            SpaceShooter.screenWidth * 0.5,
            SpaceShooter.screenHeight * 0.8,
            0,
            -0.0000000001
        );
    }

    void update(boolean incSpeed, boolean decSpeed, boolean incAngle, boolean decAngle) {
        if(incSpeed) {
            addSpeed(speedAcc);
        }
        if(decSpeed) {
            addSpeed(-1 * speedAcc);
        }
        if(incAngle) {
            addAngle(angularAcc);
        }
        if(decAngle) {
            addAngle(-1 * angularAcc);
        }

        // Check if the player ship is starting to get off the screen.
        // If it is, zero the x or y velocity depending on which edge it
        // is touching, and move it back on screen.
        double halfWidth = getWidth() * 0.5;
        double halfHeight = getHeight() * 0.5;
        double thisX = getX(), thisY = getY();
        
        if (thisX < halfWidth) {
            setXVel(bounceDampener * getXVel());
            setX(halfWidth);
        } else if (thisX > SpaceShooter.screenWidth - halfWidth) {
            setXVel(bounceDampener * getXVel());
            setX(SpaceShooter.screenWidth - halfWidth);
        }
        
        if (thisY < halfHeight) {
            setYVel(bounceDampener * getYVel());
            setY(halfHeight);
        } else if (thisY > SpaceShooter.screenHeight - halfHeight) {
            setYVel(bounceDampener * getYVel());
            setY(SpaceShooter.screenHeight - halfHeight);
        }
        
        super.updateBase();
    }
}
