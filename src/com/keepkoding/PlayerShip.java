package com.keepkoding;


import java.awt.Graphics2D;
import java.awt.Color;

class PlayerShip extends Ship{

    private static final int
            playerSpriteWidth = 64,
            playerSpriteHeight = 64;
    private final double acc = 0.1;
    private double x, y, xVel, yVel;

    PlayerShip() {
        xVel = 0;
        yVel = 0;
        x = (SpaceShooter.screenWidth * 0.5) - (playerSpriteWidth * 0.5);
        y = SpaceShooter.screenHeight * 0.8;
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

        x += xVel;
        y += yVel;
    }

    @Override
    void paint(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillOval((int)x, (int)y, playerSpriteWidth, playerSpriteHeight);
    }
}
