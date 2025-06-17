import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialMove {
    private double x, y;
    private double velocityX, velocityY;
    private Color color;
    private int damage;
    private int lifeTime;
    private int maxLifeTime;
    private boolean active;
    private String moveType;
    
    // プロジェクタイルのサイズ
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    
    public SpecialMove(double x, double y, double velocityX, double velocityY, 
                      Color color, int damage, String moveType) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.color = color;
        this.damage = damage;
        this.moveType = moveType;
        this.active = true;
        this.maxLifeTime = 120; // 約2秒
        this.lifeTime = maxLifeTime;
    }
    
    public void update() {
        if (!active) return;
        
        // 位置の更新
        x += velocityX;
        y += velocityY;
        
        // 重力（弧を描く攻撃の場合）
        if (moveType.equals("fireball")) {
            velocityY += 0.3;
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
        
        double playerCenterX = player.getX() + 20; // プレイヤーの中心
        double playerCenterY = player.getY() + 40;
        double moveCenterX = x + WIDTH / 2;
        double moveCenterY = y + HEIGHT / 2;
        
        double distance = Math.sqrt(Math.pow(playerCenterX - moveCenterX, 2) + 
                                   Math.pow(playerCenterY - moveCenterY, 2));
        
        if (distance <= WIDTH) {
            player.takeDamage(damage);
            // ノックバック効果
            double knockbackX = (playerCenterX > moveCenterX) ? 10 : -10;
            player.setVelocityX(player.getVelocityX() + knockbackX);
            player.setVelocityY(player.getVelocityY() - 8);
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
