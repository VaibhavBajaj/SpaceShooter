package com.keepkoding;

import java.awt.image.BufferedImage;

class EnemyShip extends Ship {

    private static final int
            spriteSize = SpaceShooter.screenWidth / 45 * 4;
    private static final BufferedImage sprite =
        ImageLoader.load("enemyShip.png");

    EnemyShip() {
        super(
                randCoord(0, 600),  // XXX
                randCoord(0, 600),
                0,
                0,
                randCoord(3, 7),    // XXX
                spriteSize,
                sprite
        );
    }

    void update(double x, double y) {
        xVel = x - this.x;
        yVel = y - this.y;
        super.updateBase();
    }

    private static int randCoord(int min, int max) {
        return (int)(Math.random() * max) + min;
    }
}
