package com.keepkoding;

import java.applet.AudioClip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
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
        PAUSE = 6,
        ADD_POINTS_CHEAT = 7,
        INSTRUCTIONS = 8,
        NEXT_SONG = 9,
        PREVIOUS_SONG = 10;
    
    
    private static final GameBoard gameBoard = new GameBoard();
    
    private static boolean inGame = false;
    
    private static AudioClip musicClips[];
    
    private static boolean gameOver, incSpeed, decSpeed, incAngle, decAngle;
    
    static PlayerShip playerShip;
    static ArrayList<EnemyShip> enemies, tmpEnemies;
    static ArrayList<Asteroid> asteroids, tmpAsteroids;
    
    private static Explosions explosions;
    
    static long currentTick;
    private static int hitpoints, points;
    private static long nextEnemySpawnTick, nextAsteroidSpawnTick;
    private static long ticksPerAsteroidSpawn, ticksPerEnemySpawn;
    
    private static final BufferedImage pointsLabel;
    private static final BufferedImage[] digits;
    private static final Menu mainMenu, inGameMenu;
    
    private static final boolean addPointsCheat = false, debugPrint = false;
    private static final int musicClipCount = 4;
    private static int currentClipNum = 0, ptPerSec;
    
    static {
        musicClips = new AudioClip[musicClipCount];
        
        musicClips[0] = MusicLoader.loadClip("Descent.wav");
        musicClips[1] = MusicLoader.loadClip("Reality.wav");
        musicClips[2] = MusicLoader.loadClip("Slapstick.wav");
        musicClips[3] = MusicLoader.loadClip("Vortex.wav");
        
        pointsLabel = ImageLoader.load("text/points.png");
        digits = new BufferedImage[10];
        for (int i = 0; i < 10; ++i) {
            digits[i] = ImageLoader.load("text/" + i + ".png");
        }
        
        mainMenu = new Menu();
        
        mainMenu.add(new Menu.Button(
            EASY,
            ImageLoader.load("text/easy.png"),
            100, 40,
            KeyEvent.VK_1
        ));
        mainMenu.add(new Menu.Button(
            MEDIUM,
            ImageLoader.load("text/medium.png"),
            100, 80,
            KeyEvent.VK_2
        ));
        mainMenu.add(new Menu.Button(
            HARD,
            ImageLoader.load("text/hard.png"),
            100, 120,
            KeyEvent.VK_3
        ));
        mainMenu.add(new Menu.Button(
            VERY_HARD,
            ImageLoader.load("text/veryHard.png"),
            100, 160,
            KeyEvent.VK_4
        ));
        mainMenu.add(new Menu.Button(
            INSTRUCTIONS,
            ImageLoader.load("text/instructions.png"),
            500, 40
        ));
        mainMenu.add(new Menu.Button(
            PREVIOUS_SONG,
            ImageLoader.load("text/previousSong.png"),
            100, 240
        ));
        mainMenu.add(new Menu.Button(
            NEXT_SONG,
            ImageLoader.load("text/nextSong.png"),
            100, 280
        ));
        
        
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
        
        // I need this because I suck at the game :D
        if (addPointsCheat) {
            inGameMenu.add(new Menu.Button(
                ADD_POINTS_CHEAT,
                ImageLoader.load("text/cheat.png"),
                780, 17,
                KeyEvent.VK_C
            ));
        }
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
        
        if (debugPrint) {
            System.err.println("Difficulty level: " + difficulty);
        }
        
        switch (difficulty) {
            case EASY:
                ticksPerAsteroidSpawn = 80;
                ticksPerEnemySpawn = 180;
                ptPerSec = 3;
                break;
            case MEDIUM:
                ticksPerAsteroidSpawn = 60;
                ticksPerEnemySpawn = 120;
                ptPerSec = 4;
                break;
            case HARD:
                ticksPerAsteroidSpawn = 50;
                ticksPerEnemySpawn = 90;
                ptPerSec = 5;
                break;
            case VERY_HARD:
                ticksPerAsteroidSpawn = 30;
                ticksPerEnemySpawn = 40;
                ptPerSec = 6;
                break;
            default:
                throw new RuntimeException("Unknown difficulty " + difficulty);
        }
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
            } else {
                if (debugPrint) {
                    System.err.println(
                        "\33[33mRemoving off-screen asteroid\33[0m");
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
                points += 100;
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

        if (currentTick % 15 == 0) {
            points += ptPerSec;
        }

        if (currentTick % 450 == 0) {
            ptPerSec++;
        }

        // Add another asteroid if now is the time to do it.
        if (nextAsteroidSpawnTick == currentTick) {
            if (debugPrint) {
                System.err.println("\33[34mSpawning asteroid at tick\33[0m "
                    + currentTick);
            }
            asteroids.add(new Asteroid());
            nextAsteroidSpawnTick += ticksPerAsteroidSpawn;
        }
        
        // Add another enemy if now is the time to do it.
        if (nextEnemySpawnTick == currentTick) {
            if (debugPrint) {
                System.err.println("\33[32mSpawning enemy at tick\33[0m "
                    + currentTick);
            }
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
        
        if (!inGame) {
            mainMenu.paint(g2d);
            return;
        }
        
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
        
        paintPoints(g2d);
        
        // If game has ended, paint the gameOver sign.
        if (gameOver) {
            gameBoard.paintGameOver(g2d);
        }
    }
    
    private static AffineTransform paintPointsTransform = new AffineTransform();
    
    private static void paintPoints(Graphics2D g2d) {
        if (points < 0) {
            throw new RuntimeException("negative points");
        }
        int x = points;
        paintPointsTransform.setToTranslation(screenWidth - 20, 0);
        
        do {
            BufferedImage digitImage = digits[x % 10];
            x /= 10;
            paintPointsTransform.translate(-digitImage.getWidth(), 0);
            g2d.drawRenderedImage(digitImage, paintPointsTransform);
        } while (x != 0);
        
        paintPointsTransform.translate(-pointsLabel.getWidth(), 0);
        g2d.drawRenderedImage(pointsLabel, paintPointsTransform);
    }
    
    private static SpaceShooter initializePanel() {
        JFrame frame = new JFrame("Space Shooter");
        SpaceShooter gamePanel = new SpaceShooter();
        
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        frame.add(gamePanel);
        frame.pack();
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        return gamePanel;
    }
    
    public static void main(String[] args) {
        SpaceShooter gamePanel = null;
        musicClips[currentClipNum].loop();
        
        while (true) {
            if (gamePanel == null) {
                // HACK XXX render game for a bit at startup to reduce lag.
                inGame = true;
                resetGame(EASY);
                createExplosion(0, 0);
                gamePanel = initializePanel();
                for (int i = 0; i < 20; ++i) {
                    gamePanel.repaint();
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ignored) {
                    
                    }
                }
                inGame = false;
                mainMenu.listenToPanel(gamePanel);
            }
            
            int difficulty;
            
            mainMenuLoop:
            while (true) {
                try {
                    gamePanel.repaint();
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                
                }
                int buttonPushed = mainMenu.getPushedButton();
                
                switch (buttonPushed) {
                    case NEXT_SONG:
                        musicClips[currentClipNum].stop();
                        currentClipNum = (currentClipNum+1) % musicClipCount;
                        musicClips[currentClipNum].loop();
                        break;
                    case PREVIOUS_SONG:
                        musicClips[currentClipNum].stop();
                        if (currentClipNum == 0) {
                            currentClipNum = musicClipCount - 1;
                        } else {
                            currentClipNum--;
                        }
                        musicClips[currentClipNum].loop();
                        break;
                    case EASY: case MEDIUM: case HARD: case VERY_HARD:
                        resetGame(buttonPushed);
                        break mainMenuLoop;
                }
            }
            
            boolean paused = false;
            long lastTime = System.nanoTime();
            long NsPerFrame = 33333333;         // 30 fps is our target.
            
            mainMenu.listenToPanel(null);
            inGameMenu.listenToPanel(gamePanel);
            
            inGame = true;
            while (inGame) {
                switch (inGameMenu.getPushedButton()) {
                    case EXIT_GAME:
                        inGame = false;
                        break;
                    case PAUSE:
                        paused = !paused;
                        break;
                    case ADD_POINTS_CHEAT:
                        points += 7;
                        break;
                }
                
                long nextFrameTime = lastTime + NsPerFrame;

                while (System.nanoTime() < nextFrameTime) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                        
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

