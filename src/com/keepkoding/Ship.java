package com.keepkoding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/** Abstract base  class  for  Ships  in  the  game.  The  class  is  mainly
 *  responsible for four things:
 *  
 *   > Storing the sprite, location, and velocity of the represented ship.
 *   > Painting a rotated sprite on a Graphics2D object to represent the ship.
 *   > Updating the position of a ship each frame given its position & velocity.
 *   > Enforcing the speed limit (maxVel) for the ship and slowing it if needed.
 *  
 *  To use this class, extend your own Ship class using this Ship class as a
 *  base,  and  construct  the  base  with the initial x and y positions and
 *  velocities, the speed limit for your Ship (maxVel), and a  BufferedImage
 *  that  will  be  used  as  the sprite for your ship. To change the ship's
 *  position or velocity, just directly modify  the  x,  y,  xVel,  or  yVel
 *  variables  in  this class, and call updateBase() each tick to update the
 *  ship's  position  and  velocity  based  on  its  velocity   and   maxVel
 *  respectively  (this  usually  would  be  done  in your own Ship's update
 *  method). To paint the ship, call  paint(Graphics2D)  on  the  Graphics2D
 *  object  you  want  the  ship  painted on; you may either leave the paint
 *  method as-is or override it  and  call  super.paint  in  your  overrided
 *  method.
 */
abstract class Ship {
    // The transformation used to draw the sprite.
    private AffineTransform transform;
    
    // Temporary point object that can be used as a destination point
    // for transformation methods.
    private Point2D.Double tmpPoint = new Point2D.Double();
    
    // The ship will be stopped if it travels below this velocity.
    // This is used in particular to avoid doing math on denormalized numbers.
    private static double minVel = 0.001;
    
    static boolean drawCollisionDebug = true;
    
    private Description d;
    
    // This should be private in the future.
    private double x, y, xVel, yVel;
    
    /** Initialize  the  Ship  with  its  basic  information:  its   initial
     *  position,  velocity,  and  speed  limit, and the BufferedImage to be
     *  drawn to represent the ship on screen.
     *  DEPRECATED: Use the new Description class based constructor.
     */
    @Deprecated
    Ship(
        double x, double y,
        double xVel, double yVel,
        double maxVel, double spriteSize, BufferedImage sprite
    ) {
        this(
            new Description(sprite, spriteSize / SpaceShooter.screenWidth)
            .setMaxVelocity(maxVel).setCollisionDetection(0.5),
            x, y, xVel, yVel
        );
    }
    
    /** Initialized the Ship with its basic  information  specified  in  the
     *  Description object, and set the initial coordinate and velocity.
     */
    Ship(Description d, double x, double y, double xVel, double yVel) {
        this.d = d;
        this.x = x;
        this.y = y;
        this.xVel = xVel;
        this.yVel = yVel;
        
        this.transform = AffineTransform.getTranslateInstance(
            x - d.anchorX_, y - d.anchorY_
        );
    }
    
    /** Enforce the minimum and maximum velocity of the ship,  and,  if  the
     *  velocity is non-zero, update the position of the ship and update the
     *  private transformation matrix in preparation of repainting.
     */
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
        double maxVel = d.maxVelocity_;
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
            double ax = d.anchorX_, ay = d.anchorY_;
            transform.setTransform(
                cos, sin,
                sin, -cos,
                x - ax*cos - ay*sin, y - ax*sin + ay*cos
            );
        }

        x += xVel;
        y += yVel;
    }
    
    /** Return the width of the sprite used to render this object.
     */
    protected final int getWidth() {
        return d.scaledSprite_.getWidth();
    }
    
    /** Return the height of the sprite used to render this object.
     */
    protected final int getHeight() {
        return d.scaledSprite_.getHeight();
    }
    
    /** Transform and draw the sprite on the specified Graphics2D object.
     */
    void paint(Graphics2D g) {
        g.drawRenderedImage(d.scaledSprite_, transform);
        
        // Debug code: Draw lines to help visualize collision detection.
        if (drawCollisionDebug && d.collisionRadius_ > 0) {
            g.setColor(
                SpaceShooter.playerShip.checkCollision(this) ?
                Color.MAGENTA : Color.GREEN
            );
            for (double angle = 0.0; angle < Math.PI; angle += Math.PI / 8) {
                double x0, y0, x1, y1;
                Point2D.Double center = d.collisionCenter_;
                
                double rCos = Math.cos(angle) * d.collisionRadius_;
                double rSin = Math.sin(angle) * d.collisionRadius_;
                
                tmpPoint.x = center.x + rCos;
                tmpPoint.y = center.y + rSin;
                
                transform.transform(tmpPoint, tmpPoint);
                x0 = tmpPoint.x;
                y0 = tmpPoint.y;
                
                tmpPoint.x = center.x - rCos;
                tmpPoint.y = center.y - rSin;
                
                transform.transform(tmpPoint, tmpPoint);
                x1 = tmpPoint.x;
                y1 = tmpPoint.y;
                
                g.drawLine((int)x0, (int)y0, (int)x1, (int)y1);
            }
        }
    }
    
    /** Return whether this Ship and that Ship  have  overlapping  collision
     *  circles (as defined by their respective Description objects).
     */
    final boolean checkCollision(Ship that) {
        // First, transform the two ships' collision circle centers from
        // their specified coordinates to world coordinates.
        this.transform.transform(this.d.collisionCenter_, this.tmpPoint);
        that.transform.transform(that.d.collisionCenter_, that.tmpPoint);
        
        // Distance between the centers of the two collision circles.
        // If the distance is smaller than the combined radius of the
        // two circles, then the two circles have collided.
        double circleDistance = this.tmpPoint.distance(that.tmpPoint);
        double totalRadius = this.d.collisionRadius_ + that.d.collisionRadius_;
        return circleDistance < totalRadius;
    }
    
    protected final double getX() { return x; }
    protected final double getY() { return y; }
    protected final double getXVel() { return xVel; }
    protected final double getYVel() { return yVel; }
    protected final void setX(double arg) { x = arg; }
    protected final void setY(double arg) { y = arg; }
    protected final void setXVel(double arg) { xVel = arg; }
    protected final void setYVel(double arg) { yVel = arg; }
    protected static int randCoord(int min, int max) {
        return (int)(Math.random() * max) + min;
    }
    
    protected final void setPosition(double argX, double argY) {
        x = argX;
        y = argY;
    }
    protected final void setVelocity(double argX, double argY) {
        xVel = argX;
        yVel = argY;
    }
}

