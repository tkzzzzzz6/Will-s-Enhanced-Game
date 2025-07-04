package com.willcodes.main;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
    private Thread thread;
    private boolean running = false;
    public static int frames;

    private final Handler handler;
    public static GameState gameState = GameState.MENU;  // 改为静态，从菜单开始
    private Menu menu;

    // 游戏难度设置
    public static int difficulty = 1; // 1=简单, 2=普通, 3=困难
    public static String playerSkin = "default"; // 玩家皮肤

    private Random r = new Random();
    private int powerUpTimer = 0;

    // 波次系统
    private int currentWave = 1;
    private int enemiesInCurrentWave = 0;
    private int enemiesKilledInWave = 0;
    private int waveSpawnTimer = 0;
    private boolean waveInProgress = false;
    private int wavePauseTimer = 0;
    private final int WAVE_PAUSE_DURATION = 180; // 3秒间隔

    public Game() {
        instance = this; // 设置静态实例

        // 加载游戏图片资源
        ImageLoader.loadImages();

        // 加载音频资源
        AudioPlayer.loadAudio();

        handler = new Handler();
        menu = new Menu(this, handler);
        this.addKeyListener(new KeyInput(handler));
        this.addMouseListener(menu);
        new Window(WIDTH, HEIGHT, "Wills Enhanced Game", this);

        // 开始播放背景音乐
        AudioPlayer.loop("background");
    }

    public static void setGameState(GameState state) {
        gameState = state;
    }

    public void initGame() {
        // 清空所有对象
        handler.objects.clear();

        // 重置游戏状态
        HUD.reset();
        powerUpTimer = 0;

        // 重置波次系统
        currentWave = 1;
        enemiesInCurrentWave = 0;
        enemiesKilledInWave = 0;
        waveSpawnTimer = 0;
        waveInProgress = false;
        wavePauseTimer = 0;

        // 调整玩家初始位置到屏幕中心
        handler.addObject(new Player(WIDTH/2-24, HEIGHT/2-24, ID.Player, handler));

        // 开始第一波敌人
        startNewWave();

        // 生成初始道具
        spawnPowerUp();
    }

    private void startNewWave() {
        waveInProgress = true;
        waveSpawnTimer = 0;
        enemiesKilledInWave = 0;

        // 计算这一波的敌人数量（随波次递增）
        int baseEnemyCount = 3 + difficulty; // 基础敌人数量
        enemiesInCurrentWave = baseEnemyCount + (currentWave - 1) * 2; // 每波增加2个敌人

        // 最大限制，避免敌人过多
        enemiesInCurrentWave = Math.min(enemiesInCurrentWave, 20);

        System.out.println("第 " + currentWave + " 波开始！敌人数量: " + enemiesInCurrentWave);
    }

    private void spawnWaveEnemy() {
        if (enemiesKilledInWave >= enemiesInCurrentWave) {
            return; // 这一波已经生成完毕
        }

        // 在屏幕边缘生成敌人
        int enemyX, enemyY;
        if (r.nextBoolean()) {
            // 从左右边缘生成
            enemyX = r.nextBoolean() ? -40 : WIDTH;
            enemyY = r.nextInt(HEIGHT - 60);
        } else {
            // 从上下边缘生成
            enemyX = r.nextInt(WIDTH - 40);
            enemyY = r.nextBoolean() ? -40 : HEIGHT - 30;
        }

        // 根据波次和难度决定敌人类型
        int enemyType = r.nextInt(100);
        int smartChance = 10 + difficulty * 5 + currentWave * 2; // 智能敌人
        int shooterChance = 8 + difficulty * 4 + currentWave * 2; // 射击敌人
        int fastChance = 5 + currentWave; // 快速敌人
        int tankChance = 3 + (currentWave / 3); // 坦克敌人（较少）

        // 确保敌人在合理位置（不要太靠近边界）
        if (enemyX < 0) enemyX = 10;
        if (enemyX > WIDTH - 50) enemyX = WIDTH - 50;
        if (enemyY < 0) enemyY = 10;
        if (enemyY > HEIGHT - 80) enemyY = HEIGHT - 80;

        // 生成不同类型的敌人
        if (enemyType < 100 - smartChance - shooterChance - fastChance - tankChance) {
            handler.addObject(new BasicEnemy(enemyX, enemyY, ID.BasicEnemy, handler));
        } else if (enemyType < 100 - shooterChance - fastChance - tankChance) {
            handler.addObject(new SmartEnemy(enemyX, enemyY, ID.SmartEnemy, handler));
        } else if (enemyType < 100 - fastChance - tankChance) {
            handler.addObject(new ShooterEnemy(enemyX, enemyY, ID.ShooterEnemy, handler));
        } else if (enemyType < 100 - tankChance) {
            handler.addObject(new FastEnemy(enemyX, enemyY, ID.FastEnemy, handler));
        } else {
            handler.addObject(new TankEnemy(enemyX, enemyY, ID.TankEnemy, handler));
        }

        enemiesKilledInWave++;
    }

    private boolean isWaveComplete() {
        // 检查是否所有敌人都被消灭
        int enemyCount = 0;
        for (GameObject obj : handler.objects) {
            if (obj.getId() == ID.BasicEnemy ||
                obj.getId() == ID.SmartEnemy ||
                obj.getId() == ID.ShooterEnemy ||
                obj.getId() == ID.FastEnemy ||
                obj.getId() == ID.TankEnemy ||
                obj.getId() == ID.BossEnemy) {
                enemyCount++;
            }
        }

        return enemyCount == 0 && enemiesKilledInWave >= enemiesInCurrentWave;
    }

    private void spawnPowerUp() {
        if (r.nextInt(100) < 30) { // 30% 概率生成道具
            int x = r.nextInt(WIDTH - 50) + 25;
            int y = r.nextInt(HEIGHT - 100) + 50;

            PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
            PowerUp.PowerUpType type = types[r.nextInt(types.length)];

            handler.addObject(new PowerUp(x, y, ID.PowerUp, type, handler));
        }
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 70.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
            }
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }
    private void tick() {
        if (gameState == GameState.GAME) {
            handler.tick();
            HUD.tick();

            // 波次系统逻辑
            if (waveInProgress) {
                // 生成敌人
                waveSpawnTimer++;
                if (waveSpawnTimer >= 60 && enemiesKilledInWave < enemiesInCurrentWave) { // 每秒生成一个敌人
                    spawnWaveEnemy();
                    waveSpawnTimer = 0;
                }

                // 检查波次是否完成
                if (isWaveComplete()) {
                    waveInProgress = false;
                    wavePauseTimer = 0;
                    currentWave++;
                    System.out.println("第 " + (currentWave - 1) + " 波完成！准备下一波...");
                }
            } else {
                // 波次间隔
                wavePauseTimer++;
                if (wavePauseTimer >= WAVE_PAUSE_DURATION) {
                    startNewWave();
                }
            }

            // 动态生成道具
            powerUpTimer++;
            if (powerUpTimer >= 900) { // 每15秒生成道具
                spawnPowerUp();
                powerUpTimer = 0;
            }
        }
    }


    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState == GameState.GAME) {
            // 绘制游戏画面
            renderStarField(g);
            handler.render(g);
            HUD.render(g);
        } else if (gameState == GameState.PAUSE) {
            // 绘制暂停界面
            renderStarField(g);
            handler.render(g);
            HUD.render(g);
            renderPauseOverlay(g);
        } else {
            // 绘制菜单画面
            menu.render(g);
        }

        g.dispose();
        bs.show();
    }

    private void renderStarField(Graphics g) {
        // 绘制渐变背景
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 10, 30),
                                                  0, HEIGHT, new Color(30, 10, 50));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制简单的静态星星
        g.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (i * 37) % WIDTH;
            int y = (i * 73) % HEIGHT;
            g.fillOval(x, y, 1, 1);
        }

        // 绘制更大的星星
        g.setColor(new Color(200, 200, 255));
        for (int i = 0; i < 20; i++) {
            int x = (i * 127) % WIDTH;
            int y = (i * 193) % HEIGHT;
            g.fillOval(x, y, 2, 2);
        }
    }

    private void renderPauseOverlay(Graphics g) {
        // 绘制半透明覆盖层
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 150)); // 半透明黑色
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制暂停文字
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String pauseText = "游戏暂停";
        int textWidth = fm.stringWidth(pauseText);
        int textX = (WIDTH - textWidth) / 2;
        int textY = HEIGHT / 2 - 50;
        g2d.drawString(pauseText, textX, textY);

        // 绘制提示文字
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g2d.getFontMetrics();
        String hintText = "按 ESC 键继续游戏";
        textWidth = fm.stringWidth(hintText);
        textX = (WIDTH - textWidth) / 2;
        textY = HEIGHT / 2 + 30;
        g2d.drawString(hintText, textX, textY);
    }

    public static int clamp(int var, int min, int max) {
        if (var >= max) {
            return var = max;
        } else if (var <= min) {
            return var = min;
        }
        else
            return var;
    }

    // 波次信息访问方法
    public static int getCurrentWave() {
        return instance != null ? instance.currentWave : 1;
    }

    public static boolean isWaveInProgress() {
        return instance != null ? instance.waveInProgress : false;
    }

    public static int getRemainingEnemies() {
        if (instance == null) return 0;
        int currentEnemies = 0;
        for (GameObject obj : instance.handler.objects) {
            if (obj.getId() == ID.BasicEnemy ||
                obj.getId() == ID.SmartEnemy ||
                obj.getId() == ID.ShooterEnemy ||
                obj.getId() == ID.FastEnemy ||
                obj.getId() == ID.TankEnemy ||
                obj.getId() == ID.BossEnemy) {
                currentEnemies++;
            }
        }
        return currentEnemies + (instance.enemiesInCurrentWave - instance.enemiesKilledInWave);
    }

    private static Game instance;

    public static void main(String[] args) {
        new Game();
    }

}
