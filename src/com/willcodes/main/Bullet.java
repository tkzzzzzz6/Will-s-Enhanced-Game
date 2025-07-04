package com.willcodes.main;

import java.awt.*;

public class Bullet extends GameObject {
    private Handler handler;
    private int damage;
    private Color color;
    private int life;
    private final int maxLife = 300; // 子弹存活时间
    
    public Bullet(int x, int y, ID id, int velX, int velY, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        this.velX = velX;
        this.velY = velY;
        this.damage = 10;
        this.life = maxLife;
        
        if (id == ID.Bullet) {
            this.color = new Color(255, 255, 0); // 玩家子弹为黄色
        } else {
            this.color = new Color(255, 100, 100); // 敌人子弹为红色
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }
    
    public void tick() {
        x += velX;
        y += velY;
        life--;
        
        // 子弹超出屏幕边界或生命耗尽时移除
        if (x < 0 || x > Game.WIDTH || y < 0 || y > Game.HEIGHT || life <= 0) {
            handler.removeObject(this);
            return;
        }
        
        // 碰撞检测
        collision();
    }
    
    private void collision() {
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);

            if (tempObject.getBounds() != null && getBounds().intersects(tempObject.getBounds())) {
                // 玩家子弹击中敌人
                if (id == ID.Bullet && (tempObject.getId() == ID.BasicEnemy ||
                    tempObject.getId() == ID.SmartEnemy ||
                    tempObject.getId() == ID.ShooterEnemy ||
                    tempObject.getId() == ID.FastEnemy ||
                    tempObject.getId() == ID.TankEnemy ||
                    tempObject.getId() == ID.BossEnemy)) {

                    // 创建爆炸效果
                    createExplosion(tempObject.getX(), tempObject.getY());

                    // 增加击杀数和分数
                    HUD.enemiesKilled++;

                    // 根据敌人类型给不同分数
                    if (tempObject.getId() == ID.BasicEnemy) {
                        HUD.score += 10;
                    } else if (tempObject.getId() == ID.SmartEnemy) {
                        HUD.score += 25;
                    } else if (tempObject.getId() == ID.ShooterEnemy) {
                        HUD.score += 20;
                    } else if (tempObject.getId() == ID.FastEnemy) {
                        HUD.score += 15;
                    } else if (tempObject.getId() == ID.TankEnemy) {
                        HUD.score += 50;
                    } else if (tempObject.getId() == ID.BossEnemy) {
                        HUD.score += 100;
                    }

                    // 移除敌人和子弹
                    handler.removeObject(tempObject);
                    handler.removeObject(this);

                    // 播放音效
                    AudioPlayer.play("explosion");
                    return;
                }
                
                // 敌人子弹击中玩家
                if (id == ID.EnemyBullet && tempObject.getId() == ID.Player) {
                    Player player = (Player) tempObject;
                    if (!player.hasShield()) {
                        HUD.HEALTH -= damage;
                        AudioPlayer.play("hit");
                    }
                    handler.removeObject(this);
                    return;
                }
            }
        }
    }
    
    private void createExplosion(int x, int y) {
        // 创建爆炸粒子效果
        for (int i = 0; i < 8; i++) {
            int velX = (int) (Math.random() * 10 - 5);
            int velY = (int) (Math.random() * 10 - 5);
            Color particleColor = new Color(255, (int)(Math.random() * 100 + 155), 0);
            handler.addObject(new Particle(x, y, ID.Particle, velX, velY, particleColor, 30, handler));
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制子弹主体
        g.setColor(color);
        g.fillOval(x, y, 8, 8);
        
        // 添加发光效果
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g.fillOval(x - 2, y - 2, 12, 12);
        
        // 绘制轨迹
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        g.fillOval(x - velX/2, y - velY/2, 6, 6);
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
}
