import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Player {
    private double x, y;
    private double velocityX, velocityY;
    private final double GRAVITY = 1.4;
    private final double JUMP_STRENGTH = -30;    private final double MOVE_SPEED = 5;
    
    private String name;
    private double health;
    private boolean onGround;
    private boolean isAttacking;
    private int attackCooldown;
    private final int ATTACK_COOLDOWN_MAX = 25;
    
    // 特殊攻撃の硬直
    private boolean isSpecialAttacking;
    private int specialAttackStun;
    private final int SPECIAL_ATTACK_STUN_MAX = 36; // 0.6秒間（36フレーム）
    
    // キャラクターの状態とアニメーション
    private boolean facingRight;
    private int animationFrame;
    private int walkAnimationFrame;  // 歩行専用のアニメーションフレーム
    private boolean isMoving;
    private boolean wasMovingRight;  // 前フレームで右移動していたかを記録
    
    // 無敵時間システム
    private boolean isInvulnerable;
    private int invulnerabilityTime;
    private final int INVULNERABILITY_MAX = 18; // 0.3秒間（18フレーム）
    
    // ノックバック状態
    private boolean isKnockedBack;
    private int knockbackTime;
    private final int KNOCKBACK_MAX = 20; // 約0.33秒間（20フレーム）
    
    // キャラクターの色バリエーション
    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    
    // プレイヤーのサイズ
    private final int WIDTH = 160;
    private final int HEIGHT = 320;
    
    // 攻撃範囲
    private final int ATTACK_RANGE = 240;
    private final int ATTACK_DAMAGE = 10;
    
    // プレイヤー画像
    private BufferedImage playerImage;
    private BufferedImage walkImage1;
    private BufferedImage walkImage2;
    private BufferedImage walkImage3;
    private BufferedImage walkImage4;
    private BufferedImage walkImage5;
    private BufferedImage jumpImage;
    private BufferedImage fireImage;
    private BufferedImage damagedImage;
    private BufferedImage attackImage1;
    private BufferedImage attackImage2;
    private BufferedImage attackImage3;
    private BufferedImage attackImage4;
    private BufferedImage attackImage5;
    private boolean useImage;    public Player(double x, double y, Color color, String name) {
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
        this.walkAnimationFrame = 0;
        this.isMoving = false;
        this.wasMovingRight = false;
        
        // 特殊攻撃硬直の初期化
        this.isSpecialAttacking = false;
        this.specialAttackStun = 0;
        
        // 無敵時間の初期化
        this.isInvulnerable = false;
        this.invulnerabilityTime = 0;
        
        // ノックバック状態の初期化
        this.isKnockedBack = false;
        this.knockbackTime = 0;
        
        // 色バリエーションの設定
        this.primaryColor = color;
        this.secondaryColor = color.darker();
        this.accentColor = color.brighter();
        
        // プレイヤー1の場合は画像を読み込み
        if (name.equals("Player 1")) {
            loadPlayerImage();
        } else {
            useImage = false;
        }
    }
      public void update() {
        // 重力の適用
        velocityY += GRAVITY;
        
        // 位置の更新
        x += velocityX;
        y += velocityY;
        
        // 移動状態の更新
        isMoving = Math.abs(velocityX) > 0.5;
        
        // 向きの更新（移動時のみ）
        if (velocityX > 0.5) {
            facingRight = true;
        } else if (velocityX < -0.5) {
            facingRight = false;
        }
        
        // アニメーションフレームの更新
        animationFrame++;
        if (animationFrame > 60) animationFrame = 0;
        
        // 水平方向の摩擦
        velocityX *= 0.8;
        
        // 攻撃クールダウンの更新
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (attackCooldown == 0) {
            isAttacking = false;
        }
        
        // 特殊攻撃硬直の更新
        if (specialAttackStun > 0) {
            specialAttackStun--;
        }
        if (specialAttackStun == 0) {
            isSpecialAttacking = false;
        }
        
        // 無敵時間の更新
        if (invulnerabilityTime > 0) {
            invulnerabilityTime--;
        }
        if (invulnerabilityTime == 0) {
            isInvulnerable = false;
        }
        
        // ノックバック状態の更新
        if (knockbackTime > 0) {
            knockbackTime--;
        }
        if (knockbackTime == 0) {
            isKnockedBack = false;
        }
    }
    
    public void moveLeft() {
        if (!isSpecialAttacking && !isKnockedBack && !isInvulnerable && !isAttacking) {
            velocityX = -MOVE_SPEED;
        }
    }
    
    public void moveRight() {
        if (!isSpecialAttacking && !isKnockedBack && !isInvulnerable && !isAttacking) {
            velocityX = MOVE_SPEED;
        }
    }
    
    public void jump() {
        if (onGround && !isSpecialAttacking && !isKnockedBack && !isInvulnerable && !isAttacking) {
            velocityY = JUMP_STRENGTH;
            onGround = false;
        }
    }
    
    public void attack(Player target) {
        if (attackCooldown == 0 && !isSpecialAttacking && !isKnockedBack && !isInvulnerable && onGround) {
            isAttacking = true;
            attackCooldown = ATTACK_COOLDOWN_MAX;
            
            // 攻撃範囲内にターゲットがいるかチェック
            double distance = Math.abs(this.x - target.x);
            if (distance <= ATTACK_RANGE && Math.abs(this.y - target.y) <= HEIGHT/2) {
                if (!target.isInvulnerable) {
                    target.takeDamage(ATTACK_DAMAGE);
                    
                    // ノックバック効果（攻撃者から見て後ろ方向）
                    double knockbackDirection = (target.x > this.x) ? 1 : -1;
                    target.startKnockback(knockbackDirection * 15);
                }
            }
        }
    }
    
    public boolean useSpecialAttack() {
        if (specialAttackStun == 0 && onGround && !isKnockedBack && !isInvulnerable && !isAttacking) {
            isSpecialAttacking = true;
            specialAttackStun = SPECIAL_ATTACK_STUN_MAX;
            return true;
        }
        return false;
    }
      public void takeDamage(double damage) {
        if (!isInvulnerable) {
            health -= damage;
            if (health < 0) {
                health = 0;
            }
            
            // 無敵時間開始
            isInvulnerable = true;
            invulnerabilityTime = INVULNERABILITY_MAX;
        }
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
        if (useImage && playerImage != null) {
            // 画像を使用して描画
            drawImageCharacter(g);
        } else {
            drawCharacter(g, primaryColor, secondaryColor);
        }
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
        int headSize = 96;
        int headX = (int)x + WIDTH/2 - headSize/2;
        int headY = (int)y + 20;
        
        // 頭の輪郭
        g.setColor(primary);
        g.fillOval(headX, headY, headSize, headSize);
        g.setColor(Color.BLACK);
        g.drawOval(headX, headY, headSize, headSize);
        
        // 目の描画
        int eyeSize = 16;
        int eyeY = headY + 32;
        if (facingRight) {
            // 右向きの目
            g.setColor(Color.WHITE);
            g.fillOval(headX + 24, eyeY, eyeSize, eyeSize);
            g.fillOval(headX + 56, eyeY, eyeSize, eyeSize);
            g.setColor(Color.BLACK);
            g.fillOval(headX + 28, eyeY + 4, 8, 8);
            g.fillOval(headX + 60, eyeY + 4, 8, 8);
        } else {
            // 左向きの目
            g.setColor(Color.WHITE);
            g.fillOval(headX + 24, eyeY, eyeSize, eyeSize);
            g.fillOval(headX + 56, eyeY, eyeSize, eyeSize);
            g.setColor(Color.BLACK);
            g.fillOval(headX + 24, eyeY + 4, 8, 8);
            g.fillOval(headX + 56, eyeY + 4, 8, 8);
        }
        
        // 口の描画
        g.setColor(Color.BLACK);
        if (isAttacking) {
            // 攻撃時は開いた口
            g.fillOval(headX + 40, headY + 64, 16, 24);
        } else {
            // 通常時は笑顔
            g.drawArc(headX + 32, headY + 56, 32, 24, 0, -180);
        }
    }
    
    private void drawBody(Graphics g, Color primary, Color secondary) {
        int bodyWidth = 80;
        int bodyHeight = 120;
        int bodyX = (int)x + WIDTH/2 - bodyWidth/2;
        int bodyY = (int)y + 100;
        
        // 胴体
        g.setColor(primary);
        g.fillRect(bodyX, bodyY, bodyWidth, bodyHeight);
        g.setColor(secondary);
        g.drawRect(bodyX, bodyY, bodyWidth, bodyHeight);
        
        // 胸部の装飾
        g.setColor(accentColor);
        g.fillRect(bodyX + 8, bodyY + 20, bodyWidth - 16, 12);
        g.fillRect(bodyX + 16, bodyY + 40, bodyWidth - 32, 8);
    }
    
    private void drawLimbs(Graphics g, Color primary, Color secondary) {
        int armWidth = 32;
        int armHeight = 80;
        int legWidth = 40;
        int legHeight = 100;
        
        // アニメーション用の角度計算
        double walkCycle = Math.sin(animationFrame * 0.3) * (isMoving ? 1 : 0);
        double jumpOffset = !onGround ? -5 : 0;
        
        // 腕の描画
        int armY = (int)y + 120 + (int)jumpOffset;
        int leftArmX = (int)x + 20;
        int rightArmX = (int)x + WIDTH - 52;
        
        // 左腕
        g.setColor(primary);
        if (isAttacking && !facingRight) {
            // 攻撃時のポーズ
            g.fillRect(leftArmX - 20, armY - 20, armWidth + 20, armHeight);
        } else {
            g.fillRect(leftArmX, armY + (int)(walkCycle * 12), armWidth, armHeight);
        }
        
        // 右腕
        if (isAttacking && facingRight) {
            // 攻撃時のポーズ
            g.fillRect(rightArmX, armY - 20, armWidth + 20, armHeight);
        } else {
            g.fillRect(rightArmX, armY - (int)(walkCycle * 12), armWidth, armHeight);
        }
        
        // 脚の描画
        int legY = (int)y + 200 + (int)jumpOffset;
        int leftLegX = (int)x + 32;
        int rightLegX = (int)x + WIDTH - 72;
        
        g.setColor(secondary);
        // 左脚
        g.fillRect(leftLegX, legY + (int)(walkCycle * 20), legWidth, legHeight);
        // 右脚
        g.fillRect(rightLegX, legY - (int)(walkCycle * 20), legWidth, legHeight);
        
        // 足の描画
        g.setColor(Color.BLACK);
        g.fillRect(leftLegX - 8, legY + legHeight + (int)(walkCycle * 20), legWidth + 16, 16);
        g.fillRect(rightLegX - 8, legY + legHeight - (int)(walkCycle * 20), legWidth + 16, 16);
    }
    
    private void drawAttackEffect(Graphics g) {
        // 攻撃エフェクトは削除済み
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
    public boolean isSpecialAttacking() { return isSpecialAttacking; }
    public boolean isInvulnerable() { return isInvulnerable; }
    public boolean isKnockedBack() { return isKnockedBack; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    
    // セッター
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setHealth(double health) { this.health = health; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }
    
    // 相手の方を向くメソッド
    public void faceOpponent(Player opponent) {
        if (opponent.getX() > this.x) {
            facingRight = true;
        } else {
            facingRight = false;
        }
    }
    
    // 相手に対して後ろに下がっているかを判定するメソッド（facingRightと移動方向で判定）
    public boolean isMovingBackward() {
        if (facingRight) {
            // 右向きの場合、左に移動していれば後ろ歩き
            return velocityX < -0.5;
        } else {
            // 左向きの場合、右に移動していれば後ろ歩き
            return velocityX > 0.5;
        }
    }
    
    // 水平移動を停止するメソッド（ノックバック中は停止しない）
    public void stopHorizontalMovement() {
        // ノックバック中は停止しない
        if (!isKnockedBack) {
            velocityX = 0;
        }
    }
    
    // ノックバックを開始するメソッド
    public void startKnockback(double knockbackVelocity) {
        velocityX = knockbackVelocity;
        isKnockedBack = true;
        knockbackTime = KNOCKBACK_MAX;
    }
    
    // 画像読み込みメソッド
    private void loadPlayerImage() {
        try {
            playerImage = ImageIO.read(new File("image/player_1.jpg"));
            walkImage1 = ImageIO.read(new File("image/walk_1_1.jpg"));
            walkImage2 = ImageIO.read(new File("image/walk_1_2.jpg"));
            walkImage3 = ImageIO.read(new File("image/walk_1_3.jpg"));
            walkImage4 = ImageIO.read(new File("image/walk_1_4.jpg"));
            walkImage5 = ImageIO.read(new File("image/walk_1_5.jpg"));
            jumpImage = ImageIO.read(new File("image/jump_1_1.jpg"));
            fireImage = ImageIO.read(new File("image/fire_1.jpg"));
            damagedImage = ImageIO.read(new File("image/dagaged_1.jpg"));
            attackImage1 = ImageIO.read(new File("image/attack_1_1.jpg"));
            attackImage2 = ImageIO.read(new File("image/attack_1_2.jpg"));
            attackImage3 = ImageIO.read(new File("image/attack_1_3.jpg"));
            attackImage4 = ImageIO.read(new File("image/attack_1_4.jpg"));
            attackImage5 = ImageIO.read(new File("image/attack_1_5.jpg"));
            useImage = true;
            System.out.println("プレイヤー1の画像を正常に読み込みました");
        } catch (IOException e) {
            System.out.println("プレイヤー1の画像読み込みに失敗しました: " + e.getMessage());
            useImage = false;
        }
    }
    
    // 画像を使った描画メソッド
    private void drawImageCharacter(Graphics g) {
        if (playerImage == null) return;
        
        // 状態に応じて画像を選択
        BufferedImage currentImage;
        if (isInvulnerable && damagedImage != null) {
            // 無敵時間中（ダメージを受けた直後）はダメージ画像を使用
            currentImage = damagedImage;
        } else if (isAttacking && attackImage1 != null && attackImage2 != null && 
            attackImage3 != null && attackImage4 != null && attackImage5 != null) {
            // 攻撃中は攻撃画像を5フレームずつ切り替え（合計25フレーム）
            int attackFrame = (ATTACK_COOLDOWN_MAX - attackCooldown) / 5;
            switch (attackFrame) {
                case 0:
                    currentImage = attackImage1;
                    break;
                case 1:
                    currentImage = attackImage2;
                    break;
                case 2:
                    currentImage = attackImage3;
                    break;
                case 3:
                    currentImage = attackImage4;
                    break;
                case 4:
                    currentImage = attackImage5;
                    break;
                default:
                    currentImage = attackImage5;
                    break;
            }
        } else if (isSpecialAttacking && fireImage != null) {
            // 特殊攻撃中は火の画像を使用
            currentImage = fireImage;
        } else if (!onGround && jumpImage != null) {
            // ジャンプ中（空中）はジャンプ画像を使用
            currentImage = jumpImage;
        } else if (isMoving && onGround && !isKnockedBack && walkImage1 != null && walkImage2 != null && 
            walkImage3 != null && walkImage4 != null && walkImage5 != null) {
            
            if (isMovingBackward()) {
                // 後ろ歩き：10フレームごとに画像を切り替え（5枚を逆順に）
                int backwardFrame = (animationFrame / 10) % 5;
                switch (backwardFrame) {
                    case 0:
                        currentImage = walkImage5;
                        break;
                    case 1:
                        currentImage = walkImage4;
                        break;
                    case 2:
                        currentImage = walkImage3;
                        break;
                    case 3:
                        currentImage = walkImage2;
                        break;
                    case 4:
                        currentImage = walkImage1;
                        break;
                    default:
                        currentImage = walkImage5;
                        break;
                }
            } else {
                // 前進歩き：5フレームごとに画像を切り替え（5枚を順番に）
                int walkFrame = (animationFrame / 5) % 5;
                switch (walkFrame) {
                    case 0:
                        currentImage = walkImage1;
                        break;
                    case 1:
                        currentImage = walkImage2;
                        break;
                    case 2:
                        currentImage = walkImage3;
                        break;
                    case 3:
                        currentImage = walkImage4;
                        break;
                    case 4:
                        currentImage = walkImage5;
                        break;
                    default:
                        currentImage = walkImage1;
                        break;
                }
            }
        } else {
            currentImage = playerImage;
        }
        
        // 縦横比を維持して描画サイズを計算（縦を判定ボックスに合わせる）
        int imageWidth = currentImage.getWidth();
        int imageHeight = currentImage.getHeight();
        double aspectRatio = (double) imageWidth / imageHeight;
        
        // 高さを判定ボックスに合わせ、幅は縦横比を維持
        int drawHeight = HEIGHT;
        int drawWidth = (int) (HEIGHT * aspectRatio);
        
        // 横方向の中央配置のためのオフセット計算（縦は判定ボックスに合わせる）
        int offsetX = (WIDTH - drawWidth) / 2;
        int offsetY = 0;
        
        // 左向きの場合は画像を水平反転して描画
        if (!facingRight) {
            Graphics2D g2d = (Graphics2D) g;
            // 透過処理を有効にする
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g2d.drawImage(currentImage, 
                (int)x + offsetX + drawWidth, (int)y + offsetY, 
                -drawWidth, drawHeight, null);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            // 透過処理を有効にする
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g2d.drawImage(currentImage, (int)x + offsetX, (int)y + offsetY, drawWidth, drawHeight, null);
        }
    }
}
