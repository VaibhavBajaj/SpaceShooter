package com.keepkoding;

import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SpaceShooter extends JPanel{
    static final int
            screenWidth = 1440,
            screenHeight = 800;
    static int
            enemyShipCount = 1,
            asteroidCount = 1;
    private static boolean
            gameOver = false,
            incXVel,
            decXVel,
            incYVel,
            decYVel;
    private static GameBoard gameBoard = new GameBoard();
    
    // XXX MAKE ALL OF THESE VARIABLES STATIC EVENTUALLY.
    static PlayerShip playerShip = new PlayerShip();
    
    private static ArrayList<EnemyShip> enemies = new ArrayList<EnemyShip>();
    private static ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();

    private static AudioClip bgSound = MusicLoader.loadClip("bgSound.wav");

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
        for (EnemyShip enemy : enemies) {
            enemy.update(playerShip.x, playerShip.y);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        gameBoard.paint(g2d);
        playerShip.paint(g2d);
        
        for (EnemyShip enemy : enemies) {
            enemy.paint(g2d);
        }
        for (Asteroid asteroid : asteroids) {
            asteroid.paint(g2d);
        }
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
        
        for (int i = 0; i < enemyShipCount; ++i) {
            enemies.add(new EnemyShip());
        }
        for (int i = 0; i < asteroidCount; ++i) {
            asteroids.add(new Asteroid(playerShip.x, playerShip.y));
        }

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
