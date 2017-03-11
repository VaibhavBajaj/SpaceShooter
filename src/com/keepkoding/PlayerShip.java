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
        if (x < 0) {
            xVel = 0;
            x = 0;
        } else if (x > SpaceShooter.screenWidth) {
            xVel = 0;
            x = 1440;
        }

        if (y < 0) {
            yVel = 0;
            y = 0;
        } else if (y > SpaceShooter.screenHeight) {
            yVel = 0;
            y = 800;
        }

        super.updateBase();
    }
}
