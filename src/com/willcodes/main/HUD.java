package com.willcodes.main;

import java.awt.*;

public class HUD {
    public static int HEALTH = 1000;  // 提升为原来的10倍
    public static int MAX_HEALTH = 1000;
    public static int score = 0;
    public static int level = 1;
    public static int enemiesKilled = 0;

    private static int levelUpThreshold = 100;

    public static void tick() {
        HEALTH = Game.clamp(HEALTH, 0, MAX_HEALTH);

        // 检查升级
        if (score >= levelUpThreshold) {
            level++;
            levelUpThreshold += 150;
            AudioPlayer.play("levelup");
        }

        if (HEALTH <= 0) {
            // 游戏结束逻辑 - 不再直接退出，而是切换到游戏结束状态
            AudioPlayer.play("gameover");
            Game.setGameState(GameState.GAMEOVER);
        }
    }

    public static void reset() {
        HEALTH = MAX_HEALTH;
        score = 0;
        level = 1;
        enemiesKilled = 0;
        levelUpThreshold = 100;
    }

    public static void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制血量条背景
        g.setColor(new Color(50, 50, 50));
        g.fillRect(15, 15, 200, 32);

        // 绘制血量条
        Color healthColor;
        float healthPercent = (float) HEALTH / MAX_HEALTH;
        if (healthPercent > 0.6f) {
            healthColor = Color.GREEN;
        } else if (healthPercent > 0.3f) {
            healthColor = Color.YELLOW;
        } else {
            healthColor = Color.RED;
        }
        g.setColor(healthColor);
        g.fillRect(15, 15, (int)(healthPercent * 200), 32);

        // 血量条边框
        g.setColor(Color.WHITE);
        g.drawRect(15, 15, 200, 32);

        // 血量文字
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("HP: " + HEALTH, 20, 35);

        // 分数显示
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("分数: " + score, 15, 70);

        // 等级显示
        g.drawString("等级: " + level, 15, 95);

        // 击杀数显示
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("击杀: " + enemiesKilled, 15, 115);

        // 波次显示
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(255, 215, 0)); // 金色
        g.drawString("第 " + Game.getCurrentWave() + " 波", Game.WIDTH - 120, 30);

        // 波次状态
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        if (Game.isWaveInProgress()) {
            int remaining = Game.getRemainingEnemies();
            g.drawString("剩余敌人: " + remaining, Game.WIDTH - 120, 50);
        } else {
            g.drawString("准备下一波...", Game.WIDTH - 120, 50);
        }

        // 下一级所需分数
        g.drawString("下级: " + (levelUpThreshold - score), 15, 135);

        // 控制提示
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(200, 200, 200));
        g.drawString("WASD移动 | 空格射击 | M音效开关", Game.WIDTH - 250, Game.HEIGHT - 10);
    }
}
