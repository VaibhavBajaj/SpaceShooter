package com.keepkoding;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SpaceShooter extends JPanel{

    static final int
            screenWidth = 1440,
            screenHeight = 800;

    private static boolean gameOver = false;
    private boolean incXVel, decXVel, incYVel, decYVel;
    private PlayerShip playerShip = new PlayerShip();
    private EnemyShip enemyShip = new EnemyShip();

    private SpaceShooter() {
        incXVel = decXVel = incYVel = decYVel = false;

        //Adding key listener
        KeyListener listener = new MyKeyListener();
        addKeyListener(listener);
        setFocusable(true);
    }

    private void update() {
        //This function tells the snake to turn right or left depending
        // on the values of the booleans.
        playerShip.update(incXVel, decXVel, incYVel, decYVel);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        playerShip.paint(g2d);
        enemyShip.paint(g2d);
    }

    public static void main(String[] args) {
        // Some java implementations don't seem to use hardware acceleration
        // by default. Force them to behave!
        // Actually, it really depends on the implementation of the jvm
        // on a specific computer: some are really slow w/o OpenGL, some are
        // okay by default and glitch out when OpenGL is forced. Not sure
        // how to handle this for now...
        // System.setProperty("sun.java2d.opengl", "True");
        
        JFrame frame = new JFrame("Space Shooter");

        SpaceShooter game = new SpaceShooter();
        game.setPreferredSize(new Dimension(screenWidth, screenHeight));
        frame.add(game);
        frame.pack();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        while (!gameOver) {
            // Re-paint the screen
            game.repaint();
            // Update the values
            game.update();
            try {
                // SLEEEEEEP. No, seriously. Pauses game for 10 ms
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                // If exception is caused, break out of the game Loop.
                // Effectively hangs the game...
                break;
            }
        }
    }

    // My key listener class
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                // If arrow keys are pressed, make their booleans true
                default:
                break; case KeyEvent.VK_LEFT:     decXVel = true;
                break; case KeyEvent.VK_RIGHT:    incXVel = true;
                break; case KeyEvent.VK_DOWN:     decYVel = true;
                break; case KeyEvent.VK_UP:       incYVel = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                // If arrow keys are released, make their booleans false
                default:
                break; case KeyEvent.VK_LEFT:     decXVel = false;
                break; case KeyEvent.VK_RIGHT:    incXVel = false;
                break; case KeyEvent.VK_DOWN:     decYVel = false;
                break; case KeyEvent.VK_UP:       incYVel = false;
            }
        }
    }
}
