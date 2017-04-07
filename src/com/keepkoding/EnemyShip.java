package com.keepkoding;

import java.awt.image.BufferedImage;

class EnemyShip extends Ship {

    private static final int
            maxVel = 7;

    private static final Description enemyDescription =
            new Description("enemyShip.png", 0.08)
                    .setMaxVelocity(maxVel)
                    .setCollisionDetection(.5, .47, 0.58);

    private static final BufferedImage sprite =
        ImageLoader.load("enemyShip.png");

    EnemyShip() {
        super(enemyDescription);
    }

    void update() {
        this.setXVel(SpaceShooter.playerShip.getX() - this.getX());
        this.setYVel(SpaceShooter.playerShip.getY() - this.getY());
        super.updateBase();
    }

}
