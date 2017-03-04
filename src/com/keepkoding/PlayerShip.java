package com.keepkoding;


import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

class PlayerShip extends Ship{
    private static final BufferedImage sprite =
        ImageLoader.load("playerShip.png");
    
    private static final int
            playerSpriteWidth = sprite.getWidth(),
            playerSpriteHeight = sprite.getHeight();
            
    private static final double
            midptX = sprite.getWidth() / 2.0,
            midptY = sprite.getWidth() / 2.0,
            acc = 0.1,
            minVel = 0.001,
            maxVel = 32.0;
    
    private double x, y, xVel, yVel;
    private AffineTransform transform;

    PlayerShip() {
        xVel = 0;
        yVel = 0;
        x = (SpaceShooter.screenWidth * 0.5) - (playerSpriteWidth * 0.5);
        y = SpaceShooter.screenHeight * 0.8;
        
        // Get identity matrix, but with correct x and y translation.
        transform = new AffineTransform(1, 0, 0, 1, x - midptX, y - midptY);
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
        double velocity = Math.hypot(xVel, yVel);
        
        // Stop the ship if velocity is below minVel.
        // This is needed to prevent inaccurate transformation matrices,
        // and the player is unlikely to notice.
        if (velocity < minVel) {
            xVel = 0.0;
            yVel = 0.0;
            velocity = 0.0;
        }
        // Reduce the velocities if we are exceeding maxVel.
        if (velocity > maxVel) {
            double scaleFactor = maxVel / velocity;
            xVel *= scaleFactor;
            yVel *= scaleFactor;
            velocity = maxVel;
        }
        
        // If velocity != 0, we need to reset the transformation matrix.
        if (velocity != 0.0) {
            double cos = yVel / velocity;
            double sin = -xVel / velocity;
            // Magic center + rotate + translate matrix 0_O.
            transform.setTransform(
                cos, sin,
                sin, -cos,
                x - midptX*cos - midptY*sin, y - midptX*sin + midptY*cos
            );
        }
    }

    @Override
    void paint(Graphics2D g) {
        g.drawRenderedImage(sprite, transform); 
    }
}
