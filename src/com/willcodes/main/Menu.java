package com.willcodes.main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Menu extends MouseAdapter {
    private Game game;
    private Handler handler;
    private Font titleFont;
    private Font menuFont;
    
    public Menu(Game game, Handler handler) {
        this.game = game;
        this.handler = handler;
        titleFont = new Font("Arial", Font.BOLD, 50);
        menuFont = new Font("Arial", Font.BOLD, 30);
    }
    
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        
        if (Game.gameState == GameState.MENU) {
            // 开始游戏按钮
            if (mouseOver(mx, my, 210, 150, 200, 64)) {
                Game.setGameState(GameState.GAME);
                handler.clearObjects();
                game.initGame();
                AudioPlayer.play("powerup");
            }

            // 难度选择按钮
            if (mouseOver(mx, my, 210, 220, 200, 64)) {
                Game.setGameState(GameState.SETTINGS);
                AudioPlayer.play("powerup");
            }

            // 皮肤选择按钮
            if (mouseOver(mx, my, 210, 290, 200, 64)) {
                Game.setGameState(GameState.SHOP);
                AudioPlayer.play("powerup");
            }

            // 退出按钮
            if (mouseOver(mx, my, 210, 360, 200, 64)) {
                System.exit(0);
            }
        } else if (Game.gameState == GameState.SETTINGS) {
            // 难度选择
            if (mouseOver(mx, my, 150, 150, 100, 50)) {
                Game.difficulty = 1;
                AudioPlayer.play("powerup");
            }
            if (mouseOver(mx, my, 270, 150, 100, 50)) {
                Game.difficulty = 2;
                AudioPlayer.play("powerup");
            }
            if (mouseOver(mx, my, 390, 150, 100, 50)) {
                Game.difficulty = 3;
                AudioPlayer.play("powerup");
            }

            // 返回按钮
            if (mouseOver(mx, my, 210, 350, 200, 64)) {
                Game.setGameState(GameState.MENU);
                AudioPlayer.play("powerup");
            }
        } else if (Game.gameState == GameState.SHOP) {
            // 皮肤选择
            if (mouseOver(mx, my, 150, 150, 100, 100)) {
                Game.playerSkin = "default";
                AudioPlayer.play("powerup");
            }
            if (mouseOver(mx, my, 270, 150, 100, 100)) {
                Game.playerSkin = "blue";
                AudioPlayer.play("powerup");
            }
            if (mouseOver(mx, my, 390, 150, 100, 100)) {
                Game.playerSkin = "red";
                AudioPlayer.play("powerup");
            }

            // 返回按钮
            if (mouseOver(mx, my, 210, 350, 200, 64)) {
                Game.setGameState(GameState.MENU);
                AudioPlayer.play("powerup");
            }
        } else if (Game.gameState == GameState.GAMEOVER) {
            // 重新开始按钮
            if (mouseOver(mx, my, 150, 300, 150, 64)) {
                Game.setGameState(GameState.GAME);
                handler.clearObjects();
                game.initGame();
                AudioPlayer.play("powerup");
            }

            // 返回菜单按钮
            if (mouseOver(mx, my, 330, 300, 150, 64)) {
                Game.setGameState(GameState.MENU);
                AudioPlayer.play("powerup");
            }
        }
    }
    
    private boolean mouseOver(int mx, int my, int x, int y, int width, int height) {
        return mx > x && mx < x + width && my > y && my < y + height;
    }
    
    public void tick() {
        
    }
    
    public void render(Graphics g) {
        if (Game.gameState == GameState.MENU) {
            // 绘制渐变背景
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 50),
                                                      0, Game.HEIGHT, new Color(50, 20, 80));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

            g.setFont(titleFont);
            g.setColor(new Color(255, 215, 0)); // 金色标题
            g.drawString("超级躲避游戏", 150, 70);

            g.setFont(menuFont);
            g.setColor(Color.WHITE);

            // 开始游戏按钮
            g.setColor(new Color(0, 150, 0));
            g.fillRect(210, 150, 200, 64);
            g.setColor(Color.WHITE);
            g.drawRect(210, 150, 200, 64);
            g.drawString("开始游戏", 240, 190);

            // 难度设置按钮
            g.setColor(new Color(0, 100, 150));
            g.fillRect(210, 220, 200, 64);
            g.setColor(Color.WHITE);
            g.drawRect(210, 220, 200, 64);
            g.drawString("难度设置", 240, 260);

            // 皮肤选择按钮
            g.setColor(new Color(150, 0, 150));
            g.fillRect(210, 290, 200, 64);
            g.setColor(Color.WHITE);
            g.drawRect(210, 290, 200, 64);
            g.drawString("皮肤选择", 240, 330);

            // 退出按钮
            g.setColor(new Color(150, 0, 0));
            g.fillRect(210, 360, 200, 64);
            g.setColor(Color.WHITE);
            g.drawRect(210, 360, 200, 64);
            g.drawString("退出游戏", 240, 400);
        } else if (Game.gameState == GameState.SETTINGS) {
            // 绘制设置界面背景
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 60),
                                                      0, Game.HEIGHT, new Color(60, 30, 90));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

            g.setFont(titleFont);
            g.setColor(new Color(255, 215, 0));
            g.drawString("难度设置", 200, 70);

            g.setFont(menuFont);
            g.setColor(Color.WHITE);
            g.drawString("选择游戏难度:", 220, 120);

            // 难度按钮
            String[] difficulties = {"简单", "普通", "困难"};
            Color[] diffColors = {new Color(0, 150, 0), new Color(150, 150, 0), new Color(150, 0, 0)};

            for (int i = 0; i < 3; i++) {
                int x = 150 + i * 120;

                // 高亮当前选中的难度
                if (Game.difficulty == i + 1) {
                    g.setColor(new Color(255, 255, 255, 100));
                    g.fillRect(x - 5, 145, 110, 60);
                }

                g.setColor(diffColors[i]);
                g.fillRect(x, 150, 100, 50);
                g.setColor(Color.WHITE);
                g.drawRect(x, 150, 100, 50);
                g.drawString(difficulties[i], x + 20, 180);
            }

            // 返回按钮
            g.setColor(new Color(100, 100, 100));
            g.fillRect(210, 350, 200, 64);
            g.setColor(Color.WHITE);
            g.drawRect(210, 350, 200, 64);
            g.drawString("返回", 270, 390);
        } else if (Game.gameState == GameState.SHOP) {
            // 绘制皮肤选择界面背景
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 20, 60),
                                                      0, Game.HEIGHT, new Color(80, 40, 120));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

            g.setFont(titleFont);
            g.setColor(new Color(255, 215, 0));
            g.drawString("皮肤选择", 200, 70);

            g.setFont(menuFont);
            g.setColor(Color.WHITE);
            g.drawString("选择玩家皮肤:", 220, 120);

            // 皮肤选择框
            String[] skins = {"默认", "蓝色", "红色"};
            Color[] skinColors = {new Color(100, 100, 255), new Color(0, 100, 255), new Color(255, 100, 100)};

            for (int i = 0; i < 3; i++) {
                int x = 150 + i * 120;

                // 高亮当前选中的皮肤
                String[] skinNames = {"default", "blue", "red"};
                if (Game.playerSkin.equals(skinNames[i])) {
                    g.setColor(new Color(255, 255, 255, 150));
                    g.fillRect(x - 5, 145, 110, 110);
                }

                // 绘制皮肤预览
                g.setColor(skinColors[i]);
                g.fillRect(x + 25, 150, 50, 50);
                g.setColor(Color.WHITE);
                g.drawRect(x + 25, 150, 50, 50);
                g.drawRect(x, 150, 100, 100);

                // 皮肤名称
                g.drawString(skins[i], x + 25, 230);
            }

            // 返回按钮
            g.setColor(new Color(100, 100, 100));
            g.fillRect(210, 350, 200, 64);
            g.setColor(Color.WHITE);
            g.drawRect(210, 350, 200, 64);
            g.drawString("返回", 270, 390);
        } else if (Game.gameState == GameState.GAMEOVER) {
            // 绘制半透明背景
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

            g.setFont(titleFont);
            g.setColor(Color.RED);
            g.drawString("游戏结束", 180, 100);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            g.drawString("最终得分: " + HUD.score, 220, 180);
            g.drawString("击杀敌人: " + HUD.enemiesKilled, 220, 210);
            g.drawString("达到等级: " + HUD.level, 220, 240);

            g.setFont(menuFont);

            // 重新开始按钮
            g.setColor(new Color(0, 150, 0));
            g.fillRect(150, 300, 150, 64);
            g.setColor(Color.WHITE);
            g.drawRect(150, 300, 150, 64);
            g.drawString("重新开始", 170, 340);

            // 返回菜单按钮
            g.setColor(new Color(150, 0, 0));
            g.fillRect(330, 300, 150, 64);
            g.setColor(Color.WHITE);
            g.drawRect(330, 300, 150, 64);
            g.drawString("返回菜单", 350, 340);
        }
    }
}