import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 600;
    private final int UNIT_SIZE = 25;
    private final int GAME_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private final int DELAY = 100;
    
    private ArrayList<Point> snake;
    private Point food;
    private char direction = 'R'; // R=右, L=左, U=上, D=下
    private boolean running = false;
    private Timer timer;
    private Random random;
    private int score = 0;
    
    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        
        startGame();
    }
    
    public void startGame() {
        snake = new ArrayList<>();
        snake.add(new Point(0, 0)); // 初期位置
        newFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
        if (running) {
            // グリッドライン（デバッグ用、コメントアウト可能）
            g.setColor(Color.GRAY);
            for (int i = 0; i < BOARD_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, BOARD_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, BOARD_WIDTH, i * UNIT_SIZE);
            }
            
            // 食べ物を描画
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);
            
            // スネークを描画
            for (int i = 0; i < snake.size(); i++) {
                if (i == 0) {
                    // 頭部（緑色）
                    g.setColor(Color.GREEN);
                } else {
                    // 体部（明るい緑色）
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(snake.get(i).x, snake.get(i).y, UNIT_SIZE, UNIT_SIZE);
            }
            
            // スコア表示
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, 
                        (BOARD_WIDTH - metrics.stringWidth("Score: " + score)) / 2, 
                        g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
    
    public void newFood() {
        food = new Point(
            random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE,
            random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE
        );
    }
    
    public void move() {
        Point newHead = new Point(snake.get(0));
        
        switch (direction) {
            case 'U':
                newHead.y -= UNIT_SIZE;
                break;
            case 'D':
                newHead.y += UNIT_SIZE;
                break;
            case 'L':
                newHead.x -= UNIT_SIZE;
                break;
            case 'R':
                newHead.x += UNIT_SIZE;
                break;
        }
        
        snake.add(0, newHead);
        
        // 食べ物を食べたかチェック
        if (newHead.equals(food)) {
            score++;
            newFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }
    
    public void checkFood() {
        // move()メソッド内で処理済み
    }
    
    public void checkCollisions() {
        // 頭が体に当たったかチェック
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }
        
        // 頭が境界に当たったかチェック
        if (head.x < 0 || head.x >= BOARD_WIDTH || head.y < 0 || head.y >= BOARD_HEIGHT) {
            running = false;
        }
        
        if (!running) {
            timer.stop();
        }
    }
    
    public void gameOver(Graphics g) {
        // スコア表示
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + score, 
                    (BOARD_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, 
                    g.getFont().getSize());
        
        // ゲームオーバーテキスト
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", 
                    (BOARD_WIDTH - metrics2.stringWidth("Game Over")) / 2, 
                    BOARD_HEIGHT / 2);
        
        // リスタート指示
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press SPACE to restart", 
                    (BOARD_WIDTH - metrics3.stringWidth("Press SPACE to restart")) / 2, 
                    BOARD_HEIGHT / 2 + 100);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!running) {
                    score = 0;
                    startGame();
                }
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}