package com.keepkoding;

import java.awt.image.BufferedImage;

class PlayerShip extends Ship {
    private static final int spriteSize = SpaceShooter.screenWidth / 45 * 4;
    private static final BufferedImage sprite =
        ImageLoader.load("playerShip.png");
    
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

        // This will be here since we will be having enemyShips outside boundaries
        if (x < spriteSize * 0.5) {
            xVel = 0;
            x = spriteSize * 0.5;
        } else if (x > SpaceShooter.screenWidth - (spriteSize * 0.5)) {
            xVel = 0;
            x = SpaceShooter.screenWidth - (spriteSize * 0.5);
        }

        if (y < spriteSize * 0.5) {
            yVel = 0;
            y = spriteSize * 0.5;
        } else if (y > SpaceShooter.screenHeight - (spriteSize * 0.5)) {
            yVel = 0;
            y = SpaceShooter.screenHeight - (spriteSize * 0.5);
        }

        super.updateBase();
    }
}
