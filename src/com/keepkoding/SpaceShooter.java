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
            ticksPerEnemySpawn = 120,
            ticksPerAsteroidSpawn = 60;
        
    
    static long currentTick = 0;
    
    private static boolean
            gameOver = false,
            incSpeed = false,
            decSpeed = false,
            incAngle = false,
            decAngle = false;
    private static GameBoard gameBoard = new GameBoard();
    
    static PlayerShip playerShip = new PlayerShip();
    
    private static ArrayList<EnemyShip> enemies = new ArrayList<EnemyShip>();
    private static ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    private static ArrayList<EnemyShip> tmpEnemies = new ArrayList<EnemyShip>();
    private static ArrayList<Asteroid> tmpAsteroids = new ArrayList<Asteroid>();

    private static AudioClip bgSound = MusicLoader.loadClip("bgSound.wav");

    private SpaceShooter() {
        addKeyListener(new MyKeyListener());
        setFocusable(true);
    }
    
    private static boolean temp = false; // XXX
    
    private static long nextEnemySpawnTick = 0;
    private static long nextAsteroidSpawnTick = 0;
    
    private static void updateGame() {
        playerShip.update(incSpeed, decSpeed, incAngle, decAngle);
        
        int asteroidCount = asteroids.size();
        int enemyCount = enemies.size();
        
        tmpAsteroids.clear();
        for (int i = 0; i < asteroidCount; ++i) {
            // Use the temporary ArrayList to purge all asteroids that are
            // off screen and will not return. Swap the temporary ArrayList
            // with the main asteroids ArrayList, and use the object that was
            // formerly the main ArrayList as the temporary for the next tick.
            Asteroid a = asteroids.get(i);
            
            
            // Check collisions with the player, update & keep asteroids
            // that are still on-screen or heading to the screen.
            if (!a.exitingScreen()) {
                if (a.checkCollision(playerShip)) {
                    createExplosion(playerShip.getX(), playerShip.getY());
                    gameOver();
                }
                a.update();
                tmpAsteroids.add(a);
            }
        }
        ArrayList<Asteroid> swapTmpAsteroids = asteroids;
        asteroids = tmpAsteroids;
        tmpAsteroids = swapTmpAsteroids;
        
        tmpEnemies.clear();
        for (int i = 0; i < enemyCount; ++i) {
            EnemyShip e = enemies.get(i);
            
            // If an enemy is colliding with an asteroid, remove it from
            // the array list by not adding it to the temporary enemies,
            // and create an explosion at the position it was in. Otherwise,
            // update the enemy.
            if (checkEnemyAsteroidCollision(e)) {
                createExplosion(e.getX(), e.getY());
            } else {
                e.update();
                tmpEnemies.add(e);
            }
        }
        ArrayList<EnemyShip> swapTmpEnemies = enemies;
        enemies = tmpEnemies;
        tmpEnemies = swapTmpEnemies;
        
        // Add another asteroid if now is the time to do it.
        if (nextAsteroidSpawnTick == currentTick) {
            asteroids.add(new Asteroid());
            nextAsteroidSpawnTick += ticksPerAsteroidSpawn;
        }
        
        // Add another enemy if now is the time to do it.
        if (nextEnemySpawnTick == currentTick) {
            enemies.add(new EnemyShip());
            nextEnemySpawnTick += ticksPerEnemySpawn;
        }
    }
    
    private static boolean checkEnemyAsteroidCollision(EnemyShip enemyArg) {
        int asteroidCount = asteroids.size();
        for (int i = 0; i < asteroidCount; ++i) {
            Asteroid a = asteroids.get(i);
            if (a.checkCollision(enemyArg)) {
                return true;
            }
        }
        return false;
    }
    
    private static void createExplosion(double x, double y) {
        // Placeholder for now FIXME.
    }
    
    private static void gameOver() {
        System.out.println("GAME OVER!!!!!!11111!!1!!1one!!!");
        // Placeholder for now FIXME.
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
        
        SpaceShooter gamePanel = new SpaceShooter();
        
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        frame.add(gamePanel);
        frame.pack();
        
        bgSound.loop();
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        long lastTime = System.nanoTime();
        long NsPerFrame = 33333333;         // 30 fps is our target.
        
        while (!gameOver) {
            long nextFrameTime = lastTime + NsPerFrame;
            updateGame();
            gamePanel.repaint();
            
            while (System.nanoTime() < nextFrameTime) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                    // ignored.
                }
            }
            lastTime = nextFrameTime;
            ++currentTick;
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
                case KeyEvent.VK_LEFT: decAngle = true;
                    break;
                case KeyEvent.VK_RIGHT: incAngle = true;
                    break;
                case KeyEvent.VK_UP: incSpeed = true;
                    break;
                case KeyEvent.VK_DOWN: decSpeed = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                // If left or right are released, make their booleans false
                case KeyEvent.VK_LEFT: decAngle = false;
                    break;
                case KeyEvent.VK_RIGHT: incAngle = false;
                    break;
                case KeyEvent.VK_UP: incSpeed = false;
                    break;
                case KeyEvent.VK_DOWN: decSpeed = false;
                    break;
            }
        }
    }
}

