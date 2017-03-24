package com.keepkoding;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

abstract class Ship {
    private AffineTransform transform;
    private BufferedImage sprite;
    
    private static double minVel = 0.001;
    
    private double maxVel, midptX, midptY, spriteSize;
    
    double x, y, xVel, yVel;
    
    Ship(double x, double y, double xVel, double yVel, double maxVel, double spriteSize, BufferedImage sprite) {
        this.sprite = sprite;
        this.spriteSize = spriteSize;
        this.midptX = spriteSize * 0.5;
        this.midptY = spriteSize * 0.5;
        this.x = x;
        this.y = y;
        this.xVel = xVel;
        this.yVel = yVel;
        this.maxVel = maxVel;
        this.transform = AffineTransform.getTranslateInstance(midptX - x, midptY - y);
        transform.scale(
                spriteSize / sprite.getWidth(),
                spriteSize / sprite.getHeight()
        );
    }

    final void updateBase() {
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

            // Magic center + rotate + translate matrix.
            transform.setTransform(
                cos, sin,
                sin, -cos,
                x - midptX*cos - midptY*sin, y - midptX*sin + midptY*cos
            );
        }

        transform.scale(
                spriteSize / sprite.getWidth(),
                spriteSize / sprite.getHeight()
        );

        x += xVel;
        y += yVel;
    }
    
    void paint(Graphics2D g) {
        g.drawRenderedImage(sprite, transform);
    }
}
