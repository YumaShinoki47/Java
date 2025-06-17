import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

public class FightingGame2D extends JPanel implements ActionListener, KeyListener {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int GROUND_LEVEL = 450;
    private Timer gameTimer;
    private Player player1;
    private Player player2;
    private boolean[] keysPressed = new boolean[256];
    private ArrayList<SpecialMove> specialMoves;
    
    // ゲーム状態
    private boolean gameRunning = true;
    private String winner = "";
    
    public FightingGame2D() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
          // プレイヤーの初期化
        player1 = new Player(100, GROUND_LEVEL - 100, Color.BLUE, "Player 1");
        player2 = new Player(600, GROUND_LEVEL - 100, Color.RED, "Player 2");
        
        // スペシャル攻撃リストの初期化
        specialMoves = new ArrayList<>();
        
        // ゲームタイマーの開始
        gameTimer = new Timer(16, this); // 約60FPS
        gameTimer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 背景の描画
        drawBackground(g);
          // プレイヤーの描画
        player1.draw(g);
        player2.draw(g);
        
        // スペシャル攻撃の描画
        for (SpecialMove move : specialMoves) {
            move.draw(g);
        }
        
        // UIの描画
        drawUI(g);
        
        // ゲーム終了時の表示
        if (!gameRunning) {
            drawGameOver(g);
        }
    }
    
    private void drawBackground(Graphics g) {
        // 地面
        g.setColor(new Color(139, 69, 19));
        g.fillRect(0, GROUND_LEVEL, WINDOW_WIDTH, WINDOW_HEIGHT - GROUND_LEVEL);
        
        // 空
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, WINDOW_WIDTH, GROUND_LEVEL);
        
        // 雲
        g.setColor(Color.WHITE);
        g.fillOval(100, 50, 80, 40);
        g.fillOval(300, 80, 100, 50);
        g.fillOval(600, 40, 90, 45);
    }
    
    private void drawUI(Graphics g) {
        // プレイヤー1のヘルスバー
        g.setColor(Color.WHITE);
        g.drawString(player1.getName(), 10, 20);
        g.setColor(Color.RED);
        g.fillRect(10, 25, 200, 20);
        g.setColor(Color.GREEN);
        g.fillRect(10, 25, (int)(200 * (player1.getHealth() / 100.0)), 20);
        
        // プレイヤー2のヘルスバー
        g.setColor(Color.WHITE);
        g.drawString(player2.getName(), WINDOW_WIDTH - 120, 20);
        g.setColor(Color.RED);
        g.fillRect(WINDOW_WIDTH - 210, 25, 200, 20);
        g.setColor(Color.GREEN);
        g.fillRect(WINDOW_WIDTH - 210, 25, (int)(200 * (player2.getHealth() / 100.0)), 20);
          // 操作説明
        g.setColor(Color.WHITE);
        g.drawString("Player 1: A/D - Move, W - Jump, S - Attack, Q - Special", 10, WINDOW_HEIGHT - 60);
        g.drawString("Player 2: ←/→ - Move, ↑ - Jump, ↓ - Attack, / - Special", 10, WINDOW_HEIGHT - 40);
        g.drawString("Press R to restart", 10, WINDOW_HEIGHT - 20);
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String text = winner + " Wins!";
        int x = (WINDOW_WIDTH - fm.stringWidth(text)) / 2;
        int y = WINDOW_HEIGHT / 2;
        g.drawString(text, x, y);
        
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        text = "Press R to restart";
        fm = g.getFontMetrics();
        x = (WINDOW_WIDTH - fm.stringWidth(text)) / 2;
        y += 50;
        g.drawString(text, x, y);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            updateGame();
        }
        repaint();
    }
    
    private void updateGame() {        // プレイヤー1の操作
        if (keysPressed[KeyEvent.VK_A]) {
            player1.moveLeft();
        }
        if (keysPressed[KeyEvent.VK_D]) {
            player1.moveRight();
        }
        if (keysPressed[KeyEvent.VK_W]) {
            player1.jump();
        }
        if (keysPressed[KeyEvent.VK_S]) {
            player1.attack(player2);
        }
        if (keysPressed[KeyEvent.VK_Q]) {
            // スペシャル攻撃
            double direction = (player2.getX() > player1.getX()) ? 1 : -1;
            SpecialMove fireball = new SpecialMove(
                player1.getX() + 20, player1.getY() + 20, 
                direction * 8, -2, Color.RED, 15, "fireball"
            );
            specialMoves.add(fireball);
            keysPressed[KeyEvent.VK_Q] = false; // 連続発射を防ぐ
        }
        
        // プレイヤー2の操作
        if (keysPressed[KeyEvent.VK_LEFT]) {
            player2.moveLeft();
        }
        if (keysPressed[KeyEvent.VK_RIGHT]) {
            player2.moveRight();
        }
        if (keysPressed[KeyEvent.VK_UP]) {
            player2.jump();
        }
        if (keysPressed[KeyEvent.VK_DOWN]) {
            player2.attack(player1);
        }
        if (keysPressed[KeyEvent.VK_SLASH]) {
            // スペシャル攻撃
            double direction = (player1.getX() > player2.getX()) ? 1 : -1;
            SpecialMove energyBlast = new SpecialMove(
                player2.getX() + 20, player2.getY() + 20, 
                direction * 8, -2, Color.BLUE, 15, "energy_blast"
            );
            specialMoves.add(energyBlast);
            keysPressed[KeyEvent.VK_SLASH] = false; // 連続発射を防ぐ
        }
          // プレイヤーの更新
        player1.update();
        player2.update();
        
        // スペシャル攻撃の更新
        Iterator<SpecialMove> moveIterator = specialMoves.iterator();
        while (moveIterator.hasNext()) {
            SpecialMove move = moveIterator.next();
            move.update();
            move.checkBounds(WINDOW_WIDTH, WINDOW_HEIGHT);
            
            // プレイヤーとの衝突チェック
            if (move.checkCollision(player1) || move.checkCollision(player2)) {
                moveIterator.remove();
            } else if (!move.isActive()) {
                moveIterator.remove();
            }
        }
        
        // 境界チェック
        player1.checkBounds(WINDOW_WIDTH, GROUND_LEVEL);
        player2.checkBounds(WINDOW_WIDTH, GROUND_LEVEL);
        
        // 勝利条件チェック
        if (player1.getHealth() <= 0) {
            winner = player2.getName();
            gameRunning = false;
        } else if (player2.getHealth() <= 0) {
            winner = player1.getName();
            gameRunning = false;
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed[e.getKeyCode()] = true;
        
        // ゲーム再開
        if (e.getKeyCode() == KeyEvent.VK_R && !gameRunning) {
            restartGame();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed[e.getKeyCode()] = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
      private void restartGame() {
        player1 = new Player(100, GROUND_LEVEL - 100, Color.BLUE, "Player 1");
        player2 = new Player(600, GROUND_LEVEL - 100, Color.RED, "Player 2");
        specialMoves.clear();
        gameRunning = true;
        winner = "";
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Fighting Game");
        FightingGame2D game = new FightingGame2D();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
