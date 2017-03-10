package com.keepkoding;

import java.applet.AudioClip;
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
    private GameBoard gameBoard = new GameBoard();
    private PlayerShip playerShip = new PlayerShip();
    private EnemyShip enemyShip = new EnemyShip();
    private AudioClip bgSound = MusicLoader.loadClip("bgSound.wav");

    private SpaceShooter() {
        incXVel = decXVel = incYVel = decYVel = false;

        //Adding key listener
        KeyListener listener = new MyKeyListener();
        addKeyListener(listener);
        setFocusable(true);

        bgSound.loop();
    }

    private void update() {
        playerShip.update(incXVel, decXVel, incYVel, decYVel);
        enemyShip.update(playerShip.x, playerShip.y);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        gameBoard.paint(g2d);
        playerShip.paint(g2d);
        enemyShip.paint(g2d);
    }

    public static void main(String[] args) {
        // Some java implementations don't seem to use hardware acceleration
        // by default. Force them to behave!
        System.setProperty("sun.java2d.opengl", "True");
        
        JFrame frame = new JFrame("Space Shooter");

        SpaceShooter game = new SpaceShooter();
        game.setPreferredSize(new Dimension(screenWidth, screenHeight));
        frame.add(game);
        frame.pack();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /*
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
        */


        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;
        while (!gameOver) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / ns;
            lastTime = currentTime;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {

            }
            while (delta >= 1) {
                game.repaint();
                game.update();
                delta--;
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
                case KeyEvent.VK_LEFT: decXVel = true;
                    break;
                case KeyEvent.VK_RIGHT: incXVel = true;
                    break;
                case KeyEvent.VK_UP: incYVel = true;
                    break;
                case KeyEvent.VK_DOWN: decYVel = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                // If left or right are released, make their booleans false
                case KeyEvent.VK_LEFT: decXVel = false;
                    break;
                case KeyEvent.VK_RIGHT: incXVel = false;
                    break;
                case KeyEvent.VK_UP: incYVel = false;
                    break;
                case KeyEvent.VK_DOWN: decYVel = false;
                    break;
            }
        }
    }
}
