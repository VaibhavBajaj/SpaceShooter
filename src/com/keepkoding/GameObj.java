package com.keepkoding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/*  Abstract base class for game objects in the game. The  class  is  mainly
 *  responsible for five things:
 *  
 *   > Storing the sprite, location, and velocity of the represented ship.
 *   > Painting a rotated sprite on a Graphics2D object to represent the ship.
 *   > Updating the position of a ship each frame given its position & velocity.
 *   > Enforcing the speed limit (d.maxVelocity_) and slowing down if needed.
 *   > Checking collision detection between objects.
 *  
 *  To use this class, extend your own  GameObj  class  using  this  GameObj
 *  class  as a base, and construct the base using a Description object that
 *  specifies (among other things... see Description class  for  more  info)
 *  the  sprite  and maximum velocity for the object. Call updateBase() each
 *  tick to update the ship's position and velocity based  on  its  velocity
 *  and  maxVel  respectively  (this  usually  would  be  done  in  your own
 *  GameObj's update method). To paint the ship, call  paint(Graphics2D)  on
 *  the Graphics2D object you want the ship painted on; you may either leave
 *  the paint method as-is or override  it  and  call  super.paint  in  your
 *  overrided method.
 */
abstract class GameObj {
    // The transformation used to draw the sprite.
    private AffineTransform transform;
    
    // Temporary point object that can be used as a destination point
    // for transformation methods.
    private Point2D.Double tmpPoint = new Point2D.Double();
    
    private static final int errorRange = (int)(SpaceShooter.screenWidth / 7.2);
    
    static boolean drawCollisionDebug = false;
    
    private Description d;
    
    private double x, y, angle, speed;
    
    /** Initialize the GameObj with its  basic  information  specified  in  the
     *  description  object,  and  set  the initial coordinates and velocity
     *  randomly, subject to these rules:
     *  The position will be a random  off-screen  coordinate,  with  a  1/4
     *  chance  of  being  to the left of the left edge, 1/4 chance of being
     *  above the top edge, and so on.
     *  The velocity will be towards  some  random  coordinate  that  is  on
     *  screen.
     */
    GameObj(Description d) {
        this.d = d;
        switch (randCoord(0,4)) {
            case 0:
                this.x = randCoord(-1 * errorRange, 0);
                this.y = randCoord(-1 * errorRange, SpaceShooter.screenHeight + errorRange);
                break;
            case 1:
                this.x = randCoord(SpaceShooter.screenWidth, SpaceShooter.screenWidth + errorRange);
                this.y = randCoord(-1 * errorRange, SpaceShooter.screenHeight + errorRange);
                break;
            case 2:
                this.x = randCoord(-1 * errorRange, SpaceShooter.screenWidth + errorRange);
                this.y = randCoord(-1 * errorRange, 0);
                break;
            case 3:
                this.x = randCoord(-1 * errorRange, SpaceShooter.screenWidth + errorRange);
                this.y = randCoord(SpaceShooter.screenHeight, SpaceShooter.screenHeight + errorRange);
                break;
        }
        this.setVelocity(randCoord(1, SpaceShooter.screenWidth-1) - this.x,
                randCoord(1, SpaceShooter.screenHeight-1) - this.y);

        this.transform = AffineTransform.getTranslateInstance(
                x - d.anchorX_, y - d.anchorY_
        );
    }
    
    /** Initialized the GameObj with its basic information specified in  the
     *  Description object, and set the initial coordinate and velocity.
     */
    GameObj(Description d, double x, double y, double xVel, double yVel) {
        this.d = d;
        this.x = x;
        this.y = y;
        this.setVelocity(xVel, yVel);
        
        this.transform = AffineTransform.getTranslateInstance(
            x - d.anchorX_, y - d.anchorY_
        );
    }
    
    /** Enforce the minimum and maximum velocity of the ship,  and,  if  the
     *  velocity is non-zero, update the position of the ship and update the
     *  private transformation matrix in preparation of repainting.
     */
    final void updateBase() {
        // Reduce the speed if we are exceeding the maximum allowed velocity.
        speed = Math.min(speed, d.maxVelocity_);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Magic center + rotate + translate matrix.
        double ax = d.anchorX_, ay = d.anchorY_;
        transform.setTransform(
            sin, -cos,
            -cos, -sin,
            x - ax*sin + ay*cos, y + ax*cos + ay*sin
        );
        
        x += cos * speed;
        y += sin * speed;
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
    
    /** Return whether  this  GameObj  and  that  GameObj  have  overlapping
     *  collision  circles  (as  defined  by  their  respective  Description
     *  objects).
     */
    final boolean checkCollision(GameObj that) {
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
    
    /** Return the width of the sprite used to render this object.
     */
    final int getWidth() {
        return d.getSpriteWidth();
    }
    
    /** Return the height of the sprite used to render this object.
     */
    final int getHeight() {
        return d.getSpriteHeight();
    }
    
    final double getX() { return x; }
    final double getY() { return y; }
    final double getXVel() { return speed * Math.cos(angle); }
    final double getYVel() { return speed * Math.sin(angle); }
    final double getSpeed() { return speed; }
    final double getAngle() { return angle; }
    
    protected final void setX(double arg) {
        x = arg;
    }
    protected final void setY(double arg) {
        y = arg;
    }
    protected final void setPosition(double argX, double argY) {
        x = argX;
        y = argY;
    }
    /** Set the velocity of this object using cartesian coordinates.
     */
    protected final void setVelocity(double xVel, double yVel) {
        angle = Math.atan2(yVel, xVel);
        speed = Math.hypot(yVel, xVel);
    }
    protected final void setSpeed(double speed) {
        this.speed = speed;
    }
    protected final double addSpeed(double delta) {
        this.speed += delta;
        if (this.speed < 0) {
            speed = 0;
        }
        return this.speed;
    }
    protected final void setAngle(double angle) {
        this.angle = angle;
    }
    protected final double addAngle(double delta) {
        this.angle += delta;
        this.angle %= 6.283185307179586;
        return this.angle;
    }
    protected final void setXVel(double xVel) {
        setVelocity(xVel, getYVel());
    }
    protected final void setYVel(double yVel) {
        setVelocity(getXVel(), yVel);
    }
    
    protected static int randCoord(int min, int max) {
        return (int)(Math.random() * max) + min;
    }
}

