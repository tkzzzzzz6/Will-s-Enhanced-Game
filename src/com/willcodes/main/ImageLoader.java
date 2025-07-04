package com.willcodes.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 用于加载游戏中使用的图片资源
 */
public class ImageLoader {
    
    private static BufferedImage playerImage;
    private static BufferedImage enemyImage;
    private static BufferedImage backgroundImage;
    private static BufferedImage[] powerUpImages = new BufferedImage[3];
    
    /**
     * 初始化并加载所有游戏图片
     */
    public static void loadImages() {
        // 加载玩家和敌人图片
        playerImage = loadImage("player.png");
        enemyImage = loadImage("enemy.png");
        
        // 加载背景图片
        backgroundImage = loadImage("background.png");
        
        // 加载道具图片
        powerUpImages[0] = loadImage("powerup_health.png");
        powerUpImages[1] = loadImage("powerup_speed.png");
        powerUpImages[2] = loadImage("powerup_shield.png");
        
        if (playerImage != null && enemyImage != null) {
            System.out.println("图片资源加载成功");
        } else {
            System.err.println("警告: 部分或全部图片资源加载失败，将使用默认图形");
        }
    }
    
    /**
     * 尝试多种方法加载单个图片
     */
    private static BufferedImage loadImage(String fileName) {
        BufferedImage img = null;

        // 尝试从文件系统直接加载
        try {
            img = ImageIO.read(new File("src/images/" + fileName));
            if (img != null) {
                return img;
            }
        } catch (IOException e) {
            // 静默处理
        }

        // 尝试从当前目录加载
        try {
            img = ImageIO.read(new File("images/" + fileName));
            if (img != null) {
                return img;
            }
        } catch (IOException e) {
            // 静默处理
        }

        // 尝试从类路径资源加载
        try {
            img = ImageIO.read(ImageLoader.class.getResource("/images/" + fileName));
            if (img != null) {
                return img;
            }
        } catch (Exception e) {
            // 静默处理
        }

        // 尝试从类路径资源加载 (不使用前导斜杠)
        try {
            img = ImageIO.read(ImageLoader.class.getResource("images/" + fileName));
            if (img != null) {
                return img;
            }
        } catch (Exception e) {
            // 静默处理
        }

        // 尝试从类加载器加载
        try {
            img = ImageIO.read(ImageLoader.class.getClassLoader().getResource("images/" + fileName));
            if (img != null) {
                return img;
            }
        } catch (Exception e) {
            // 静默处理
        }

        return null; // 所有方法都失败，返回null
    }
    
    /**
     * 获取玩家图片
     */
    public static BufferedImage getPlayerImage() {
        return playerImage;
    }
    
    /**
     * 获取敌人图片
     */
    public static BufferedImage getEnemyImage() {
        return enemyImage;
    }
    
    public static BufferedImage getBackgroundImage() {
        return backgroundImage;
    }
    
    public static BufferedImage getPowerUpImage(int type) {
        if (type >= 0 && type < powerUpImages.length) {
            return powerUpImages[type];
        }
        return null;
    }
}
