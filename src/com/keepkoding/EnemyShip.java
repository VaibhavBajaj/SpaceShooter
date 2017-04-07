package com.keepkoding;

import java.awt.image.BufferedImage;

class EnemyShip extends Ship {

    private static final int
            slowVel = 3,
            fastVel = 7;

    private static final Description enemyDescription =
            new Description("enemyShip.png", 0.08)
                    .setMaxVelocity(fastVel)
                    .setCollisionDetection(.5, .47, 0.58);

    private static final BufferedImage sprite =
        ImageLoader.load("enemyShip.png");

    EnemyShip() {
        super(
            enemyDescription,
            randCoord(0, 600),
            randCoord(0, 600),
            randCoord(slowVel, fastVel),
            randCoord(slowVel, fastVel)
        );
    }

    void update() {
        this.setXVel(SpaceShooter.playerShip.getX() - this.getX());
        this.setYVel(SpaceShooter.playerShip.getY() - this.getY());
        super.updateBase();
    }

}
