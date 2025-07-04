package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TankEnemy extends GameObject {
    private Handler handler;
    private BufferedImage enemyImage;
    private int trailCounter = 0;
    private int health = 50;
    private int maxHealth = 50;
    private int shieldTimer = 0;
    private boolean hasShield = false;
    
    private final int ENEMY_WIDTH = 48;
    private final int ENEMY_HEIGHT = 48;
    private final float SPEED = 1.0f;
    
    public TankEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        enemyImage = ImageLoader.getEnemyImage();
        
        // 缓慢移动
        double angle = Math.random() * 2 * Math.PI;
        velX = (int) (Math.cos(angle) * SPEED);
        velY = (int) (Math.sin(angle) * SPEED);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
    }
    
    public void tick() {
        // 护盾逻辑
        shieldTimer++;
        if (shieldTimer >= 300) { // 每5秒激活护盾
            hasShield = true;
            shieldTimer = 0;
        }
        
        if (hasShield && shieldTimer >= 120) { // 护盾持续2秒
            hasShield = false;
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
        if (trailCounter >= 12) {
            Color trailColor = hasShield ? 
                new Color(0, 255, 0, 60) :   // 护盾时绿色轨迹
                new Color(139, 69, 19, 60);  // 正常时棕色轨迹
            handler.addObject(new Trail(x, y, ID.Trail, trailColor, ENEMY_WIDTH, ENEMY_HEIGHT, 0.02f, handler));
            trailCounter = 0;
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (enemyImage != null) {
            g.drawImage(enemyImage, x, y, ENEMY_WIDTH, ENEMY_HEIGHT, null);
        } else {
            // 坦克敌人为棕色
            g.setColor(new Color(139, 69, 19));
            g.fillRect(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
            
            // 装甲细节
            g.setColor(new Color(160, 82, 45));
            g.fillRect(x + 4, y + 4, ENEMY_WIDTH - 8, ENEMY_HEIGHT - 8);
        }
        
        // 护盾效果
        if (hasShield) {
            g.setColor(new Color(0, 255, 0, 100));
            g.fillOval(x - 8, y - 8, ENEMY_WIDTH + 16, ENEMY_HEIGHT + 16);
            g.setColor(new Color(0, 255, 0, 200));
            g.drawOval(x - 8, y - 8, ENEMY_WIDTH + 16, ENEMY_HEIGHT + 16);
        }
        
        // 绘制血量条
        g.setColor(Color.RED);
        g.fillRect(x, y - 12, ENEMY_WIDTH, 6);
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 12, (int) ((float) health / maxHealth * ENEMY_WIDTH), 6);
        g.setColor(Color.WHITE);
        g.drawRect(x, y - 12, ENEMY_WIDTH, 6);
        
        // 血量数字
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        g.drawString(health + "/" + maxHealth, x + 2, y - 2);
        
        // 护盾充能指示
        if (!hasShield && shieldTimer > 240) {
            g.setColor(new Color(0, 255, 0, 150));
            g.fillOval(x + ENEMY_WIDTH/2 - 3, y + ENEMY_HEIGHT/2 - 3, 6, 6);
        }
    }
    
    public void takeDamage(int damage) {
        if (hasShield) {
            // 护盾状态下减少伤害
            damage = Math.max(1, damage / 3);
        }
        
        health -= damage;
        if (health <= 0) {
            createDeathEffect();
            handler.removeObject(this);
            HUD.score += 50; // 坦克敌人给50分
            HUD.enemiesKilled++;
        }
    }
    
    private void createDeathEffect() {
        // 大爆炸效果
        for (int i = 0; i < 20; i++) {
            int velX = (int) (Math.random() * 12 - 6);
            int velY = (int) (Math.random() * 12 - 6);
            Color particleColor = new Color(255, 100, 0);
            handler.addObject(new Particle(x + ENEMY_WIDTH/2, y + ENEMY_HEIGHT/2, ID.Particle, velX, velY, particleColor, 50, handler));
        }
        
        // 额外的烟雾效果
        for (int i = 0; i < 10; i++) {
            int velX = (int) (Math.random() * 6 - 3);
            int velY = (int) (Math.random() * 6 - 3);
            Color smokeColor = new Color(100, 100, 100);
            handler.addObject(new Particle(x + ENEMY_WIDTH/2, y + ENEMY_HEIGHT/2, ID.Particle, velX, velY, smokeColor, 80, handler));
        }
    }
    
    public int getHealth() {
        return health;
    }
    
    public boolean hasShield() {
        return hasShield;
    }
}
