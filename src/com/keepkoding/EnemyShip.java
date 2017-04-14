package com.keepkoding;

class EnemyShip extends GameObj {

    private static final int
            maxVel = 8;

    private static final Description enemyDescription =
            new Description("enemyShip.png", 0.08)
                    .setMaxVelocity(maxVel)
                    .setCollisionDetection(.5, .47, 0.58);

    EnemyShip() {
        super(enemyDescription);
    }

    void update() {
        this.setXVel(SpaceShooter.playerShip.getX() - this.getX());
        this.setYVel(SpaceShooter.playerShip.getY() - this.getY());
        super.updateBase();
    }

}
