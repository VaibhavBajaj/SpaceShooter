package com.keepkoding;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

class EnemyShip extends Ship {

    private static final int
            spriteSize = SpaceShooter.screenWidth / 45 * 4;
    private static final BufferedImage sprite =
        ImageLoader.load("enemyShip.png");
    private AffineTransform transform;

    EnemyShip() {
        super(
                0,
                0,
                0,
                0,
                7,
                spriteSize,
                sprite
        );
    }

    void update(double x, double y) {
        xVel = x - this.x;
        yVel = y - this.y;
        super.updateBase();
    }

    private int randCoord(int min, int max) {
        return (int)(Math.random() * max) + min;
    }
}
