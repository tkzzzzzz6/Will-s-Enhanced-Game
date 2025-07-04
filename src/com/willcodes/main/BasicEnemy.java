package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BasicEnemy extends GameObject {

    private Handler handler;
    private BufferedImage enemyImage;
    private int trailCounter = 0;  // 用于控制Trail生成频率
    
    // 敌人图片尺寸
    private final int ENEMY_WIDTH = 32;
    private final int ENEMY_HEIGHT = 32;

    public BasicEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        enemyImage = ImageLoader.getEnemyImage();
        velX = 5;
        velY = 5;
    }

    public Rectangle getBounds() {
        // 更新碰撞检测矩形大小，与图片大小一致
        return new Rectangle(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
    }

    public void tick() {
        x += velX;
        y += velY;

        // 边界检测也需要根据更新后的尺寸调整
        if (y <= 0 || y >= Game.HEIGHT - ENEMY_HEIGHT - 30) {
            velY *= -1;
        }

        if (x <= 0 || x >= Game.WIDTH - ENEMY_WIDTH) {
            velX *= -1;
        }
        
        // 降低轨迹生成频率，每5帧生成一次
        trailCounter++;
        if(trailCounter >= 5) {
            handler.addObject(new Trail(x, y, ID.Trail, new Color(255, 50, 50, 50), ENEMY_WIDTH, ENEMY_HEIGHT, 0.03f, handler));
            trailCounter = 0;
        }
    }

    public void render(Graphics g) {
        if (enemyImage != null) {
            // 绘制更大尺寸的敌人图片
            g.drawImage(enemyImage, x, y, ENEMY_WIDTH, ENEMY_HEIGHT, null);
        } else {
            // 如果图片加载失败，回退到原始的方块渲染
            g.setColor(Color.RED);
            g.fillRect(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
    }
}
