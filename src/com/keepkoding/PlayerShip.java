package com.keepkoding;

import java.awt.Image;

class PlayerShip extends Ship {
    private static final int spriteSize = SpaceShooter.screenWidth / 45 * 4;
    private static final Image sprite =
        ImageLoader.load("playerShip.png").getScaledInstance(spriteSize, spriteSize, Image.SCALE_SMOOTH);
    
    private static final double acc = 0.4;

    PlayerShip() {
        super(
            SpaceShooter.screenWidth * 0.5,
            SpaceShooter.screenHeight * 0.8,
            0,
            0,
            10,
            spriteSize,
            sprite
        );
    }

    private static boolean tmp;

    void update(boolean incXVel, boolean decXVel, boolean incYVel, boolean decYVel) {
        if(incXVel) {
            xVel += acc;
        }
        if(decXVel) {
            xVel -= acc;
        }
        if(incYVel) {
            yVel -= acc;
        }
        if(decYVel) {
            yVel += acc;
        }
        super.updateBase();
    }
}
