package com.keepkoding;

import java.awt.image.BufferedImage;

class EnemyShip extends Ship {

    private static final int
            spriteSize = SpaceShooter.screenWidth / 45 * 4,
            slowVel = 3,
            fastVel = 7;
    private static final BufferedImage sprite =
        ImageLoader.load("enemyShip.png");

    EnemyShip() {
        super(
                randCoord(0, 600),  // XXX
                randCoord(0, 600),
                0,
                0,
                randCoord(slowVel, fastVel),    // XXX
                spriteSize,
                sprite
        );
    }

    void update(double x, double y) {
        xVel = x - this.x;
        yVel = y - this.y;
        super.updateBase();
    }


}
