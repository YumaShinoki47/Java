import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialMove {
    private double x, y;
    private double startX;  // 発射位置を記録
    private double velocityX, velocityY;
    private Color color;
    private int damage;
    private int lifeTime;
    private int maxLifeTime;
    private boolean active;
    private String moveType;
    private final double MAX_DISTANCE = 600;  // 最大飛距離
    private Player owner;  // 発射者を記録
    
    // プロジェクタイルのサイズ
    private final int WIDTH = 40;
    private final int HEIGHT = 40;
    
    public SpecialMove(double x, double y, double velocityX, double velocityY, 
                      Color color, int damage, String moveType, Player owner) {
        this.x = x;
        this.y = y;
        this.startX = x;  // 発射位置を記録
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.color = color;
        this.damage = damage;
        this.moveType = moveType;
        this.owner = owner;  // 発射者を記録
        this.active = true;
        this.maxLifeTime = 120; // 約2秒
        this.lifeTime = maxLifeTime;
    }
    
    public void update() {
        if (!active) return;
        
        // 位置の更新
        x += velocityX;
        y += velocityY;
        
        // 重力は適用しない（横方向の直線攻撃）
        
        // 飛距離チェック
        double distance = Math.abs(x - startX);
        if (distance >= MAX_DISTANCE) {
            active = false;
        }
        
        // ライフタイムの減少
        lifeTime--;
        if (lifeTime <= 0) {
            active = false;
        }
    }
    
    public void draw(Graphics g) {
        if (!active) return;
        
        switch (moveType) {
            case "fireball":
                // 火の玉エフェクト
                g.setColor(Color.RED);
                g.fillOval((int)x, (int)y, WIDTH, HEIGHT);
                g.setColor(Color.ORANGE);
                g.fillOval((int)x + 2, (int)y + 2, WIDTH - 4, HEIGHT - 4);
                g.setColor(Color.YELLOW);
                g.fillOval((int)x + 4, (int)y + 4, WIDTH - 8, HEIGHT - 8);
                break;
                
            case "energy_blast":
                // エネルギー弾エフェクト
                g.setColor(Color.BLUE);
                g.fillOval((int)x, (int)y, WIDTH, HEIGHT);
                g.setColor(Color.CYAN);
                g.fillOval((int)x + 2, (int)y + 2, WIDTH - 4, HEIGHT - 4);
                g.setColor(Color.WHITE);
                g.fillOval((int)x + 6, (int)y + 6, WIDTH - 12, HEIGHT - 12);
                break;
                
            case "shockwave":
                // 衝撃波エフェクト
                int alpha = (int)(255 * (lifeTime / (double)maxLifeTime));
                g.setColor(new Color(255, 255, 0, alpha));
                g.fillOval((int)x - WIDTH/2, (int)y - HEIGHT/2, WIDTH * 2, HEIGHT);
                break;
        }
    }
    
    public boolean checkCollision(Player player) {
        if (!active) return false;
        
        // 発射者自身には当たらない
        if (player == owner) return false;
        
        // プレイヤーの当たり判定範囲を拡大
        double playerLeft = player.getX();
        double playerRight = player.getX() + 160; // プレイヤーの幅
        double playerTop = player.getY();
        double playerBottom = player.getY() + 320; // プレイヤーの高さ
        
        // 弾の当たり判定
        double bulletLeft = x;
        double bulletRight = x + WIDTH;
        double bulletTop = y;
        double bulletBottom = y + HEIGHT;
        
        // 矩形同士の衝突判定
        if (bulletRight >= playerLeft && bulletLeft <= playerRight &&
            bulletBottom >= playerTop && bulletTop <= playerBottom) {
            
            // 無敵時間チェックをここで行う
            if (!player.isInvulnerable()) {
                player.takeDamage(damage);
                // ノックバック効果（歩行中でも必ず発生）
                double knockbackX = (player.getX() > x) ? 20 : -20;
                player.startKnockback(knockbackX);
            }
            active = false;
            return true;
        }
        return false;
    }
    
    public void checkBounds(int windowWidth, int windowHeight) {
        if (x < -WIDTH || x > windowWidth || y > windowHeight) {
            active = false;
        }
    }
    
    // ゲッター
    public boolean isActive() { return active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getDamage() { return damage; }
    public String getMoveType() { return moveType; }
    
    // セッター
    public void setActive(boolean active) { this.active = active; }
}
