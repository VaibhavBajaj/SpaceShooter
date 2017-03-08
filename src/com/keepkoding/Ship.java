package com.keepkoding;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

/** Abstract base  class  for  Ships  in  the  game.  The  class  is  mainly
 *  responsible for four things:
 *  
 *  > Storing the sprite, location, and velocity of the represented ship.
 *  > Painting a rotated sprite on a  Graphics2D  object  to  represent  the
 *  ship.
 *  > Updating the position of a  ship  each  frame  given  its  position  &
 *  velocity.
 *  > Enforcing the speed limit (maxVel) for the  ship  and  slowing  it  if
 *  needed.
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
    // The sprite and the transformation used to draw the sprite.
    private AffineTransform transform;
    private BufferedImage sprite;
    
    // The ship will be stopped if it travels below this velocity.
    // This is used in particular to avoid doing math on denormalized numbers.
    private static final double minVel = 1e-30;
    
    // Information on the sprite: its width and height, and half its
    // width and height.
    private int spriteWidth, spriteHeight;
    private double midptX, midptY;
    
    // User-specified and modifiable data.
    protected double x, y;          // Position
    protected double xVel, yVel;    // Velocity
    protected double maxVel;        // Speed limit
    
    protected Ship() { } // XXX Remove this when EnemyShip gets fixed to properly extend this class.
    
    /** Initialize  the  Ship  with  its  basic  information:  its   initial
     *  position,  velocity,  and  speed  limit, and the BufferedImage to be
     *  drawn to represent the ship on screen.
     */
    protected Ship(
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
        
        // To start out, draw the ship unrotated in its original x, y position.
        this.transform = AffineTransform.getTranslateInstance(
            x - midptX, y - midptY
        );
    }
    
    /** Enforce the minimum and maximum velocity of the ship,  and,  if  the
     *  velocity is non-zero, update the position of the ship and update the
     *  private transformation matrix in preparation of repainting.
     */
    protected final void updateBase() {
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
        
        // Update the position based on velocity.
        x += xVel;
        y += yVel;
        
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
    
    /** Return the width of the sprite that the user specified for the ship.
     */
    protected final int getWidth() {
        return spriteWidth;
    }
    
    /** Return the height of the sprite that the user specified for the ship.
     */
    protected final int getHeight() {
        return spriteHeight;
    }
    
    /** Transform and draw the sprite on the specified Graphics2D object.
     */
    void paint(Graphics2D g) {
        g.drawRenderedImage(sprite, transform);
    }
}
