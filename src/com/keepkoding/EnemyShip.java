package com.keepkoding;

import java.awt.Image;

class EnemyShip extends Ship {

    private static final int
            spriteSize = SpaceShooter.screenWidth / 45 * 4;
    private static final Image sprite =
        ImageLoader.load("enemyShip.png").getScaledInstance(spriteSize, spriteSize, Image.SCALE_SMOOTH);

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
