package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FastEnemy extends GameObject {
    private Handler handler;
    private BufferedImage enemyImage;
    private int trailCounter = 0;
    private int health = 5;
    private int maxHealth = 5;
    private int dashTimer = 0;
    private int dashCooldown = 180; // 3秒冲刺间隔
    private boolean isDashing = false;
    private int dashDuration = 30; // 0.5秒冲刺时间
    
    private final int ENEMY_WIDTH = 24;
    private final int ENEMY_HEIGHT = 24;
    private final float NORMAL_SPEED = 3.0f;
    private final float DASH_SPEED = 8.0f;
    
    public FastEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        enemyImage = ImageLoader.getEnemyImage();
        
        // 随机初始移动方向
        double angle = Math.random() * 2 * Math.PI;
        velX = (int) (Math.cos(angle) * NORMAL_SPEED);
        velY = (int) (Math.sin(angle) * NORMAL_SPEED);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
    }
    
    public void tick() {
        // 冲刺逻辑
        dashTimer++;
        if (dashTimer >= dashCooldown && !isDashing) {
            // 开始冲刺
            isDashing = true;
            dashTimer = 0;
            
            // 冲刺方向随机或朝向玩家
            Player target = findPlayer();
            if (target != null && Math.random() < 0.7) {
                // 70%概率冲向玩家
                float diffX = target.getX() - x;
                float diffY = target.getY() - y;
                float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);
                
                if (distance > 0) {
                    velX = (int) ((diffX / distance) * DASH_SPEED);
                    velY = (int) ((diffY / distance) * DASH_SPEED);
                }
            } else {
                // 30%概率随机冲刺
                double angle = Math.random() * 2 * Math.PI;
                velX = (int) (Math.cos(angle) * DASH_SPEED);
                velY = (int) (Math.sin(angle) * DASH_SPEED);
            }
        }
        
        if (isDashing && dashTimer >= dashDuration) {
            // 结束冲刺
            isDashing = false;
            dashTimer = 0;
            
            // 恢复正常速度
            double angle = Math.random() * 2 * Math.PI;
            velX = (int) (Math.cos(angle) * NORMAL_SPEED);
            velY = (int) (Math.sin(angle) * NORMAL_SPEED);
        }
        
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
        
        // 轨迹效果
        trailCounter++;
        if (trailCounter >= (isDashing ? 2 : 8)) {
            Color trailColor = isDashing ? 
                new Color(255, 255, 0, 80) : // 冲刺时黄色轨迹
                new Color(0, 255, 255, 50);  // 正常时青色轨迹
            handler.addObject(new Trail(x, y, ID.Trail, trailColor, ENEMY_WIDTH, ENEMY_HEIGHT, 0.05f, handler));
            trailCounter = 0;
        }
    }
    
    private Player findPlayer() {
        for (GameObject obj : handler.objects) {
            if (obj.getId() == ID.Player) {
                return (Player) obj;
            }
        }
        return null;
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (enemyImage != null) {
            g.drawImage(enemyImage, x, y, ENEMY_WIDTH, ENEMY_HEIGHT, null);
        } else {
            // 根据状态改变颜色
            if (isDashing) {
                g.setColor(new Color(255, 255, 0)); // 冲刺时黄色
            } else {
                g.setColor(new Color(0, 255, 255)); // 正常时青色
            }
            g.fillRect(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
        
        // 绘制血量条
        if (health < maxHealth) {
            g.setColor(Color.RED);
            g.fillRect(x, y - 8, ENEMY_WIDTH, 4);
            g.setColor(Color.GREEN);
            g.fillRect(x, y - 8, (int) ((float) health / maxHealth * ENEMY_WIDTH), 4);
        }
        
        // 冲刺预警效果
        if (dashTimer > dashCooldown - 30 && !isDashing) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillOval(x - 5, y - 5, ENEMY_WIDTH + 10, ENEMY_HEIGHT + 10);
        }
        
        // 冲刺时的光环效果
        if (isDashing) {
            g.setColor(new Color(255, 255, 0, 150));
            g.drawOval(x - 8, y - 8, ENEMY_WIDTH + 16, ENEMY_HEIGHT + 16);
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            createDeathEffect();
            handler.removeObject(this);
            HUD.score += 15; // 快速敌人给15分
            HUD.enemiesKilled++;
        }
    }
    
    private void createDeathEffect() {
        for (int i = 0; i < 10; i++) {
            int velX = (int) (Math.random() * 8 - 4);
            int velY = (int) (Math.random() * 8 - 4);
            Color particleColor = new Color(0, 255, 255);
            handler.addObject(new Particle(x + ENEMY_WIDTH/2, y + ENEMY_HEIGHT/2, ID.Particle, velX, velY, particleColor, 25, handler));
        }
    }
    
    public int getHealth() {
        return health;
    }
}
