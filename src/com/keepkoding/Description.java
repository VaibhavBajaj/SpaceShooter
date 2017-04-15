
package com.keepkoding;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.concurrent.atomic.AtomicBoolean;

/** Class for describing the essential characteristics of a game object: its
 *  sprite,   maximum  velocity,  collision  detection  circle,  and  anchor
 *  position. The meanings of these parameters will be discussed  in  detail
 *  in their respective setters. Create an instance of this class by calling
 *  the constructor using the sprite and diagonal size that you  want,  then
 *  "chain"  setters to that constructor call to add behavior that you want,
 *  then pass the created object to the GameObj class constructor to control
 *  the  GameObj's  behavior. Please treat the instance variables as if they
 *  were private, and do not call any setters once the object is  passed  to
 *  the GameObj constructor.
 *  
 *  Example syntax:
 *      new Description("foo.png", 1337.)
 *      .setAnchor(.4, .6),
 *      .setCollisionDetection(.5, .5, 100.);
 */
final class Description {
    // Instance variables are public because the GameObj class needs to access
    // them, and java does not support class friendship :( Please do not
    // write or read them yourself. The design of this class may be changed
    // later. Use the methods to manipulate the variables.
    final BufferedImage scaledSprite_;
    
    
    // Default values for non-essential variables. By default, collision
    // detection is disabled by setting the radius to negative infinity.
    double maxVelocity_ = 5.0;
    
    // All values here are in pixels.
    // Collision detection disabled by default by setting radius to -inf.
    double collisionRadius_ = -1./0.;
    double anchorX_, anchorY_;
    Point2D.Double collisionCenter_ = new Point2D.Double(0, 0);
    
    private class MyImageObserver implements ImageObserver {
        private AtomicBoolean finished = new AtomicBoolean(false);
        
        public boolean imageUpdate(Image i, int a, int x, int y, int w, int h) {
            finished.set(true);
            return true;
        }
        
        public void waitUntilFinished() {
            while (!finished.get()) {
                
            }
        }
    }
    
    /** Create a new description  object  using  a  scaled  version  of  the
     *  argument sprite. The sprite used to render the GameObj parameterized
     *  by this Description object will have a diagonal that is diagonalSize
     *  times  the diagonal of the screen. For example, if the screen is 500
     *  by 1200 pixels (1300 pixel diagonal), rawSprite has a size of 660 by
     *  880  pixels,  and diagonalSize is 5./13., then the sprite inside the
     *  Description object will be 300 by 400 pixels (500 pixel diagonal).
     */
    Description(BufferedImage rawSprite, double diagonalSize) {
        double screenDiagonal = Math.hypot(
            SpaceShooter.screenWidth, SpaceShooter.screenHeight
        );
        double rawSpriteDiagonal = Math.hypot(
            rawSprite.getWidth(), rawSprite.getHeight()
        );
        
        double scale = diagonalSize * screenDiagonal / rawSpriteDiagonal;
        
        int width = (int)(scale * rawSprite.getWidth() + 0.5);
        int height = (int)(scale * rawSprite.getHeight() + 0.5);
        
        scaledSprite_ = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB
        );
        
        // Code adapted from StackOverflow user "Hovercraft Full Of Eels".
        Image toolkitImage = rawSprite.getScaledInstance(
            width, height, Image.SCALE_SMOOTH
        );
        
        Graphics g = scaledSprite_.getGraphics();
        try {
            MyImageObserver observer = new MyImageObserver();
            g.drawImage(toolkitImage, 0, 0, observer);
            observer.waitUntilFinished();
        } finally {
            g.dispose();
        }
        
        // By default, the center of the sprite will be treated
        // as the anchor point.
        anchorX_ = scaledSprite_.getWidth() * 0.5;
        anchorY_ = scaledSprite_.getHeight() * 0.5;
    }
    
    /** Same as the Description(BufferedImage, double) constructor, but  the
     *  raw  sprite is loaded from the String filename using the ImageLoader
     *  class.
     */
    Description(String spriteName, double diagonalSize) {
        this(ImageLoader.load(spriteName), diagonalSize);
    }
    
    /** Set the maximum velocity of objects described by this Description.
     */
    Description setMaxVelocity(double value) {
        maxVelocity_ = value;
        return this;
    }
    
    /** Objects described by this description will behave as if its position
     *  of the object is the same as the position (x, y) of the sprite. This
     *  controls, among other things,  the  point  about  which  the  object
     *  appears  to  rotate.  The  x  and  y  coordinates are represented as
     *  proportions of the sprite, not as pixels: (.5, .5) is the center  of
     *  the sprite and (1., 1.) is the lower-right corner.
     */
    Description setAnchor(double x, double y) {
        anchorX_ = x * scaledSprite_.getWidth();
        anchorY_ = y * scaledSprite_.getHeight();
        return this;
    }
    
    /** Two objects are considered  to  have  collided  if  their  collision
     *  circles  overlap. The collision circle for an object is defined as a
     *  circle of the specified radius,  centered  at  the  point  (xOffset,
     *  yOffset)  of  the  sprite.  All  these  values  are  represented  as
     *  proportions of the sprite: (.5, .5) is the center of the sprite.
     *  
     *  Example:  if  the  scaled  sprite  is  100   by   100   pixels   and
     *  setCollisionDetection  is  called  with  (.5,  .25,  .4),  then  the
     *  collision circle is the circle of radius 40 pixels centered  at  the
     *  point (50 pixels, 25 pixels) of the scaled sprite.
     */
    Description setCollisionDetection(
        double x, double y, double radius
    ) {
        double w = scaledSprite_.getWidth();
        double h = scaledSprite_.getHeight();
        collisionCenter_.x = x * w;
        collisionCenter_.y = y * h;
        collisionRadius_ = radius * 0.5 * Math.hypot(w, h);
        return this;
    }
    
    /** Set the collision circle of described objects to be  the  circle  of
     *  the  specified  radius  (expressed  as  a proportion of the sprite's
     *  size) centered at the center of the sprite.
     */
    Description setCollisionDetection(double radius) {
        double w = scaledSprite_.getWidth();
        double h = scaledSprite_.getHeight();
        collisionCenter_.x = 0.5 * w;
        collisionCenter_.y = 0.5 * h;
        collisionRadius_ = radius * 0.5 * Math.hypot(w, h);
        return this;
    }

    /** Return the width in pixels of the  sprite  used  to  render  GameObj
     *  instances initialized with this description.
     */
    int getSpriteWidth() {
        return scaledSprite_.getWidth();
    }
    
    /** Return the height in pixels of the sprite  used  to  render  GameObj
     *  instances initialized with this description.
     */
    int getSpriteHeight() {
        return scaledSprite_.getHeight();
    }
}

