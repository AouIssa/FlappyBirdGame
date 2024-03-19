package com.flappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlappyBird extends JPanel implements ActionListener {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int BIRD_SIZE = 40;
    private int birdY = HEIGHT / 2;
    private int birdVelocity = 0;
    private final int GRAVITY = 1;
    private final int JUMP_SPEED = -10;
    private final int OBSTACLE_WIDTH = 60;
    private final int OBSTACLE_HEIGHT = 300;
    private final int OBSTACLE_GAP = 200;
    private final int OBSTACLE_INTERVAL = 1500;
    private long lastObstacleTime = -OBSTACLE_INTERVAL;
    private List<Rectangle> obstacles;
    private boolean gameOver;

    public FlappyBird() {
        JFrame frame = new JFrame();
        Timer timer = new Timer(20, this);
        obstacles = new ArrayList<>();
        gameOver = false;

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
                }
            }
        });

        timer.start();
    }

    private void addObstacle() {
        int spaceTop = (int) (Math.random() * (HEIGHT - OBSTACLE_GAP));
        obstacles.add(new Rectangle(WIDTH, 0, OBSTACLE_WIDTH, spaceTop));
        obstacles.add(new Rectangle(WIDTH, spaceTop + OBSTACLE_GAP, OBSTACLE_WIDTH, HEIGHT - spaceTop - OBSTACLE_GAP));
    }

    private void moveObstacles() {
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle rect = iterator.next();
            rect.x -= 10;
            if (rect.x + OBSTACLE_WIDTH < 0) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        for (Rectangle rect : obstacles) {
            if (rect.intersects(new Rectangle(0, birdY, BIRD_SIZE, BIRD_SIZE))) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.fillRect(0, birdY, BIRD_SIZE, BIRD_SIZE);
        g.setColor(Color.GREEN);
        for (Rectangle rect : obstacles) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over!", WIDTH / 2, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            birdY += birdVelocity;
            birdVelocity += GRAVITY;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastObstacleTime > OBSTACLE_INTERVAL) {
                lastObstacleTime = currentTime;
                addObstacle();
            }
            moveObstacles();
            checkCollisions();
        }
        repaint();
    }

    public static void main(String[] args) {
        new FlappyBird();
    }
}
