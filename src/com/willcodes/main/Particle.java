package com.willcodes.main;

import java.awt.*;

public class Particle extends GameObject {
    private Handler handler;
    private Color color;
    private int life;
    private int maxLife;
    private float alpha;
    private int size;
    private float gravity;
    
    public Particle(int x, int y, ID id, int velX, int velY, Color color, int life, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        this.velX = velX;
        this.velY = velY;
        this.color = color;
        this.life = life;
        this.maxLife = life;
        this.alpha = 1.0f;
        this.size = (int)(Math.random() * 4 + 2);
        this.gravity = 0.1f;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
    
    public void tick() {
        x += velX;
        y += velY;
        
        // 添加重力效果
        velY += gravity;
        
        // 添加空气阻力
        velX *= 0.98f;
        velY *= 0.98f;
        
        life--;
        
        // 计算透明度
        alpha = (float) life / maxLife;
        
        // 粒子生命结束时移除
        if (life <= 0) {
            handler.removeObject(this);
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 设置透明度
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        // 绘制粒子
        g.setColor(color);
        g.fillOval(x, y, size, size);
        
        // 添加发光效果
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 50)));
        g.fillOval(x - 1, y - 1, size + 2, size + 2);
        
        // 恢复原始透明度
        g2d.setComposite(oldComposite);
    }
}
