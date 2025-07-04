package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Player extends GameObject {
    Random r = new Random();
    Handler handler;
    private BufferedImage playerImage;
    private int trailCounter = 0;

    private final int PLAYER_WIDTH = 48;
    private final int PLAYER_HEIGHT = 48;

    private boolean speedBoost = false;
    private boolean shield = false;
    private int speedBoostTimer = 0;
    private int shieldTimer = 0;
    private final int BOOST_DURATION = 300; // 约5秒

    // 射击相关
    private int shootCooldown = 0;
    private int weaponLevel = 1;
    
    public Player(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        playerImage = ImageLoader.getPlayerImage();
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
    }
    
    public void tick() {
        // 速度加成效果
        if (speedBoost) {
            x += velX * 1.5;
            y += velY * 1.5;
            speedBoostTimer++;
            if (speedBoostTimer > BOOST_DURATION) {
                speedBoost = false;
                speedBoostTimer = 0;
            }
        } else {
            x += velX;
            y += velY;
        }

        // 护盾效果计时
        if (shield) {
            shieldTimer++;
            if (shieldTimer > BOOST_DURATION) {
                shield = false;
                shieldTimer = 0;
            }
        }

        // 射击冷却
        if (shootCooldown > 0) {
            shootCooldown--;
        }

        x = Game.clamp(x, 0, Game.WIDTH - PLAYER_WIDTH);
        y = Game.clamp(y, 0, Game.HEIGHT - PLAYER_HEIGHT - 30);
        
        // 轨迹效果
        trailCounter++;
        if(trailCounter >= 5) {
            Color trailColor;
            if (speedBoost) {
                trailColor = new Color(255, 255, 0, 50); // 黄色轨迹表示速度加成
            } else {
                trailColor = new Color(240, 240, 240, 50); // 普通白色轨迹
            }
            handler.addObject(new Trail(x, y, ID.Trail, trailColor, PLAYER_WIDTH, PLAYER_HEIGHT, 0.03f, handler));
            trailCounter = 0;
        }
        
        collision();
    }
    
    private void collision() {
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);

            // 检测与各种敌人的碰撞
            if (tempObject.getId() == ID.BasicEnemy ||
                tempObject.getId() == ID.SmartEnemy ||
                tempObject.getId() == ID.ShooterEnemy ||
                tempObject.getId() == ID.FastEnemy ||
                tempObject.getId() == ID.TankEnemy ||
                tempObject.getId() == ID.BossEnemy) {
                if(getBounds().intersects(tempObject.getBounds())) {
                    // 如果有护盾，不受伤害
                    if (!shield) {
                        HUD.HEALTH -= 2;
                        AudioPlayer.play("hit");
                    }
                }
            }
        }
    }
    
    public void render(Graphics g) {
        if (playerImage != null) {
            g.drawImage(playerImage, x, y, PLAYER_WIDTH, PLAYER_HEIGHT, null);
            
            // 如果有护盾，绘制护盾效果
            if (shield) {
                g.setColor(new Color(0, 255, 255, 100));
                g.fillOval(x - 5, y - 5, PLAYER_WIDTH + 10, PLAYER_HEIGHT + 10);
            }
            
            // 如果有速度加成，绘制速度效果
            if (speedBoost) {
                g.setColor(new Color(255, 255, 0, 100));
                g.drawOval(x - 2, y - 2, PLAYER_WIDTH + 4, PLAYER_HEIGHT + 4);
            }
        } else {
            g.setColor(Color.white);
            g.fillRect(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        }
    }
    
    public void setSpeedBoost(boolean speedBoost) {
        this.speedBoost = speedBoost;
        this.speedBoostTimer = 0;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
        this.shieldTimer = 0;
    }

    public boolean hasShield() {
        return shield;
    }

    public void shoot() {
        if (shootCooldown <= 0) {
            int bulletSpeed = 8;

            // 根据武器等级发射不同数量的子弹
            if (weaponLevel == 1) {
                // 单发子弹
                handler.addObject(new Bullet(x + PLAYER_WIDTH/2 - 4, y, ID.Bullet, 0, -bulletSpeed, handler));
            } else if (weaponLevel == 2) {
                // 双发子弹
                handler.addObject(new Bullet(x + PLAYER_WIDTH/2 - 8, y, ID.Bullet, -1, -bulletSpeed, handler));
                handler.addObject(new Bullet(x + PLAYER_WIDTH/2 + 4, y, ID.Bullet, 1, -bulletSpeed, handler));
            } else if (weaponLevel >= 3) {
                // 三发子弹
                handler.addObject(new Bullet(x + PLAYER_WIDTH/2 - 4, y, ID.Bullet, 0, -bulletSpeed, handler));
                handler.addObject(new Bullet(x + PLAYER_WIDTH/2 - 12, y, ID.Bullet, -2, -bulletSpeed, handler));
                handler.addObject(new Bullet(x + PLAYER_WIDTH/2 + 8, y, ID.Bullet, 2, -bulletSpeed, handler));
            }

            shootCooldown = 15; // 射击冷却时间
            AudioPlayer.play("shoot");
        }
    }

    public void upgradeWeapon() {
        if (weaponLevel < 3) {
            weaponLevel++;
        }
    }

    public int getWeaponLevel() {
        return weaponLevel;
    }
}
