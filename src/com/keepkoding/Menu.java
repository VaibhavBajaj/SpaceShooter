package com.keepkoding;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/** Class representing a group of buttons. Each  button  should  have  a
 *  distinct,  nonzero  ID  associated  that will be used to communicate
 *  which  button  was  pushed  to  the  user  of  the   class   through
 *  getPushedButton,  which  returns 0 if no button was pushed. When the
 *  class is initialized, it does not start listening for button pushes.
 *  It  must  be told to listen to a panel using listenToPanel for mouse
 *  and key events that will be translated to button press IDs.  Calling
 *  listenToPanel  with  null  will  restore the Buttons instance to the
 *  default state of not listening for any button presses. Call paint on
 *  the  Graphics2D  object  associated  with  the  same  panel used for
 *  listening to mouse events to paint the buttons. Add buttons  to  the
 *  menu  by passing a new Button instance to the add method. The Button
 *  instance defines the position,  keyboard  shortcut  (if  any  -  use
 *  KeyEvent.VK_*  constants),  and appearance (BufferedImage used) of a
 *  button in a menu.
 */
class Menu {
    static class Button {
        final BufferedImage image;
        final int x, y, xSize, ySize, id, shortcut;
        final AffineTransform transform;
        
        // Gamble and hope no VK_* constants are -1.
        static final int NO_SHORTCUT = -1;
        
        Button(int id, BufferedImage image, int x, int y, int shortcut) {
            this.id = id;
            this.image = image;
            this.x = x;
            this.y = y;
            this.xSize = image.getWidth();
            this.ySize = image.getHeight();
            this.shortcut = shortcut;
            this.transform = AffineTransform.getTranslateInstance(x, y);
        }
        
        Button(int id, BufferedImage image, int x, int y) {
            this(id, image, x, y, NO_SHORTCUT);
        }
        
        boolean clicked(int x, int y) {
            int dx = x - this.x;
            int dy = y - this.y;
            
            return 0 <= dx & dx <= xSize & 0 <= y & dy <= ySize;
        }
    }
    
    static final int NONE = 0;
    
    ArrayList<Button> buttonList = new ArrayList<Button>();
    AtomicInteger pushedButton = new AtomicInteger(NONE);
    JPanel listeningPanel = null;
    MenuKeyListener keyListener = new MenuKeyListener();
    MenuMouseListener mouseListener = new MenuMouseListener();
    
    void add(Button button) {
        int size = buttonList.size();
        for (int i = 0; i < size; ++i) {
            Button checkedButton = buttonList.get(i);
            if (checkedButton.id == button.id) {
                throw new RuntimeException("Duplicate id " + button.id);
            }
            if (button.shortcut != Button.NO_SHORTCUT
            && checkedButton.shortcut == button.shortcut) {
                throw new RuntimeException(
                    "Duplicate shortcut " + button.shortcut);
            }
        }
        if (button.id == 0) {
            throw new RuntimeException("Button cannot have 0 id.");
        }
        buttonList.add(button);
    }
    
    int getPushedButton() {
        return pushedButton.getAndSet(NONE);
    }
    
    void paint(Graphics2D g2d) {
        int size = buttonList.size();
        for (int i = 0; i < size; ++i) {
            Button b = buttonList.get(i);
            g2d.drawRenderedImage(b.image, b.transform);
        }
    }
    
    void listenToPanel(JPanel panel) {
        if (listeningPanel != null) {
            listeningPanel.removeKeyListener(keyListener);
            listeningPanel.removeMouseListener(mouseListener);
        }
        if (panel != null) {
            panel.addKeyListener(keyListener);
            panel.addMouseListener(mouseListener);
        }
        listeningPanel = panel;
        pushedButton.set(NONE);
    }
    
    private class MenuKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            
            for (int i = buttonList.size() - 1; i != -1; --i) {
                Button b = buttonList.get(i);
                if (b.shortcut == keyCode) {
                    pushedButton.set(b.id);
                    return;
                }
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
        
        }
    }
    
    private class MenuMouseListener implements MouseListener { 
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() != MouseEvent.BUTTON1) {
                return;
            }
            
            int x = e.getX();
            int y = e.getY();
            
            for (int i = buttonList.size() - 1; i != -1; --i) {
                Button b = buttonList.get(i);
                if (b.clicked(x, y)) {
                    pushedButton.set(b.id);
                    return;
                }
            }
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
        
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
        
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
        
        }
    }
}

