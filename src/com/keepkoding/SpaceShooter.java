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
    // ASCII values for left and right.
    private static final int
            LEFT = 37,
            UP = 38,
            RIGHT = 39,
            DOWN = 40;
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
        //This function tells the snake to turn right or left depending on the values of the booleans
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
                // If exception is caused, break out of the game Loop. Effectively hangs the game...
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
                // If left or right are pressed, make their booleans true
                case LEFT: decXVel = true;
                    break;
                case RIGHT: incXVel = true;
                    break;
                case UP: incYVel = true;
                    break;
                case DOWN: decYVel = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                // If left or right are released, make their booleans false
                case LEFT: decXVel = false;
                    break;
                case RIGHT: incXVel = false;
                    break;
                case UP: incYVel = false;
                    break;
                case DOWN: decYVel = false;
                    break;
            }
        }
    }
}
