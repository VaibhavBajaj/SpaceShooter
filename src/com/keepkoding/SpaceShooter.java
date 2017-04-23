package com.keepkoding;

import java.applet.AudioClip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SpaceShooter extends JPanel{
    static final int screenWidth = 1440, screenHeight = 800, maxHitpoints = 10;
    
    private static final int
        EASY = 1,
        MEDIUM = 2,
        HARD = 3,
        VERY_HARD = 4,
        EXIT_GAME = 5,
        PAUSE = 6;
    
    private static final GameBoard gameBoard = new GameBoard();
    
    private static AudioClip bgSound = MusicLoader.loadClip("hindiBgSound.wav");
    
    private static boolean gameOver, incSpeed, decSpeed, incAngle, decAngle;
    
    static PlayerShip playerShip;
    static ArrayList<EnemyShip> enemies, tmpEnemies;
    static ArrayList<Asteroid> asteroids, tmpAsteroids;
    
    private static Explosions explosions;
    
    static long currentTick;
    private static int hitpoints, points;
    private static long nextEnemySpawnTick, nextAsteroidSpawnTick;
    private static long ticksPerAsteroidSpawn, ticksPerEnemySpawn;
    
    private static final Menu mainMenu, inGameMenu;
    
    static {
        mainMenu = new Menu();
        
        inGameMenu = new Menu();
        
        inGameMenu.add(new Menu.Button(
            EXIT_GAME,
            ImageLoader.load("text/exit.png"),
            300, 17
        ));
        inGameMenu.add(new Menu.Button(
            PAUSE,
            ImageLoader.load("text/pause.png"),
            540, 17,
            KeyEvent.VK_P
        ));
    }
    
    private SpaceShooter() {
        addKeyListener(new MyKeyListener());
        setFocusable(true);
    }
    
    private static void resetGame(int difficulty) {
        hitpoints = maxHitpoints;
        
        gameOver = false;
        incSpeed = false;
        decSpeed = false;
        incAngle = false;
        decAngle = false;
        
        playerShip = new PlayerShip();
        
        enemies = new ArrayList<EnemyShip>();
        tmpEnemies = new ArrayList<EnemyShip>();
        
        asteroids = new ArrayList<Asteroid>();
        tmpAsteroids = new ArrayList<Asteroid>();
        
        explosions = new Explosions();
        
        currentTick = 0;
        hitpoints = maxHitpoints;
        points = 0;
        nextEnemySpawnTick = 0;
        nextAsteroidSpawnTick = 0;

        switch (difficulty) {
            case EASY:
                ticksPerAsteroidSpawn = 80;
                ticksPerEnemySpawn = 180;
                break;
            case MEDIUM:
                ticksPerAsteroidSpawn = 60;
                ticksPerEnemySpawn = 120;
                break;
            case HARD:
                ticksPerAsteroidSpawn = 50;
                ticksPerEnemySpawn = 90;
                break;
            case VERY_HARD:
                ticksPerAsteroidSpawn = 30;
                ticksPerEnemySpawn = 40;
                break;
        }

        ticksPerEnemySpawn = 120;
        ticksPerAsteroidSpawn = 60;
    }
    
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
            // that are still on-screen or heading to the screen, and
            // didn't crash into the player.
            if (!a.exitingScreen()) {
                if (a.checkCollision(playerShip)) {
                    createExplosion(playerShip.getX(), playerShip.getY());
                    hitpoints--;
                    if (hitpoints == 0) {
                        gameOver = true;
                    }
                } else {
                    a.update();
                    tmpAsteroids.add(a);
                }
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
            // and create an explosion at the position it was in. Also
            // award a point to the player. Otherwise, check if it collides 
            // with the player. If so, reduce hitpoints.
            // If it is not colliding with either of these things, then
            // update the enemy and add it to the temporary enemy list so
            // it can live to fight another day.
            if (checkEnemyAsteroidCollision(e)) {
                createExplosion(e.getX(), e.getY());
                points++;
                
            } else if (e.checkCollision(playerShip)) {
                createExplosion(
                    (playerShip.getX() + e.getX()) * 0.5,
                    (playerShip.getY() + e.getY()) * 0.5
                );
                hitpoints--;
                if(hitpoints <= 0) {
                    gameOver = true;
                }
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
                asteroids.remove(i);
                return true;
            }
        }
        return false;
    }
    
    private static void createExplosion(double x, double y) {
        explosions.addExplosion((int)x, (int)y);
    }

    @Override
    public void paint(Graphics g) {
        // Clears screen
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        // Paint the background
        gameBoard.paintField(g2d);
        if (!gameOver) {
            // Paint the player
            playerShip.paint(g2d);
        }
        // Paint enemies one by one
        for (EnemyShip enemy : enemies) {
            enemy.paint(g2d);
        }
        // Paint asteroids one by one
        for (Asteroid asteroid : asteroids) {
            asteroid.paint(g2d);
        }
        // Paint the explosions.
        explosions.paint(g2d);
        // Paint the hitpoints
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill3DRect(
            SpaceShooter.screenWidth / 50,
            SpaceShooter.screenHeight / 50,
            SpaceShooter.screenWidth / 6,
            SpaceShooter.screenHeight / 24,
            true
        );
        if(hitpoints < maxHitpoints * 0.33) {
            g2d.setColor(Color.RED);
        }
        else if(hitpoints < maxHitpoints * 0.66) {
            g2d.setColor(Color.YELLOW);
        }
        else {
            g2d.setColor(Color.GREEN);
        }
        g2d.fillRect(
            (SpaceShooter.screenWidth / 50) + 5,
            (SpaceShooter.screenHeight / 50) + 5,
            (int)(((SpaceShooter.screenWidth / 6) *
                ((double)hitpoints / maxHitpoints)) - 10),
            (SpaceShooter.screenHeight / 24) - 10
        );
        
        inGameMenu.paint(g2d);
        
        // If game has ended, paint the gameOver sign.
        if (gameOver) {
            gameBoard.paintGameOver(g2d);
        }
    }
    
    private static SpaceShooter initializePanel() {
        JFrame frame = new JFrame("Space Shooter");
        SpaceShooter gamePanel = new SpaceShooter();
        
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        frame.add(gamePanel);
        frame.pack();
        
        bgSound.loop();
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        return gamePanel;
    }
    
    public static void main(String[] args) {
        SpaceShooter gamePanel = null;
        
        while (true) {
            if (gamePanel == null) {
                gamePanel = initializePanel();
            }
            
            // int difficulty = mainMenuGetDifficulty();
            resetGame(EASY); // lol no Vaibhav I ain't playing on VERY_HARD :P
            
            boolean paused = false;
            long lastTime = System.nanoTime();
            long NsPerFrame = 33333333;         // 30 fps is our target.
            
            mainMenu.listenToPanel(null);
            inGameMenu.listenToPanel(gamePanel);
            
            inGameLoop:
            while (true) {
                switch (inGameMenu.getPushedButton()) {
                    case EXIT_GAME:
                        break inGameLoop;
                    case PAUSE:
                        paused = !paused;
                }
                
                long nextFrameTime = lastTime + NsPerFrame;

                while (System.nanoTime() < nextFrameTime) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                        // ignored.
                    }
                }
                if (!gameOver && !paused) {
                    updateGame();
                }
                gamePanel.repaint();

                lastTime = nextFrameTime;
                ++currentTick;
            }
            inGameMenu.listenToPanel(null);
            mainMenu.listenToPanel(gamePanel);
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
                case KeyEvent.VK_LEFT:
                    decAngle = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    incAngle = true;
                    break;
                case KeyEvent.VK_UP:
                    incSpeed = true;
                    break;
                case KeyEvent.VK_DOWN:
                    decSpeed = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                // If left or right are released, make their booleans false
                case KeyEvent.VK_LEFT:
                    decAngle = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    incAngle = false;
                    break;
                case KeyEvent.VK_UP:
                    incSpeed = false;
                    break;
                case KeyEvent.VK_DOWN:
                    decSpeed = false;
                    break;
            }
        }
    }
}

