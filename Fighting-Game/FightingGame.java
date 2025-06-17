import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FightingGame extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH = 1000;
    private final int BOARD_HEIGHT = 600;
    private final int GROUND_Y = 500;
    private final int DELAY = 16; // 約60FPS
    
    private Fighter player1;
    private Fighter player2;
    private ArrayList<Projectile> projectiles;
    private Timer timer;
    private boolean[] keysPressed = new boolean[256];
    
    // ゲーム状態
    private boolean gameRunning = true;
    private String winner = "";
    private int roundTime = 99; // 99秒
    private int frameCount = 0;
    
    public FightingGame() {
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setBackground(new Color(135, 206, 235)); // 空色
        this.setFocusable(true);
        this.addKeyListener(this);
        
        initializeGame();
        startGame();
    }
    
    public void initializeGame() {
        // プレイヤー1 (左側、戦士)
        player1 = new Fighter(150, GROUND_Y, Color.RED, true, "warrior");
        player1.name = "Warrior";
        
        // プレイヤー2 (右側、忍者)
        player2 = new Fighter(850, GROUND_Y, Color.BLUE, false, "ninja");
        player2.name = "Ninja";
        
        projectiles = new ArrayList<>();
    }
    
    public void startGame() {
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
        // 背景
        drawBackground(g);
        
        if (gameRunning) {
            // キャラクター描画
            player1.draw(g);
            player2.draw(g);
            
            // 飛び道具描画
            for (Projectile proj : projectiles) {
                proj.draw(g);
            }
            
            // UI描画
            drawUI(g);
        } else {
            drawGameOver(g);
        }
    }
    
    public void drawBackground(Graphics g) {
        // 地面
        g.setColor(new Color(101, 67, 33));
        g.fillRect(0, GROUND_Y, BOARD_WIDTH, BOARD_HEIGHT - GROUND_Y);
        
        // 地面のライン
        g.setColor(Color.BLACK);
        g.drawLine(0, GROUND_Y, BOARD_WIDTH, GROUND_Y);
    }
    
    public void drawUI(Graphics g) {
        // プレイヤー1のHP
        g.setColor(Color.RED);
        g.fillRect(50, 30, (int)(player1.hp * 2), 20);
        g.setColor(Color.BLACK);
        g.drawRect(50, 30, 200, 20);
        
        // プレイヤー2のHP
        g.setColor(Color.BLUE);
        g.fillRect(BOARD_WIDTH - 250, 30, (int)(player2.hp * 2), 20);
        g.setColor(Color.BLACK);
        g.drawRect(BOARD_WIDTH - 250, 30, 200, 20);
        
        // プレイヤー名
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(player1.name, 50, 25);
        g.drawString(player2.name, BOARD_WIDTH - 250, 25);
        
        // 時間
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String timeStr = String.valueOf(roundTime);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(timeStr, (BOARD_WIDTH - fm.stringWidth(timeStr)) / 2, 40);
        
        // 操作説明
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P1: WASD+GH (move+attack) | P2: Arrows+KL", 10, BOARD_HEIGHT - 10);
    }
    
    public void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String message = winner.isEmpty() ? "TIME UP!" : winner + " WINS!";
        g.drawString(message, (BOARD_WIDTH - fm.stringWidth(message)) / 2, BOARD_HEIGHT / 2);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        fm = g.getFontMetrics();
        String restart = "Press SPACE to restart";
        g.drawString(restart, (BOARD_WIDTH - fm.stringWidth(restart)) / 2, BOARD_HEIGHT / 2 + 60);
    }
    
    public void update() {
        if (!gameRunning) return;
        
        frameCount++;
        // 1秒ごとに時間を減らす（60FPS想定）
        if (frameCount % 60 == 0) {
            roundTime--;
            if (roundTime <= 0) {
                gameRunning = false;
                // HP が多い方の勝利
                if (player1.hp > player2.hp) {
                    winner = player1.name;
                } else if (player2.hp > player1.hp) {
                    winner = player2.name;
                }
            }
        }
        
        // プレイヤー入力処理
        handlePlayer1Input();
        handlePlayer2Input();
        
        // キャラクター更新
        player1.update();
        player2.update();
        
        // 衝突判定
        checkFighterCollision();
        
        // 飛び道具更新
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile proj = projIter.next();
            proj.update();
            
            // 画面外に出たら削除
            if (proj.x < 0 || proj.x > BOARD_WIDTH) {
                projIter.remove();
                continue;
            }
            
            // キャラクターとの衝突判定
            Fighter target = (proj.owner == player1) ? player2 : player1;
            if (proj.getBounds().intersects(target.getBounds())) {
                target.takeDamage(proj.damage);
                projIter.remove();
                
                // KO判定
                if (target.hp <= 0) {
                    gameRunning = false;
                    winner = (target == player1) ? player2.name : player1.name;
                }
            }
        }
    }
    
    public void handlePlayer1Input() {
        // WASD + GH
        if (keysPressed[KeyEvent.VK_A]) player1.moveLeft();
        if (keysPressed[KeyEvent.VK_D]) player1.moveRight();
        if (keysPressed[KeyEvent.VK_W]) player1.jump();
        if (keysPressed[KeyEvent.VK_S]) player1.crouch();
        
        if (keysPressed[KeyEvent.VK_G]) player1.punch();
        if (keysPressed[KeyEvent.VK_H]) {
            Projectile fireball = player1.fireball();
            if (fireball != null) {
                projectiles.add(fireball);
            }
        }
    }
    
    public void handlePlayer2Input() {
        // Arrow keys + KL
        if (keysPressed[KeyEvent.VK_LEFT]) player2.moveLeft();
        if (keysPressed[KeyEvent.VK_RIGHT]) player2.moveRight();
        if (keysPressed[KeyEvent.VK_UP]) player2.jump();
        if (keysPressed[KeyEvent.VK_DOWN]) player2.crouch();
        
        if (keysPressed[KeyEvent.VK_K]) player2.punch();
        if (keysPressed[KeyEvent.VK_L]) {
            Projectile fireball = player2.fireball();
            if (fireball != null) {
                projectiles.add(fireball);
            }
        }
    }
    
    public void checkFighterCollision() {
        if (player1.getBounds().intersects(player2.getBounds())) {
            if (player1.isAttacking && !player2.isBlocking) {
                player2.takeDamage(10);
                player1.isAttacking = false;
            }
            if (player2.isAttacking && !player1.isBlocking) {
                player1.takeDamage(10);
                player2.isAttacking = false;
            }
            
            // KO判定
            if (player1.hp <= 0) {
                gameRunning = false;
                winner = player2.name;
            } else if (player2.hp <= 0) {
                gameRunning = false;
                winner = player1.name;
            }
        }
    }
    
    public void restartGame() {
        gameRunning = true;
        winner = "";
        roundTime = 99;
        frameCount = 0;
        projectiles.clear();
        
        player1.reset(150, GROUND_Y);
        player2.reset(850, GROUND_Y);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed[e.getKeyCode()] = true;
        
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameRunning) {
            restartGame();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed[e.getKeyCode()] = false;
        
        // 攻撃のリリース処理
        if (e.getKeyCode() == KeyEvent.VK_G) player1.isAttacking = false;
        if (e.getKeyCode() == KeyEvent.VK_K) player2.isAttacking = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // Fighter クラス
    class Fighter {
        double x, y;
        double velX, velY;
        Color color;
        boolean facingRight;
        String name;
        String characterType; // "warrior", "ninja", "mage"
        
        // 状態
        int hp = 100;
        boolean isJumping = false;
        boolean isCrouching = false;
        boolean isAttacking = false;
        boolean isBlocking = false;
        
        // アニメーション
        int animationFrame = 0;
        int animationCounter = 0;
        String currentAnimation = "idle";
        
        // サイズ
        int width = 50;
        int height = 90;
        
        // 攻撃クールダウン
        int attackCooldown = 0;
        int fireballCooldown = 0;
        
        public Fighter(double x, double y, Color color, boolean facingRight, String characterType) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.facingRight = facingRight;
            this.characterType = characterType;
            this.velX = 0;
            this.velY = 0;
        }
        
        public void update() {
            // アニメーション更新
            animationCounter++;
            if (animationCounter % 8 == 0) { // アニメーション速度調整
                animationFrame = (animationFrame + 1) % 4;
            }
            
            // アニメーション状態設定
            if (isAttacking) {
                currentAnimation = "attack";
            } else if (isCrouching) {
                currentAnimation = "crouch";
            } else if (isJumping) {
                currentAnimation = "jump";
            } else if (Math.abs(velX) > 0.5) {
                currentAnimation = "walk";
            } else {
                currentAnimation = "idle";
            }
            
            // 重力
            if (y < GROUND_Y) {
                velY += 0.8;
                isJumping = true;
            } else {
                y = GROUND_Y;
                velY = 0;
                isJumping = false;
            }
            
            // 位置更新
            x += velX;
            y += velY;
            
            // 画面端制限
            x = Math.max(0, Math.min(BOARD_WIDTH - width, x));
            
            // 摩擦
            velX *= 0.8;
            
            // クールダウン減少
            if (attackCooldown > 0) attackCooldown--;
            if (fireballCooldown > 0) fireballCooldown--;
            
            // しゃがみ状態リセット
            isCrouching = false;
        }
        
        public void moveLeft() {
            velX = -4;
            facingRight = false;
        }
        
        public void moveRight() {
            velX = 4;
            facingRight = true;
        }
        
        public void jump() {
            if (!isJumping) {
                velY = -15;
                isJumping = true;
            }
        }
        
        public void crouch() {
            isCrouching = true;
        }
        
        public void punch() {
            if (attackCooldown <= 0) {
                isAttacking = true;
                attackCooldown = 20;
            }
        }
        
        public Projectile fireball() {
            if (fireballCooldown <= 0) {
                fireballCooldown = 60;
                // キャラクタータイプ別の飛び道具
                switch (characterType) {
                    case "warrior":
                        return new Projectile(x + (facingRight ? width : 0), y + height/2, 
                                            facingRight ? 6 : -6, this, "axe");
                    case "ninja":
                        return new Projectile(x + (facingRight ? width : 0), y + height/3, 
                                            facingRight ? 10 : -10, this, "shuriken");
                    case "mage":
                        return new Projectile(x + (facingRight ? width : 0), y + height/2, 
                                            facingRight ? 8 : -8, this, "fireball");
                    default:
                        return new Projectile(x + (facingRight ? width : 0), y + height/2, 
                                            facingRight ? 8 : -8, this, "basic");
                }
            }
            return null;
        }
        
        public void takeDamage(int damage) {
            hp = Math.max(0, hp - damage);
        }
        
        public void reset(double x, double y) {
            this.x = x;
            this.y = y;
            this.hp = 100;
            this.velX = 0;
            this.velY = 0;
            this.isJumping = false;
            this.isCrouching = false;
            this.isAttacking = false;
            this.attackCooldown = 0;
            this.fireballCooldown = 0;
            this.animationFrame = 0;
            this.currentAnimation = "idle";
        }
        
        public Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, width, isCrouching ? height/2 : height);
        }
        
        public void draw(Graphics g) {
            int drawHeight = isCrouching ? height * 2/3 : height;
            int drawY = (int)(isCrouching ? y + height/3 : y);
            
            // キャラクタータイプ別の描画
            switch (characterType) {
                case "warrior":
                    drawWarrior(g, drawY, drawHeight);
                    break;
                case "ninja":
                    drawNinja(g, drawY, drawHeight);
                    break;
                case "mage":
                    drawMage(g, drawY, drawHeight);
                    break;
                default:
                    drawBasicCharacter(g, drawY, drawHeight);
            }
            
            // HP表示（キャラクター上）
            g.setColor(Color.RED);
            g.fillRect((int)x, drawY - 15, (int)(width * (hp / 100.0)), 6);
            g.setColor(Color.BLACK);
            g.drawRect((int)x, drawY - 15, width, 6);
            
            // 名前表示
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(name, (int)x + (width - fm.stringWidth(name))/2, drawY - 18);
        }
        
        private void drawWarrior(Graphics g, int drawY, int drawHeight) {
            // 体（茶色の鎧）
            g.setColor(new Color(139, 69, 19));
            g.fillRect((int)x + 10, drawY + 20, width - 20, drawHeight - 40);
            
            // 頭（肌色）
            g.setColor(new Color(255, 220, 177));
            g.fillOval((int)x + 15, drawY, width - 30, 25);
            
            // ヘルメット（銀色）
            g.setColor(Color.GRAY);
            g.fillOval((int)x + 12, drawY - 5, width - 24, 20);
            
            // 腕
            g.setColor(new Color(255, 220, 177));
            g.fillRect((int)x + 5, drawY + 25, 10, 30);
            g.fillRect((int)x + width - 15, drawY + 25, 10, 30);
            
            // 脚
            g.setColor(new Color(139, 69, 19));
            g.fillRect((int)x + 15, drawY + drawHeight - 25, 8, 25);
            g.fillRect((int)x + width - 23, drawY + drawHeight - 25, 8, 25);
            
            // 武器（剣）
            if (isAttacking) {
                g.setColor(Color.LIGHT_GRAY);
                int swordX = facingRight ? (int)x + width : (int)x - 25;
                g.fillRect(swordX, drawY + 10, 20, 5);
                g.fillRect(swordX + 15, drawY, 5, 25);
            }
            
            // 目
            g.setColor(Color.BLACK);
            int eyeX = facingRight ? (int)x + width - 25 : (int)x + 15;
            g.fillOval(eyeX, drawY + 8, 3, 3);
            
            // アニメーション効果
            if (currentAnimation.equals("walk") && animationFrame % 2 == 0) {
                // 歩行時の上下動
                drawY -= 2;
            }
        }
        
        private void drawNinja(Graphics g, int drawY, int drawHeight) {
            // 体（黒装束）
            g.setColor(Color.BLACK);
            g.fillRect((int)x + 12, drawY + 15, width - 24, drawHeight - 30);
            
            // 頭（覆面）
            g.setColor(Color.BLACK);
            g.fillOval((int)x + 15, drawY, width - 30, 25);
            
            // 目の部分だけ見える
            g.setColor(new Color(255, 220, 177));
            g.fillRect((int)x + 18, drawY + 8, width - 36, 8);
            
            // 腕
            g.setColor(Color.BLACK);
            g.fillRect((int)x + 7, drawY + 20, 8, 25);
            g.fillRect((int)x + width - 15, drawY + 20, 8, 25);
            
            // 脚
            g.fillRect((int)x + 16, drawY + drawHeight - 20, 6, 20);
            g.fillRect((int)x + width - 22, drawY + drawHeight - 20, 6, 20);
            
            // 武器（手裏剣または刀）
            if (isAttacking) {
                g.setColor(Color.GRAY);
                int weaponX = facingRight ? (int)x + width : (int)x - 15;
                // 手裏剣風
                g.fillRect(weaponX, drawY + 15, 12, 3);
                g.fillRect(weaponX + 4, drawY + 11, 3, 12);
            }
            
            // 目
            g.setColor(Color.RED);
            int eyeX = facingRight ? (int)x + width - 22 : (int)x + 18;
            g.fillOval(eyeX, drawY + 10, 2, 2);
            
            // アニメーション効果（より俊敏な動き）
            if (currentAnimation.equals("walk")) {
                if (animationFrame == 0 || animationFrame == 2) drawY -= 1;
            }
        }
        
        private void drawMage(Graphics g, int drawY, int drawHeight) {
            // ローブ（紫色）
            g.setColor(new Color(128, 0, 128));
            g.fillRect((int)x + 8, drawY + 20, width - 16, drawHeight - 25);
            
            // 頭（肌色）
            g.setColor(new Color(255, 220, 177));
            g.fillOval((int)x + 15, drawY, width - 30, 25);
            
            // 帽子（とんがり帽子）
            g.setColor(new Color(75, 0, 130));
            int[] hatX = {(int)x + width/2, (int)x + 10, (int)x + width - 10};
            int[] hatY = {drawY - 15, drawY + 5, drawY + 5};
            g.fillPolygon(hatX, hatY, 3);
            
            // 腕
            g.setColor(new Color(255, 220, 177));
            g.fillRect((int)x + 3, drawY + 25, 8, 20);
            g.fillRect((int)x + width - 11, drawY + 25, 8, 20);
            
            // 杖
            g.setColor(new Color(139, 69, 19));
            int staffX = facingRight ? (int)x + width + 5 : (int)x - 10;
            g.fillRect(staffX, drawY, 3, 40);
            
            // 杖の先（魔法の玉）
            g.setColor(Color.CYAN);
            g.fillOval(staffX - 3, drawY - 5, 9, 9);
            
            // 魔法攻撃エフェクト
            if (isAttacking) {
                g.setColor(Color.MAGENTA);
                int magicX = facingRight ? (int)x + width : (int)x - 20;
                for (int i = 0; i < 3; i++) {
                    g.fillOval(magicX + i * 8, drawY + 15 + i * 5, 6, 6);
                }
            }
            
            // 目
            g.setColor(Color.BLUE);
            int eyeX = facingRight ? (int)x + width - 22 : (int)x + 15;
            g.fillOval(eyeX, drawY + 10, 3, 3);
        }
        
        private void drawBasicCharacter(Graphics g, int drawY, int drawHeight) {
            // 基本キャラクター（従来のシンプルな描画）
            g.setColor(color);
            g.fillRect((int)x, drawY, width, drawHeight);
            
            if (isAttacking) {
                g.setColor(Color.YELLOW);
                int attackX = facingRight ? (int)x + width : (int)x - 30;
                g.fillRect(attackX, drawY, 30, drawHeight);
            }
            
            g.setColor(Color.WHITE);
            int eyeX = facingRight ? (int)x + width - 15 : (int)x + 5;
            g.fillOval(eyeX, drawY + 10, 8, 8);
        }
    }
    
    // Projectile クラス
    class Projectile {
        double x, y;
        double velX;
        Fighter owner;
        int damage = 20;
        int size = 10;
        String type;
        int animationFrame = 0;
        
        public Projectile(double x, double y, double velX, Fighter owner, String type) {
            this.x = x;
            this.y = y;
            this.velX = velX;
            this.owner = owner;
            this.type = type;
            
            // タイプ別の設定
            switch (type) {
                case "axe":
                    this.damage = 25;
                    this.size = 15;
                    break;
                case "shuriken":
                    this.damage = 15;
                    this.size = 8;
                    break;
                case "fireball":
                    this.damage = 30;
                    this.size = 12;
                    break;
                default:
                    this.damage = 20;
                    this.size = 10;
            }
        }
        
        public void update() {
            x += velX;
            animationFrame++;
        }
        
        public Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, size, size);
        }
        
        public void draw(Graphics g) {
            switch (type) {
                case "axe":
                    drawAxe(g);
                    break;
                case "shuriken":
                    drawShuriken(g);
                    break;
                case "fireball":
                    drawFireball(g);
                    break;
                default:
                    drawBasicProjectile(g);
            }
        }
        
        private void drawAxe(Graphics g) {
            // 回転する斧
            g.setColor(new Color(139, 69, 19));
            int rotation = animationFrame % 8;
            if (rotation < 2) {
                g.fillRect((int)x, (int)y + 3, size, size - 6);
                g.fillRect((int)x + 3, (int)y, size - 6, size);
            } else if (rotation < 4) {
                g.fillOval((int)x + 2, (int)y + 2, size - 4, size - 4);
            } else if (rotation < 6) {
                g.fillRect((int)x + 3, (int)y, size - 6, size);
                g.fillRect((int)x, (int)y + 3, size, size - 6);
            } else {
                g.fillOval((int)x + 1, (int)y + 1, size - 2, size - 2);
            }
            g.setColor(Color.GRAY);
            g.drawOval((int)x, (int)y, size, size);
        }
        
        private void drawShuriken(Graphics g) {
            // 回転する手裏剣
            g.setColor(Color.GRAY);
            int centerX = (int)x + size/2;
            int centerY = (int)y + size/2;
            int rotation = (animationFrame * 2) % 8;
            
            for (int i = 0; i < 4; i++) {
                double angle = (rotation + i * 2) * Math.PI / 4;
                int pointX = centerX + (int)(Math.cos(angle) * size/2);
                int pointY = centerY + (int)(Math.sin(angle) * size/2);
                g.fillOval(pointX - 1, pointY - 1, 3, 3);
            }
            g.setColor(Color.DARK_GRAY);
            g.fillOval(centerX - 2, centerY - 2, 4, 4);
        }
        
        private void drawFireball(Graphics g) {
            // 燃える火球
            int flame = animationFrame % 6;
            
            // 外側の炎
            g.setColor(Color.RED);
            g.fillOval((int)x - 2, (int)y - 2, size + 4, size + 4);
            
            // 中間の炎
            g.setColor(Color.ORANGE);
            g.fillOval((int)x, (int)y, size, size);
            
            // 内側の炎
            g.setColor(Color.YELLOW);
            g.fillOval((int)x + 2, (int)y + 2, size - 4, size - 4);
            
            // 炎のゆらめき効果
            if (flame < 3) {
                g.setColor(Color.ORANGE);
                g.fillOval((int)x - 1, (int)y - 1, 3, 3);
                g.fillOval((int)x + size - 2, (int)y + size - 2, 3, 3);
            }
        }
        
        private void drawBasicProjectile(Graphics g) {
            g.setColor(Color.ORANGE);
            g.fillOval((int)x, (int)y, size, size);
            g.setColor(Color.RED);
            g.drawOval((int)x, (int)y, size, size);
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Fighting Game");
        FightingGame game = new FightingGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}