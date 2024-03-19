package com.flappybird;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlappyBird extends JPanel implements ActionListener {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int BIRD_SIZE = 40;
    private final int BIRD_X = WIDTH / 4 - BIRD_SIZE / 2; 
    private int birdY = HEIGHT / 2;
    private int birdVelocity = 0;
    private final int GRAVITY = 1;
    private final int JUMP_SPEED = -10;
    private final int OBSTACLE_WIDTH = 60;
    private final int OBSTACLE_GAP = 200;
    private final int OBSTACLE_INTERVAL = 1500;
    private final int COIN_DIAMETER = 30;
    private long lastObstacleTime = -OBSTACLE_INTERVAL;
    private List<Rectangle> obstacles;
    private Rectangle coin;
    private boolean gameOver;
    private int score;

    public FlappyBird() {
        JFrame frame = new JFrame();
        Timer timer = new Timer(20, this);
        obstacles = new ArrayList<>();
        gameOver = false;
        score = 0;

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    birdVelocity = JUMP_SPEED;
                    playSound("jump.wav");
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    restartGame();
                }
            }
        });

        timer.start();
    }

    private void addObstacleAndCoin() {
        // Only add a new obstacle and coin if there's no existing coin
        if (coin == null) {
            int spaceTop = (int) (Math.random() * (HEIGHT - OBSTACLE_GAP));
            int coinY = spaceTop + (int) (Math.random() * (OBSTACLE_GAP - COIN_DIAMETER));
            obstacles.add(new Rectangle(WIDTH, 0, OBSTACLE_WIDTH, spaceTop));
            obstacles.add(new Rectangle(WIDTH, spaceTop + OBSTACLE_GAP, OBSTACLE_WIDTH, HEIGHT - spaceTop - OBSTACLE_GAP));
            coin = new Rectangle(WIDTH + OBSTACLE_WIDTH / 2 - COIN_DIAMETER / 2, coinY, COIN_DIAMETER, COIN_DIAMETER);
        }
    }

	private void moveObstaclesAndCoin() {
	    Iterator<Rectangle> iterator = obstacles.iterator();
	    while (iterator.hasNext()) {
	        Rectangle rect = iterator.next();
	        rect.x -= 10;
	        if (rect.x + OBSTACLE_WIDTH < 0) {
	            iterator.remove();
	        }
	    }
	    if (coin != null) {
	        coin.x -= 10;
	        if (coin.x + COIN_DIAMETER < 0) {  // Check if the coin is completely off-screen
	            System.out.println("Coin removed (off-screen)");
	            coin = null;  // Remove the coin only if it's off-screen
	        }
	    }
	}
	
	
    private void playSound(String soundFileName) {
        try {
            URL url = this.getClass().getResource("/sounds/" + soundFileName);
            if (url == null) {
                throw new RuntimeException("Sound file not found: " + soundFileName);
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
	
    private void checkCollisionsAndCollectCoin() {
        Rectangle birdRect = new Rectangle(BIRD_X, birdY, BIRD_SIZE, BIRD_SIZE);
        if (coin != null && coin.intersects(birdRect)) {
            score++;
            playSound("coin.wav");
            coin = null;  // Remove the coin once collected
        }
        for (Rectangle rect : obstacles) {
            if (rect.intersects(birdRect)) {
                gameOver = true;
                birdVelocity = 0;
                break;
            }
        }
        if (birdY + BIRD_SIZE > HEIGHT || birdY < 0) {
            gameOver = true;
            birdY = HEIGHT - BIRD_SIZE;
        }
    }

    private void restartGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        obstacles.clear();
        coin = null;
        gameOver = false;
        score = 0;
        lastObstacleTime = -OBSTACLE_INTERVAL;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the bird
        g.setColor(Color.BLUE);
        g.fillRect(BIRD_X, birdY, BIRD_SIZE, BIRD_SIZE);
        
        // Draw the obstacles
        g.setColor(Color.GREEN);
        for (Rectangle rect : obstacles) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        
        // Draw the coin
        if (coin != null) {
            g.setColor(Color.YELLOW);
            g.fillOval(coin.x, coin.y, coin.width, coin.height);
        }
        
        // Draw the score
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
        
        // Display game over message
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over!", WIDTH / 2 - 50, HEIGHT / 2 - 20);
            g.drawString("Press SPACE to restart", WIDTH / 2 - 100, HEIGHT / 2 + 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            birdY += birdVelocity;
            birdVelocity += GRAVITY;
            long currentTime = System.currentTimeMillis();
            if (coin == null && currentTime - lastObstacleTime > OBSTACLE_INTERVAL) {
                lastObstacleTime = currentTime;
                addObstacleAndCoin();
            }
            moveObstaclesAndCoin();
            checkCollisionsAndCollectCoin();
        }
        repaint();
    }

    public static void main(String[] args) {
        new FlappyBird();
    }
}
