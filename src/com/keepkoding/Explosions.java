package com.keepkoding;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// XXX
import java.awt.Color;
import java.awt.Graphics;

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
    
    static final int spriteCount = 20;
    
    private static BufferedImage[] sprites = new BufferedImage[spriteCount];
    
    static {
        for (int i = 0; i < spriteCount; ++i) {
            // sprites[i] = ImageLoader.load("explosion" + i + ".png");
            
            // Placeholder XXX until we get real explosion sprites.
            BufferedImage sprite = new BufferedImage(160 - i*8, 160 -i*8, BufferedImage.TYPE_INT_ARGB);
            Graphics g = sprite.getGraphics();
            Color c = new Color(1.0f, 1.0f - i*0.04f, 0.1f, 0.9f - i*0.04f);
            g.setColor(c);
            g.fillOval(0, 0, 160 - 8*i, 160 - 8*i);
            
            sprites[i] = sprite;
        }
    }
    
    private final AffineTransform transform = new AffineTransform();
    
    private ArrayList<ExplosionData>
        explosionList = new ArrayList<ExplosionData>(),
        tmpList = new ArrayList<ExplosionData>();
    
    void addExplosion(int x, int y) {
        explosionList.add(new ExplosionData(x, y));
    }
    
    void paint(Graphics2D g) {
        long currentTick = SpaceShooter.currentTick;
        
        tmpList.clear();
        int listSize = explosionList.size();
        for (int from = 0; from < listSize; ++from) {
            ExplosionData data = explosionList.get(from);
            int spriteIndex = (int)(currentTick - data.creationTick);
            
            if (spriteIndex < spriteCount) {
                tmpList.add(data);
                BufferedImage sprite = sprites[spriteIndex];
                int xMidpt = sprite.getWidth() / 2;
                int yMidpt = sprite.getHeight() / 2;
                
                transform.setToTranslation(data.x - xMidpt, data.y - yMidpt);
                
                g.drawRenderedImage(sprite, transform);
            }
        }
        ArrayList<ExplosionData> swapTmp = tmpList;
        tmpList = explosionList;
        explosionList = swapTmp;
    }
}

