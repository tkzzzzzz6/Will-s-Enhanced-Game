package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ShooterEnemy extends GameObject {
    private Handler handler;
    private BufferedImage enemyImage;
    private int trailCounter = 0;
    private int shootTimer = 0;
    private int shootCooldown = 120; // 2秒射击间隔
    private Player target;
    private int health = 15;
    private int maxHealth = 15;
    
    private final int ENEMY_WIDTH = 32;
    private final int ENEMY_HEIGHT = 32;
    
    public ShooterEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        enemyImage = ImageLoader.getEnemyImage();
        findPlayer();
        
        // 随机初始移动方向
        velX = (int) (Math.random() * 4 - 2);
        velY = (int) (Math.random() * 4 - 2);
    }
    
    private void findPlayer() {
        for (GameObject obj : handler.objects) {
            if (obj.getId() == ID.Player) {
                target = (Player) obj;
                break;
            }
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
    }
    
    public void tick() {
        if (target == null) {
            findPlayer();
        }
        
        // 巡逻移动
        x += velX;
        y += velY;
        
        // 边界反弹
        if (x <= 0 || x >= Game.WIDTH - ENEMY_WIDTH) {
            velX *= -1;
            x = Game.clamp(x, 0, Game.WIDTH - ENEMY_WIDTH);
        }
        if (y <= 0 || y >= Game.HEIGHT - ENEMY_HEIGHT - 30) {
            velY *= -1;
            y = Game.clamp(y, 0, Game.HEIGHT - ENEMY_HEIGHT - 30);
        }
        
        // 射击逻辑
        shootTimer++;
        if (shootTimer >= shootCooldown && target != null) {
            shoot();
            shootTimer = 0;
        }
        
        // 轨迹效果
        trailCounter++;
        if (trailCounter >= 8) {
            Color trailColor = new Color(255, 150, 0, 50); // 橙色轨迹
            handler.addObject(new Trail(x, y, ID.Trail, trailColor, ENEMY_WIDTH, ENEMY_HEIGHT, 0.03f, handler));
            trailCounter = 0;
        }
    }
    
    private void shoot() {
        if (target == null) return;
        
        // 计算射击方向
        float diffX = target.getX() - (x + ENEMY_WIDTH/2);
        float diffY = target.getY() - (y + ENEMY_HEIGHT/2);
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);
        
        if (distance > 0 && distance < 300) { // 射击范围限制
            // 标准化方向向量
            float dirX = diffX / distance;
            float dirY = diffY / distance;
            
            // 创建子弹
            int bulletSpeed = 4;
            int bulletVelX = (int) (dirX * bulletSpeed);
            int bulletVelY = (int) (dirY * bulletSpeed);
            
            handler.addObject(new Bullet(x + ENEMY_WIDTH/2 - 4, y + ENEMY_HEIGHT/2 - 4, 
                                       ID.EnemyBullet, bulletVelX, bulletVelY, handler));
            
            // 播放射击音效
            AudioPlayer.play("enemyShoot");
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (enemyImage != null) {
            g.drawImage(enemyImage, x, y, ENEMY_WIDTH, ENEMY_HEIGHT, null);
        } else {
            g.setColor(new Color(255, 150, 0));
            g.fillRect(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
        
        // 绘制血量条
        if (health < maxHealth) {
            g.setColor(Color.RED);
            g.fillRect(x, y - 8, ENEMY_WIDTH, 4);
            g.setColor(Color.GREEN);
            g.fillRect(x, y - 8, (int) ((float) health / maxHealth * ENEMY_WIDTH), 4);
        }
        
        // 绘制射击准备指示器
        if (shootTimer > shootCooldown - 30) {
            g.setColor(new Color(255, 0, 0, 150));
            g.fillOval(x + ENEMY_WIDTH/2 - 3, y + ENEMY_HEIGHT/2 - 3, 6, 6);
        }
        
        // 绘制射击范围指示（当玩家在范围内时）
        if (target != null) {
            float distance = (float) Math.sqrt(Math.pow(target.getX() - x, 2) + Math.pow(target.getY() - y, 2));
            if (distance < 300) {
                g.setColor(new Color(255, 0, 0, 30));
                g.drawOval(x - 150 + ENEMY_WIDTH/2, y - 150 + ENEMY_HEIGHT/2, 300, 300);
            }
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            createDeathEffect();
            handler.removeObject(this);
            HUD.score += 20;
        }
    }
    
    private void createDeathEffect() {
        for (int i = 0; i < 12; i++) {
            int velX = (int) (Math.random() * 10 - 5);
            int velY = (int) (Math.random() * 10 - 5);
            Color particleColor = new Color(255, 150, 0);
            handler.addObject(new Particle(x + ENEMY_WIDTH/2, y + ENEMY_HEIGHT/2, ID.Particle, velX, velY, particleColor, 35, handler));
        }
    }
    
    public int getHealth() {
        return health;
    }
}
