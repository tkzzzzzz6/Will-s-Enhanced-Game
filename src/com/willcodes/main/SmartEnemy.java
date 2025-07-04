package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SmartEnemy extends GameObject {
    private Handler handler;
    private BufferedImage enemyImage;
    private int trailCounter = 0;
    private Player target;
    private int health = 20;
    private int maxHealth = 20;
    
    private final int ENEMY_WIDTH = 36;
    private final int ENEMY_HEIGHT = 36;
    private final float SPEED = 2.5f;
    
    public SmartEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        enemyImage = ImageLoader.getEnemyImage();
        findPlayer();
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
        
        if (target != null) {
            // 智能追踪玩家
            float diffX = target.getX() - x;
            float diffY = target.getY() - y;
            float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            
            if (distance > 0) {
                // 标准化方向向量并应用速度
                velX = (int) ((diffX / distance) * SPEED);
                velY = (int) ((diffY / distance) * SPEED);
            }
        }
        
        x += velX;
        y += velY;
        
        // 边界检测
        if (x <= 0 || x >= Game.WIDTH - ENEMY_WIDTH) {
            x = Game.clamp(x, 0, Game.WIDTH - ENEMY_WIDTH);
        }
        if (y <= 0 || y >= Game.HEIGHT - ENEMY_HEIGHT - 30) {
            y = Game.clamp(y, 0, Game.HEIGHT - ENEMY_HEIGHT - 30);
        }
        
        // 轨迹效果
        trailCounter++;
        if (trailCounter >= 6) {
            Color trailColor = new Color(255, 100, 255, 60); // 紫色轨迹
            handler.addObject(new Trail(x, y, ID.Trail, trailColor, ENEMY_WIDTH, ENEMY_HEIGHT, 0.04f, handler));
            trailCounter = 0;
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (enemyImage != null) {
            // 绘制敌人图片
            g.drawImage(enemyImage, x, y, ENEMY_WIDTH, ENEMY_HEIGHT, null);
        } else {
            // 回退到彩色方块
            g.setColor(new Color(255, 100, 255));
            g.fillRect(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
        
        // 绘制血量条
        if (health < maxHealth) {
            g.setColor(Color.RED);
            g.fillRect(x, y - 8, ENEMY_WIDTH, 4);
            g.setColor(Color.GREEN);
            g.fillRect(x, y - 8, (int) ((float) health / maxHealth * ENEMY_WIDTH), 4);
        }
        
        // 绘制智能敌人特有的光环效果
        g.setColor(new Color(255, 100, 255, 50));
        g.drawOval(x - 5, y - 5, ENEMY_WIDTH + 10, ENEMY_HEIGHT + 10);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // 创建死亡爆炸效果
            createDeathEffect();
            handler.removeObject(this);
            HUD.score += 25; // 智能敌人给更多分数
        }
    }
    
    private void createDeathEffect() {
        // 创建死亡粒子效果
        for (int i = 0; i < 15; i++) {
            int velX = (int) (Math.random() * 12 - 6);
            int velY = (int) (Math.random() * 12 - 6);
            Color particleColor = new Color(255, 100, 255);
            handler.addObject(new Particle(x + ENEMY_WIDTH/2, y + ENEMY_HEIGHT/2, ID.Particle, velX, velY, particleColor, 40, handler));
        }
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
}
