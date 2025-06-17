import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BreakoutGame extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH = 800;
    private final int BOARD_HEIGHT = 600;
    private final int DELAY = 8;
    
    // パドル設定
    private final int PADDLE_WIDTH = 100;
    private final int PADDLE_HEIGHT = 15;
    private int paddleX = BOARD_WIDTH / 2 - PADDLE_WIDTH / 2;
    private final int paddleY = BOARD_HEIGHT - 50;
    private int paddleSpeed = 0;
    private final int PADDLE_MAX_SPEED = 8;
    
    // ボール設定
    private final int BALL_SIZE = 15;
    private double ballX = BOARD_WIDTH / 2;
    private double ballY = BOARD_HEIGHT / 2;
    private double ballVelX = 3;
    private double ballVelY = -3;
    
    // ブロック設定
    private final int BLOCK_WIDTH = 75;
    private final int BLOCK_HEIGHT = 25;
    private final int BLOCK_ROWS = 6;
    private final int BLOCK_COLS = 10;
    private ArrayList<Block> blocks;
    
    // ゲーム状態
    private boolean running = false;
    private boolean gameWon = false;
    private int score = 0;
    private int lives = 3;
    private Timer timer;
    
    // 入力状態
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    public BreakoutGame() {
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        
        initializeBlocks();
        startGame();
    }
    
    public void initializeBlocks() {
        blocks = new ArrayList<>();
        int startY = 80;
        
        Color[] colors = {
            Color.RED, Color.ORANGE, Color.YELLOW, 
            Color.GREEN, Color.CYAN, Color.MAGENTA
        };
        
        for (int row = 0; row < BLOCK_ROWS; row++) {
            for (int col = 0; col < BLOCK_COLS; col++) {
                int x = col * (BLOCK_WIDTH + 5) + 40;
                int y = startY + row * (BLOCK_HEIGHT + 5);
                blocks.add(new Block(x, y, colors[row]));
            }
        }
    }
    
    public void startGame() {
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
        if (running) {
            // パドル描画
            g.setColor(Color.WHITE);
            g.fillRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
            
            // ボール描画
            g.setColor(Color.WHITE);
            g.fillOval((int)ballX, (int)ballY, BALL_SIZE, BALL_SIZE);
            
            // ブロック描画
            for (Block block : blocks) {
                if (!block.destroyed) {
                    g.setColor(block.color);
                    g.fillRect(block.x, block.y, BLOCK_WIDTH, BLOCK_HEIGHT);
                    g.setColor(Color.BLACK);
                    g.drawRect(block.x, block.y, BLOCK_WIDTH, BLOCK_HEIGHT);
                }
            }
            
            // UI描画
            drawUI(g);
        } else {
            drawGameOver(g);
        }
    }
    
    public void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // スコア
        g.drawString("Score: " + score, 20, 30);
        
        // ライフ
        g.drawString("Lives: " + lives, 20, 55);
        
        // 残りブロック数
        int remainingBlocks = 0;
        for (Block block : blocks) {
            if (!block.destroyed) remainingBlocks++;
        }
        g.drawString("Blocks: " + remainingBlocks, BOARD_WIDTH - 150, 30);
        
        // 操作説明
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Use ← → arrows to move paddle", BOARD_WIDTH - 200, BOARD_HEIGHT - 20);
    }
    
    public void drawGameOver(Graphics g) {
        g.setColor(gameWon ? Color.GREEN : Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics = getFontMetrics(g.getFont());
        
        String message = gameWon ? "YOU WIN!" : "GAME OVER";
        g.drawString(message, 
                    (BOARD_WIDTH - metrics.stringWidth(message)) / 2, 
                    BOARD_HEIGHT / 2 - 50);
        
        // 最終スコア
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        String scoreText = "Final Score: " + score;
        g.drawString(scoreText, 
                    (BOARD_WIDTH - metrics.stringWidth(scoreText)) / 2, 
                    BOARD_HEIGHT / 2 + 20);
        
        // リスタート指示
        g.setFont(new Font("Arial", Font.BOLD, 20));
        metrics = getFontMetrics(g.getFont());
        String restartText = "Press SPACE to restart";
        g.drawString(restartText, 
                    (BOARD_WIDTH - metrics.stringWidth(restartText)) / 2, 
                    BOARD_HEIGHT / 2 + 80);
    }
    
    public void update() {
        if (!running) return;
        
        // パドル移動
        paddleX += paddleSpeed;
        paddleX = Math.max(0, Math.min(BOARD_WIDTH - PADDLE_WIDTH, paddleX));
        
        // ボール移動
        ballX += ballVelX;
        ballY += ballVelY;
        
        // 壁との衝突
        if (ballX <= 0 || ballX >= BOARD_WIDTH - BALL_SIZE) {
            ballVelX = -ballVelX;
        }
        if (ballY <= 0) {
            ballVelY = -ballVelY;
        }
        
        // 下に落ちた場合
        if (ballY >= BOARD_HEIGHT) {
            lives--;
            if (lives <= 0) {
                running = false;
            } else {
                resetBall();
            }
        }
        
        // パドルとの衝突
        if (ballY + BALL_SIZE >= paddleY && 
            ballX + BALL_SIZE >= paddleX && 
            ballX <= paddleX + PADDLE_WIDTH &&
            ballVelY > 0) {
            
            // パドルの中央からの距離に応じて角度を変える
            double hitPos = (ballX + BALL_SIZE/2 - (paddleX + PADDLE_WIDTH/2)) / (PADDLE_WIDTH/2);
            ballVelX = hitPos * 5; // 最大速度5
            ballVelY = -Math.abs(ballVelY);
            
            // 速度を一定に保つ
            double speed = Math.sqrt(ballVelX * ballVelX + ballVelY * ballVelY);
            ballVelX = (ballVelX / speed) * 5;
            ballVelY = (ballVelY / speed) * 5;
        }
        
        // ブロックとの衝突
        for (Block block : blocks) {
            if (!block.destroyed && 
                ballX + BALL_SIZE >= block.x && 
                ballX <= block.x + BLOCK_WIDTH &&
                ballY + BALL_SIZE >= block.y && 
                ballY <= block.y + BLOCK_HEIGHT) {
                
                block.destroyed = true;
                score += 10;
                
                // 衝突方向の判定
                double ballCenterX = ballX + BALL_SIZE / 2;
                double ballCenterY = ballY + BALL_SIZE / 2;
                double blockCenterX = block.x + BLOCK_WIDTH / 2;
                double blockCenterY = block.y + BLOCK_HEIGHT / 2;
                
                double dx = Math.abs(ballCenterX - blockCenterX);
                double dy = Math.abs(ballCenterY - blockCenterY);
                
                if (dx / BLOCK_WIDTH > dy / BLOCK_HEIGHT) {
                    ballVelX = -ballVelX; // 横から衝突
                } else {
                    ballVelY = -ballVelY; // 上下から衝突
                }
                
                break;
            }
        }
        
        // 勝利条件チェック
        boolean allDestroyed = true;
        for (Block block : blocks) {
            if (!block.destroyed) {
                allDestroyed = false;
                break;
            }
        }
        if (allDestroyed) {
            running = false;
            gameWon = true;
        }
    }
    
    public void resetBall() {
        ballX = BOARD_WIDTH / 2;
        ballY = BOARD_HEIGHT / 2;
        ballVelX = 3;
        ballVelY = -3;
    }
    
    public void restartGame() {
        ballX = BOARD_WIDTH / 2;
        ballY = BOARD_HEIGHT / 2;
        ballVelX = 3;
        ballVelY = -3;
        paddleX = BOARD_WIDTH / 2 - PADDLE_WIDTH / 2;
        score = 0;
        lives = 3;
        gameWon = false;
        
        // ブロックをリセット
        for (Block block : blocks) {
            block.destroyed = false;
        }
        
        running = true;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                paddleSpeed = -PADDLE_MAX_SPEED;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                paddleSpeed = PADDLE_MAX_SPEED;
                break;
            case KeyEvent.VK_SPACE:
                if (!running) {
                    restartGame();
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                if (!rightPressed) paddleSpeed = 0;
                else paddleSpeed = PADDLE_MAX_SPEED;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                if (!leftPressed) paddleSpeed = 0;
                else paddleSpeed = -PADDLE_MAX_SPEED;
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // ブロッククラス
    static class Block {
        int x, y;
        Color color;
        boolean destroyed;
        
        public Block(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.destroyed = false;
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Game");
        BreakoutGame game = new BreakoutGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}