import java.awt.*;

public class Player {
    private double x, y;
    private double velocityX, velocityY;
    private final double GRAVITY = 0.8;
    private final double JUMP_STRENGTH = -15;    private final double MOVE_SPEED = 5;
    
    private String name;
    private double health;
    private boolean onGround;
    private boolean isAttacking;
    private int attackCooldown;
    private final int ATTACK_COOLDOWN_MAX = 30;
    
    // キャラクターの状態とアニメーション
    private boolean facingRight;
    private int animationFrame;
    private int damageFlash;
    private boolean isMoving;
    
    // キャラクターの色バリエーション
    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    
    // プレイヤーのサイズ
    private final int WIDTH = 40;
    private final int HEIGHT = 80;
    
    // 攻撃範囲
    private final int ATTACK_RANGE = 60;
    private final int ATTACK_DAMAGE = 10;    public Player(double x, double y, Color color, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.health = 100;
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        
        // アニメーション・状態の初期化
        this.facingRight = true;
        this.animationFrame = 0;
        this.damageFlash = 0;
        this.isMoving = false;
        
        // 色バリエーションの設定
        this.primaryColor = color;
        this.secondaryColor = color.darker();
        this.accentColor = color.brighter();
    }
      public void update() {
        // 重力の適用
        velocityY += GRAVITY;
        
        // 位置の更新
        x += velocityX;
        y += velocityY;
        
        // 移動状態の更新
        isMoving = Math.abs(velocityX) > 0.5;
        
        // 向きの更新
        if (velocityX > 0.5) {
            facingRight = true;
        } else if (velocityX < -0.5) {
            facingRight = false;
        }
        
        // アニメーションフレームの更新
        animationFrame++;
        if (animationFrame > 60) animationFrame = 0;
        
        // ダメージフラッシュの更新
        if (damageFlash > 0) {
            damageFlash--;
        }
        
        // 水平方向の摩擦
        velocityX *= 0.8;
        
        // 攻撃クールダウンの更新
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (attackCooldown == 0) {
            isAttacking = false;
        }
    }
    
    public void moveLeft() {
        velocityX = -MOVE_SPEED;
    }
    
    public void moveRight() {
        velocityX = MOVE_SPEED;
    }
    
    public void jump() {
        if (onGround) {
            velocityY = JUMP_STRENGTH;
            onGround = false;
        }
    }
    
    public void attack(Player target) {
        if (attackCooldown == 0) {
            isAttacking = true;
            attackCooldown = ATTACK_COOLDOWN_MAX;
            
            // 攻撃範囲内にターゲットがいるかチェック
            double distance = Math.abs(this.x - target.x);
            if (distance <= ATTACK_RANGE && Math.abs(this.y - target.y) <= HEIGHT) {
                target.takeDamage(ATTACK_DAMAGE);
                
                // ノックバック効果
                double knockbackDirection = (target.x > this.x) ? 1 : -1;
                target.velocityX += knockbackDirection * 8;
                target.velocityY -= 5;
            }
        }
    }
      public void takeDamage(double damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
        // ダメージフラッシュエフェクト
        damageFlash = 20;
    }
    
    public void checkBounds(int windowWidth, int groundLevel) {
        // 左右の境界チェック
        if (x < 0) {
            x = 0;
            velocityX = 0;
        }
        if (x + WIDTH > windowWidth) {
            x = windowWidth - WIDTH;
            velocityX = 0;
        }
        
        // 地面との衝突チェック
        if (y + HEIGHT >= groundLevel) {
            y = groundLevel - HEIGHT;
            velocityY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
    }
      public void draw(Graphics g) {
        // ダメージフラッシュ時の色調整
        Color currentPrimary = damageFlash > 0 ? Color.RED : primaryColor;
        Color currentSecondary = damageFlash > 0 ? Color.PINK : secondaryColor;
        
        drawCharacter(g, currentPrimary, currentSecondary);
        drawAttackEffect(g);
        drawHealthBar(g);
    }
    
    private void drawCharacter(Graphics g, Color primary, Color secondary) {
        // 頭の描画
        drawHead(g, primary, secondary);
        
        // 胴体の描画
        drawBody(g, primary, secondary);
        
        // 手足の描画
        drawLimbs(g, primary, secondary);
    }
    
    private void drawHead(Graphics g, Color primary, Color secondary) {
        int headSize = 24;
        int headX = (int)x + WIDTH/2 - headSize/2;
        int headY = (int)y + 5;
        
        // 頭の輪郭
        g.setColor(primary);
        g.fillOval(headX, headY, headSize, headSize);
        g.setColor(Color.BLACK);
        g.drawOval(headX, headY, headSize, headSize);
        
        // 目の描画
        int eyeSize = 4;
        int eyeY = headY + 8;
        if (facingRight) {
            // 右向きの目
            g.setColor(Color.WHITE);
            g.fillOval(headX + 6, eyeY, eyeSize, eyeSize);
            g.fillOval(headX + 14, eyeY, eyeSize, eyeSize);
            g.setColor(Color.BLACK);
            g.fillOval(headX + 7, eyeY + 1, 2, 2);
            g.fillOval(headX + 15, eyeY + 1, 2, 2);
        } else {
            // 左向きの目
            g.setColor(Color.WHITE);
            g.fillOval(headX + 6, eyeY, eyeSize, eyeSize);
            g.fillOval(headX + 14, eyeY, eyeSize, eyeSize);
            g.setColor(Color.BLACK);
            g.fillOval(headX + 6, eyeY + 1, 2, 2);
            g.fillOval(headX + 14, eyeY + 1, 2, 2);
        }
        
        // 口の描画
        g.setColor(Color.BLACK);
        if (isAttacking) {
            // 攻撃時は開いた口
            g.fillOval(headX + 10, headY + 16, 4, 6);
        } else {
            // 通常時は笑顔
            g.drawArc(headX + 8, headY + 14, 8, 6, 0, -180);
        }
    }
    
    private void drawBody(Graphics g, Color primary, Color secondary) {
        int bodyWidth = 20;
        int bodyHeight = 30;
        int bodyX = (int)x + WIDTH/2 - bodyWidth/2;
        int bodyY = (int)y + 25;
        
        // 胴体
        g.setColor(primary);
        g.fillRect(bodyX, bodyY, bodyWidth, bodyHeight);
        g.setColor(secondary);
        g.drawRect(bodyX, bodyY, bodyWidth, bodyHeight);
        
        // 胸部の装飾
        g.setColor(accentColor);
        g.fillRect(bodyX + 2, bodyY + 5, bodyWidth - 4, 3);
        g.fillRect(bodyX + 4, bodyY + 10, bodyWidth - 8, 2);
    }
    
    private void drawLimbs(Graphics g, Color primary, Color secondary) {
        int armWidth = 8;
        int armHeight = 20;
        int legWidth = 10;
        int legHeight = 25;
        
        // アニメーション用の角度計算
        double walkCycle = Math.sin(animationFrame * 0.3) * (isMoving ? 1 : 0);
        double jumpOffset = !onGround ? -5 : 0;
        
        // 腕の描画
        int armY = (int)y + 30 + (int)jumpOffset;
        int leftArmX = (int)x + 5;
        int rightArmX = (int)x + WIDTH - 13;
        
        // 左腕
        g.setColor(primary);
        if (isAttacking && !facingRight) {
            // 攻撃時のポーズ
            g.fillRect(leftArmX - 5, armY - 5, armWidth + 5, armHeight);
        } else {
            g.fillRect(leftArmX, armY + (int)(walkCycle * 3), armWidth, armHeight);
        }
        
        // 右腕
        if (isAttacking && facingRight) {
            // 攻撃時のポーズ
            g.fillRect(rightArmX, armY - 5, armWidth + 5, armHeight);
        } else {
            g.fillRect(rightArmX, armY - (int)(walkCycle * 3), armWidth, armHeight);
        }
        
        // 脚の描画
        int legY = (int)y + 50 + (int)jumpOffset;
        int leftLegX = (int)x + 8;
        int rightLegX = (int)x + WIDTH - 18;
        
        g.setColor(secondary);
        // 左脚
        g.fillRect(leftLegX, legY + (int)(walkCycle * 5), legWidth, legHeight);
        // 右脚
        g.fillRect(rightLegX, legY - (int)(walkCycle * 5), legWidth, legHeight);
        
        // 足の描画
        g.setColor(Color.BLACK);
        g.fillRect(leftLegX - 2, legY + legHeight + (int)(walkCycle * 5), legWidth + 4, 4);
        g.fillRect(rightLegX - 2, legY + legHeight - (int)(walkCycle * 5), legWidth + 4, 4);
    }
    
    private void drawAttackEffect(Graphics g) {
        if (isAttacking) {
            // 攻撃エフェクトの描画
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            
            int effectX = facingRight ? (int)x + WIDTH : (int)x - ATTACK_RANGE;
            int effectY = (int)y + HEIGHT/4;
            
            // 光る効果
            g.setColor(new Color(255, 255, 0, 150));
            g.fillOval(effectX, effectY, ATTACK_RANGE, HEIGHT/2);
            
            // 稲妻のような線
            g.setColor(Color.YELLOW);
            for (int i = 0; i < 5; i++) {
                int startX = effectX + (int)(Math.random() * ATTACK_RANGE);
                int startY = effectY + (int)(Math.random() * HEIGHT/2);
                int endX = startX + (int)(Math.random() * 20 - 10);
                int endY = startY + (int)(Math.random() * 20 - 10);
                g.drawLine(startX, startY, endX, endY);
            }
            
            g2d.setStroke(new BasicStroke(1));
        }
    }
    
    private void drawHealthBar(Graphics g) {
        // 名前とヘルス表示
        g.setColor(Color.WHITE);
        g.drawString(name, (int)x, (int)y - 15);
        
        // ヘルスバーの背景
        int barWidth = WIDTH;
        int barHeight = 6;
        int barX = (int)x;
        int barY = (int)y - 8;
        
        g.setColor(Color.BLACK);
        g.fillRect(barX, barY, barWidth, barHeight);
        
        // ヘルスバー
        double healthRatio = health / 100.0;
        Color healthColor;
        if (healthRatio > 0.6) {
            healthColor = Color.GREEN;
        } else if (healthRatio > 0.3) {
            healthColor = Color.YELLOW;
        } else {
            healthColor = Color.RED;
        }
        
        g.setColor(healthColor);
        g.fillRect(barX + 1, barY + 1, (int)((barWidth - 2) * healthRatio), barHeight - 2);
        
        // ヘルスバーの枠
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
    }
      // ゲッター
    public double getX() { return x; }
    public double getY() { return y; }
    public double getHealth() { return health; }
    public String getName() { return name; }
    public boolean isOnGround() { return onGround; }
    public boolean isAttacking() { return isAttacking; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    
    // セッター
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setHealth(double health) { this.health = health; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }
}
