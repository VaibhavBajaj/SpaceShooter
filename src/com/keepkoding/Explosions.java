package com.keepkoding;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/** Class that manages a list of explosions that should be drawn on  screen.
 *  For  each  explosion, the class remembers the x and y coordinates of the
 *  explosion (in pixels), and the game tick  in  which  the  explosion  was
 *  created. The class is initialized with a sequence of sprites to use when
 *  drawing the explosion. In each tick,  the  explosion  'decays'  by  one,
 *  drawing  the  sprite  that is next in the sequence after the sprite that
 *  was drawn in the previous tick. In other words, on the 24th tick  sprite
 *  number  24  will  be  drawn,  on  the 25th tick sprite number 25 will be
 *  drawn, etc. When the class runs out of  sprites  to  draw  an  explosion
 *  with,  the  memory  used to store the explosion will be released and the
 *  explosion will no longer be drawn on-screen.
 */
class Explosions {
    private static final class ExplosionData {
        final int x, y;
        final long creationTick;
        
        ExplosionData(int x, int y) {
            this.x = x;
            this.y = y;
            this.creationTick = SpaceShooter.currentTick;
        }
    }
    
    static final int spriteCount = 90;
    
    // This is the sequence of sprites that are used to animate an explosion.
    // The length of the array is the number of ticks that an explosion
    // will remain on-screen.
    private static BufferedImage[] sprites = new BufferedImage[spriteCount];
    private static final String filenameFormat = "explosionFrames/explosion1_%04d.png";
    
    static {
        for (int i = 0; i < spriteCount; ++i) {
            String name = String.format(filenameFormat , i + 1);
            sprites[i] = ImageLoader.load(name);
        }
    }
    
    private final AffineTransform transform = new AffineTransform();
    
    private ArrayList<ExplosionData>
        explosionList = new ArrayList<ExplosionData>(),
        tmpList = new ArrayList<ExplosionData>();
    
    /** Add an explosion with the  coordinates  specified  to  the  list  of
     *  explosions  to be drawn. The class automatically keeps track of when
     *  the explosion was added based on the  SpaceShooter  class's  current
     *  tick.
     */
    void addExplosion(int x, int y) {
        explosionList.add(new ExplosionData(x, y));
    }
    
    /** Paint the list of explosions at the  correct  coordinates  onto  the
     *  graphics  object. This method depends on the value of currentTick in
     *  the SpaceShooter class, and automatically draws  using  the  correct
     *  sprite and remove explosions that have finished being animated (i.e.
     *  ran out of sprites to draw).
     */
    void paint(Graphics2D g) {
        long currentTick = SpaceShooter.currentTick;
        
        // Temporary list used as part of the process of removing explosions
        // that are no longer rendered.
        tmpList.clear();
        
        int listSize = explosionList.size();
        for (int i = 0; i < listSize; ++i) {
            ExplosionData data = explosionList.get(i);
            int explosionAge = (int)(currentTick - data.creationTick);
            
            // Draw the explosion with the correct sprite and add it to the
            // list of explosions to keep only if there were still enough
            // sprites in the sprite sequence to draw the explosion with.
            if (explosionAge < spriteCount) {
                tmpList.add(data);
                BufferedImage sprite = sprites[explosionAge];
                int xMidpt = sprite.getWidth() / 2;
                int yMidpt = sprite.getHeight() / 2;
                
                transform.setToTranslation(data.x - xMidpt, data.y - yMidpt);
                
                g.drawRenderedImage(sprite, transform);
            }
        }
        // Swap out the temporary list containing the explosions that we still
        // want to keep with the main list of explosions for the next tick.
        ArrayList<ExplosionData> swapTmp = tmpList;
        tmpList = explosionList;
        explosionList = swapTmp;
    }
}

