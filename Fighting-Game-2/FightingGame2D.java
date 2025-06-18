import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FightingGame2D extends JPanel implements ActionListener, KeyListener {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final int GROUND_LEVEL = 650;
    private Timer gameTimer;
    private Player player1;
    private Player player2;
    private boolean[] keysPressed = new boolean[256];    private ArrayList<SpecialMove> specialMoves;
    
    // 背景画像
    private BufferedImage backgroundImage;
    
    // ゲーム状態
    private boolean gameRunning = true;
    private String winner = "";
      public FightingGame2D() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        // 背景画像の読み込み
        loadBackgroundImage();
        // プレイヤーの初期化（16:9画面に最適化された位置）
        player1 = new Player(150, GROUND_LEVEL - 320, Color.BLUE, "Player 1");
        player2 = new Player(WINDOW_WIDTH - 200, GROUND_LEVEL - 320, Color.RED, "Player 2");
        
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
        if (backgroundImage != null) {
            // 背景画像をウィンドウサイズに合わせて描画
            g.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, this);
        } else {
            // フォールバック: 和風の夜空背景を手描きで作成
            drawJapaneseNightSky(g);
        }
        
        // 地面の描画を削除（透明な判定のみ）
    }
    
    private void drawJapaneseNightSky(Graphics g) {
        // 夜空のグラデーション
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(20, 30, 80),
            0, WINDOW_HEIGHT, new Color(5, 10, 40)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
          // 月の描画（16:9画面に最適化）
        g.setColor(new Color(255, 255, 200));
        g.fillOval(WINDOW_WIDTH - 200, 60, 100, 100);
        g.setColor(new Color(240, 240, 180));
        g.fillOval(WINDOW_WIDTH - 190, 70, 80, 80);
        
        // 城のシルエット（16:9画面用に拡大）
        g.setColor(new Color(30, 30, 50));
        // 城の基部
        g.fillRect(80, GROUND_LEVEL - 250, 280, 200);
        // 城の屋根
        int[] roofX = {60, 180, 240, 380};
        int[] roofY = {GROUND_LEVEL - 250, GROUND_LEVEL - 320, GROUND_LEVEL - 320, GROUND_LEVEL - 250};
        g.fillPolygon(roofX, roofY, 4);
        
        // 塔（より大きく）
        g.fillRect(280, GROUND_LEVEL - 360, 60, 160);
        g.fillOval(265, GROUND_LEVEL - 390, 90, 50);
        
        // 追加の城の要素（16:9に合わせて追加）
        g.fillRect(400, GROUND_LEVEL - 200, 150, 150);
        g.fillRect(600, GROUND_LEVEL - 180, 120, 130);
        
        // 雲（16:9画面に合わせて配置）
        g.setColor(new Color(80, 80, 120, 150));
        g.fillOval(400, 100, 150, 50);
        g.fillOval(430, 90, 100, 40);
        g.fillOval(750, 120, 130, 45);
        g.fillOval(950, 80, 110, 40);
        
        // 星
        g.setColor(Color.WHITE);
        for (int i = 0; i < 20; i++) {
            int starX = (int)(Math.random() * WINDOW_WIDTH);
            int starY = (int)(Math.random() * (GROUND_LEVEL - 100));
            g.fillOval(starX, starY, 2, 2);
        }
    }
      private void drawUI(Graphics g) {
        // ヘルスバーのサイズを16:9画面に最適化
        int healthBarWidth = 250;
        int healthBarHeight = 25;
        int margin = 20;
        
        // プレイヤー1のヘルスバー（左上）
        g.setColor(Color.WHITE);
        g.drawString(player1.getName(), margin, 25);
        g.setColor(Color.RED);
        g.fillRect(margin, 30, healthBarWidth, healthBarHeight);
        g.setColor(Color.GREEN);
        g.fillRect(margin, 30, (int)(healthBarWidth * (player1.getHealth() / 100.0)), healthBarHeight);
        g.setColor(Color.WHITE);
        g.drawRect(margin, 30, healthBarWidth, healthBarHeight);
        
        // プレイヤー2のヘルスバー（右上）
        g.setColor(Color.WHITE);
        g.drawString(player2.getName(), WINDOW_WIDTH - healthBarWidth - margin, 25);
        g.setColor(Color.RED);
        g.fillRect(WINDOW_WIDTH - healthBarWidth - margin, 30, healthBarWidth, healthBarHeight);
        g.setColor(Color.GREEN);
        g.fillRect(WINDOW_WIDTH - healthBarWidth - margin, 30, (int)(healthBarWidth * (player2.getHealth() / 100.0)), healthBarHeight);
        g.setColor(Color.WHITE);
        g.drawRect(WINDOW_WIDTH - healthBarWidth - margin, 30, healthBarWidth, healthBarHeight);
        
        // 操作説明（画面下部）
        g.setColor(Color.WHITE);
        g.drawString("Player 1: A/D - Move, W - Jump, S - Attack, Q - Special", margin, WINDOW_HEIGHT - 80);
        g.drawString("Player 2: ←/→ - Move, ↑ - Jump, ↓ - Attack, / - Special", margin, WINDOW_HEIGHT - 60);
        g.drawString("Press R to restart", margin, WINDOW_HEIGHT - 40);
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
        } else if (keysPressed[KeyEvent.VK_D]) {
            player1.moveRight();
        } else {
            // キー入力がない場合は水平移動を停止
            player1.stopHorizontalMovement();
        }
        if (keysPressed[KeyEvent.VK_W]) {
            player1.jump();
        }
        if (keysPressed[KeyEvent.VK_S]) {
            player1.attack(player2);
        }
        if (keysPressed[KeyEvent.VK_Q]) {
            // スペシャル攻撃
            if (player1.useSpecialAttack()) {
                double direction = (player2.getX() > player1.getX()) ? 1 : -1;
                // プレイヤー1の向きに応じて発射位置を調整（プレイヤーの幅160px）
                double fireballX = direction > 0 ? player1.getX() + 160 : player1.getX() - 20;
                SpecialMove fireball = new SpecialMove(
                    fireballX, player1.getY() + 80, 
                    direction * 15, 0, Color.RED, 20, "fireball", player1
                );
                specialMoves.add(fireball);
            }
            keysPressed[KeyEvent.VK_Q] = false; // 連続発射を防ぐ
        }
        
        // プレイヤー2の操作
        if (keysPressed[KeyEvent.VK_LEFT]) {
            player2.moveLeft();
        } else if (keysPressed[KeyEvent.VK_RIGHT]) {
            player2.moveRight();
        } else {
            // キー入力がない場合は水平移動を停止
            player2.stopHorizontalMovement();
        }
        if (keysPressed[KeyEvent.VK_UP]) {
            player2.jump();
        }
        if (keysPressed[KeyEvent.VK_DOWN]) {
            player2.attack(player1);
        }
        if (keysPressed[KeyEvent.VK_SLASH]) {
            // スペシャル攻撃
            if (player2.useSpecialAttack()) {
                double direction = (player1.getX() > player2.getX()) ? 1 : -1;
                // プレイヤー2の向きに応じて発射位置を調整（プレイヤーの幅160px）
                double energyBlastX = direction > 0 ? player2.getX() + 160 : player2.getX() - 20;
                SpecialMove energyBlast = new SpecialMove(
                    energyBlastX, player2.getY() + 80, 
                    direction * 15, 0, Color.BLUE, 20, "energy_blast", player2
                );
                specialMoves.add(energyBlast);
            }
            keysPressed[KeyEvent.VK_SLASH] = false; // 連続発射を防ぐ
        }
          // プレイヤーの更新
        player1.update();
        player2.update();
        
        // プレイヤーが相手の方を向くように更新
        player1.faceOpponent(player2);
        player2.faceOpponent(player1);
        
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
    public void keyTyped(KeyEvent e) {}      private void restartGame() {
        player1 = new Player(150, GROUND_LEVEL - 320, Color.BLUE, "Player 1");
        player2 = new Player(WINDOW_WIDTH - 200, GROUND_LEVEL - 320, Color.RED, "Player 2");
        specialMoves.clear();
        gameRunning = true;
        winner = "";
    }
    
    // 背景画像読み込みメソッド
    private void loadBackgroundImage() {
        try {
            // 画像ファイルを読み込み
            backgroundImage = ImageIO.read(new File("image/background.jpg"));
            System.out.println("背景画像を正常に読み込みました");
        } catch (IOException e) {
            System.out.println("背景画像の読み込みに失敗しました: " + e.getMessage());
            // フォールバック: デフォルトの背景を使用
            backgroundImage = null;
        }
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
