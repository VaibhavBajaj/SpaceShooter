package com.keepkoding;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

abstract class Ship {
    private AffineTransform transform;
    private BufferedImage sprite;
    
    private static double minVel = 0.001;
    
    private double maxVel, midptX, midptY;
    private int spriteWidth, spriteHeight;
    
    protected double x, y, xVel, yVel;
    
    Ship() { } // XXX Remove this when EnemyShip gets fixed to properly extend this class.
    
    Ship(
        double x, double y,
        double xVel, double yVel,
        double maxVel,
        BufferedImage sprite
    ) {
        this.sprite = sprite;
        
        this.spriteWidth = sprite.getWidth();
        this.spriteHeight = sprite.getHeight();
        this.midptX = this.spriteWidth * 0.5;
        this.midptY = this.spriteHeight * 0.5;
        
        this.x = x;         this.y = y;
        this.xVel = xVel;   this.yVel = yVel;
        this.maxVel = maxVel;
        
        this.transform = AffineTransform.getTranslateInstance(x - midptX, y - midptY);
    }
    
    protected final void update() {
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
    
    protected final int getWidth() {
        return spriteWidth;
    }
    
    protected final int getHeight() {
        return spriteHeight;
    }
    
    void paint(Graphics2D g) {
        g.drawRenderedImage(sprite, transform);
    }
}
