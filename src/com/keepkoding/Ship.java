package com.keepkoding;

import java.awt.geom.AffineTransform;
import java.awt.Image;
import java.awt.Graphics2D;

abstract class Ship {
    private AffineTransform transform;
    private Image sprite;
    
    private static double minVel = 0.001;
    
    private double maxVel, midptX, midptY;
    
    double x, y, xVel, yVel;
    
    Ship(double x, double y, double xVel, double yVel, double maxVel, int spriteSize, Image sprite) {
        this.sprite = sprite;
        this.midptX = spriteSize * 0.5;
        this.midptY = spriteSize * 0.5;
        this.x = x;
        this.y = y;
        this.xVel = xVel;
        this.yVel = yVel;
        this.maxVel = maxVel;
        
        this.transform = AffineTransform.getTranslateInstance(x - midptX, y - midptY);
    }
    
    final void updateBase() {
        double velocity = Math.hypot(xVel, yVel);
        /*
        // Stop the ship if velocity is below minVel.
        // This is needed to prevent inaccurate transformation matrices,
        // and the player is unlikely to notice.
        if (velocity < minVel) {
            xVel = 0.0;
            yVel = 0.0;
            velocity = 0.0;
        }*/
        // Reduce the velocities if we are exceeding maxVel.
        if (velocity > maxVel) {
            double scaleFactor = maxVel / velocity;
            xVel *= scaleFactor;
            yVel *= scaleFactor;
            velocity = maxVel;
        }

        System.out.println(Math.hypot(xVel, yVel));

        // If velocity != 0, we need to reset the transformation matrix.
        if (velocity != 0.0) {
            double cos = yVel / velocity;
            double sin = -xVel / velocity;

            System.out.format("cos = %f, sin = %f%n", cos, sin);
            // Magic center + rotate + translate matrix.
            transform.setTransform(
                cos, sin,
                sin, -cos,
                x - midptX*cos - midptY*sin, y - midptX*sin + midptY*cos
            );
        }

        x += xVel;
        if (x < 0) {
            xVel = 0;
            x = 0;
        } else if (x > 1440) {
            xVel = 0;
            x = 1440;
        }
        y += yVel;
        if (y < 0) {
            yVel = 0;
            y = 0;
        } else if (y > 800) {
            yVel = 0;
            y = 800;
        }
    }
    
    void paint(Graphics2D g) {
        g.drawImage(sprite, transform, null);
    }
}
