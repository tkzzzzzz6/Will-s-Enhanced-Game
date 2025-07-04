package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SplitterEnemy extends GameObject {
    private Handler handler;
    private BufferedImage enemyImage;
    private int trailCounter = 0;
    private int health = 25;
    private int maxHealth = 25;
    private int size; // 1=大, 2=中, 3=小
    private int pulseTimer = 0;
    
    private final int[] ENEMY_WIDTHS = {40, 28, 16};
    private final int[] ENEMY_HEIGHTS = {40, 28, 16};
    private final float[] SPEEDS = {1.5f, 2.5f, 4.0f};
    private final int[] MAX_HEALTHS = {25, 15, 8};
    
    public SplitterEnemy(int x, int y, ID id, Handler handler) {
        this(x, y, id, handler, 1); // 默认大小为1（最大）
    }
    
    public SplitterEnemy(int x, int y, ID id, Handler handler, int size) {
        super(x, y, id);
        this.handler = handler;
        this.size = Math.max(1, Math.min(3, size)); // 限制在1-3之间
        enemyImage = ImageLoader.getEnemyImage();
        
        // 根据大小设置属性
        this.health = MAX_HEALTHS[this.size - 1];
        this.maxHealth = MAX_HEALTHS[this.size - 1];
        
        // 随机移动方向
        double angle = Math.random() * 2 * Math.PI;
        float speed = SPEEDS[this.size - 1];
        velX = (int) (Math.cos(angle) * speed);
        velY = (int) (Math.sin(angle) * speed);
    }
    
    public Rectangle getBounds() {
        int width = ENEMY_WIDTHS[size - 1];
        int height = ENEMY_HEIGHTS[size - 1];
        return new Rectangle(x, y, width, height);
    }
    
    public void tick() {
        pulseTimer++;
        
        x += velX;
        y += velY;
        
        int width = ENEMY_WIDTHS[size - 1];
        int height = ENEMY_HEIGHTS[size - 1];
        
        // 边界反弹
        if (x <= 0 || x >= Game.WIDTH - width) {
            velX *= -1;
            x = Game.clamp(x, 0, Game.WIDTH - width);
        }
        if (y <= 0 || y >= Game.HEIGHT - height - 30) {
            velY *= -1;
            y = Game.clamp(y, 0, Game.HEIGHT - height - 30);
        }
        
        // 轨迹效果（小的敌人轨迹更频繁）
        trailCounter++;
        int trailFreq = 10 - (size - 1) * 2; // 大敌人10帧，中敌人8帧，小敌人6帧
        if (trailCounter >= trailFreq) {
            Color trailColor = getSplitterColor();
            trailColor = new Color(trailColor.getRed(), trailColor.getGreen(), trailColor.getBlue(), 60);
            handler.addObject(new Trail(x, y, ID.Trail, trailColor, width, height, 0.04f, handler));
            trailCounter = 0;
        }
    }
    
    private Color getSplitterColor() {
        switch (size) {
            case 1: return new Color(255, 0, 255);   // 大：紫色
            case 2: return new Color(255, 100, 255); // 中：浅紫色
            case 3: return new Color(255, 150, 255); // 小：更浅紫色
            default: return new Color(255, 0, 255);
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = ENEMY_WIDTHS[size - 1];
        int height = ENEMY_HEIGHTS[size - 1];
        
        if (enemyImage != null && size == 1) {
            // 只有大敌人使用图片
            g.drawImage(enemyImage, x, y, width, height, null);
        } else {
            // 绘制分裂敌人特有的形状
            Color baseColor = getSplitterColor();
            
            // 脉冲效果
            float pulse = 1.0f + 0.2f * (float) Math.sin(pulseTimer * 0.1f);
            int pulseWidth = (int) (width * pulse);
            int pulseHeight = (int) (height * pulse);
            int offsetX = (pulseWidth - width) / 2;
            int offsetY = (pulseHeight - height) / 2;
            
            // 外层脉冲
            g.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50));
            g.fillOval(x - offsetX, y - offsetY, pulseWidth, pulseHeight);
            
            // 主体
            g.setColor(baseColor);
            g.fillOval(x, y, width, height);
            
            // 内核
            g.setColor(new Color(255, 255, 255, 150));
            g.fillOval(x + width/4, y + height/4, width/2, height/2);
            
            // 分裂线条（表示即将分裂）
            if (health < maxHealth * 0.5) {
                g.setColor(new Color(255, 255, 255, 200));
                g.drawLine(x + width/2, y, x + width/2, y + height);
                if (size == 1) {
                    g.drawLine(x, y + height/2, x + width, y + height/2);
                }
            }
        }
        
        // 绘制血量条（只有大和中敌人显示）
        if (size <= 2 && health < maxHealth) {
            g.setColor(Color.RED);
            g.fillRect(x, y - 8, width, 4);
            g.setColor(Color.GREEN);
            g.fillRect(x, y - 8, (int) ((float) health / maxHealth * width), 4);
            g.setColor(Color.WHITE);
            g.drawRect(x, y - 8, width, 4);
        }
        
        // 大小标识
        g.setFont(new Font("Arial", Font.BOLD, 8));
        g.setColor(Color.WHITE);
        String sizeText = size == 1 ? "L" : size == 2 ? "M" : "S";
        g.drawString(sizeText, x + 2, y + 10);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            split();
            handler.removeObject(this);
            
            // 根据大小给分数
            int score = size == 1 ? 30 : size == 2 ? 20 : 10;
            HUD.score += score;
            HUD.enemiesKilled++;
        }
    }
    
    private void split() {
        createDeathEffect();
        
        // 只有大和中敌人会分裂
        if (size < 3) {
            int newSize = size + 1;
            int splitCount = size == 1 ? 4 : 2; // 大敌人分裂成4个，中敌人分裂成2个
            
            for (int i = 0; i < splitCount; i++) {
                // 计算分裂位置
                double angle = (2 * Math.PI * i) / splitCount;
                int offsetX = (int) (Math.cos(angle) * 20);
                int offsetY = (int) (Math.sin(angle) * 20);
                
                int newX = x + ENEMY_WIDTHS[size - 1] / 2 + offsetX;
                int newY = y + ENEMY_HEIGHTS[size - 1] / 2 + offsetY;
                
                // 确保新敌人在屏幕内
                newX = Game.clamp(newX, 0, Game.WIDTH - ENEMY_WIDTHS[newSize - 1]);
                newY = Game.clamp(newY, 0, Game.HEIGHT - ENEMY_HEIGHTS[newSize - 1] - 30);
                
                // 创建新的分裂敌人
                SplitterEnemy newEnemy = new SplitterEnemy(newX, newY, ID.SplitterEnemy, handler, newSize);
                
                // 给新敌人一个向外的初始速度
                float speed = SPEEDS[newSize - 1];
                newEnemy.setVelX((int) (Math.cos(angle) * speed));
                newEnemy.setVelY((int) (Math.sin(angle) * speed));
                
                handler.addObject(newEnemy);
            }
            
            // 播放分裂音效
            AudioPlayer.play("explosion");
        }
    }
    
    private void createDeathEffect() {
        int width = ENEMY_WIDTHS[size - 1];
        int height = ENEMY_HEIGHTS[size - 1];
        Color particleColor = getSplitterColor();
        
        // 根据大小创建不同数量的粒子
        int particleCount = 8 + (4 - size) * 4;
        
        for (int i = 0; i < particleCount; i++) {
            int velX = (int) (Math.random() * 8 - 4);
            int velY = (int) (Math.random() * 8 - 4);
            handler.addObject(new Particle(x + width/2, y + height/2, ID.Particle, velX, velY, particleColor, 30, handler));
        }
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getSize() {
        return size;
    }
}
