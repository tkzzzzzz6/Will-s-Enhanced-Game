package com.willcodes.main;

import java.awt.*;

public class PowerUp extends GameObject {
    private Handler handler;
    private PowerUpType type;
    private Color color;
    private int pulseTimer = 0;
    private float pulseScale = 1.0f;
    private int life = 600; // 10秒后消失
    
    public enum PowerUpType {
        HEALTH,     // 生命恢复
        SHIELD,     // 护盾
        SPEED,      // 速度提升
        WEAPON,     // 武器强化
        SCORE       // 分数加成
    }
    
    public PowerUp(int x, int y, ID id, PowerUpType type, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        this.type = type;
        
        // 根据道具类型设置颜色
        switch (type) {
            case HEALTH:
                color = new Color(0, 255, 0); // 绿色
                break;
            case SHIELD:
                color = new Color(0, 255, 255); // 青色
                break;
            case SPEED:
                color = new Color(255, 255, 0); // 黄色
                break;
            case WEAPON:
                color = new Color(255, 165, 0); // 橙色
                break;
            case SCORE:
                color = new Color(255, 0, 255); // 紫色
                break;
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, 24, 24);
    }
    
    public void tick() {
        // 脉冲效果
        pulseTimer++;
        pulseScale = 1.0f + 0.2f * (float) Math.sin(pulseTimer * 0.2f);
        
        // 缓慢旋转
        y += (int) (Math.sin(pulseTimer * 0.1f) * 0.5f);
        
        life--;
        if (life <= 0) {
            handler.removeObject(this);
        }
        
        // 检测与玩家的碰撞
        collision();
    }
    
    private void collision() {
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);
            
            if (tempObject.getId() == ID.Player && getBounds().intersects(tempObject.getBounds())) {
                Player player = (Player) tempObject;
                
                // 应用道具效果
                applyEffect(player);
                
                // 创建收集特效
                createCollectEffect();
                
                // 播放音效
                AudioPlayer.play("powerup");
                
                // 移除道具
                handler.removeObject(this);
                break;
            }
        }
    }
    
    private void applyEffect(Player player) {
        switch (type) {
            case HEALTH:
                HUD.HEALTH = Math.min(100, HUD.HEALTH + 25);
                break;
            case SHIELD:
                player.setShield(true);
                break;
            case SPEED:
                player.setSpeedBoost(true);
                break;
            case WEAPON:
                player.upgradeWeapon();
                break;
            case SCORE:
                HUD.score += 50;
                break;
        }
    }
    
    private void createCollectEffect() {
        // 创建收集粒子效果
        for (int i = 0; i < 12; i++) {
            int velX = (int) (Math.random() * 8 - 4);
            int velY = (int) (Math.random() * 8 - 4);
            handler.addObject(new Particle(x + 12, y + 12, ID.Particle, velX, velY, color, 20, handler));
        }
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 计算脉冲大小
        int size = (int) (24 * pulseScale);
        int offset = (24 - size) / 2;
        
        // 绘制外圈发光效果
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g.fillOval(x + offset - 4, y + offset - 4, size + 8, size + 8);
        
        // 绘制道具主体
        g.setColor(color);
        g.fillOval(x + offset, y + offset, size, size);
        
        // 绘制内部图标
        g.setColor(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        
        String symbol = getSymbol();
        int textX = x + 12 - fm.stringWidth(symbol) / 2;
        int textY = y + 12 + fm.getAscent() / 2 - 2;
        g.drawString(symbol, textX, textY);
        
        // 生命值警告效果
        if (life < 120) { // 最后2秒闪烁
            if ((life / 10) % 2 == 0) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillOval(x + offset, y + offset, size, size);
            }
        }
    }
    
    private String getSymbol() {
        switch (type) {
            case HEALTH: return "+";
            case SHIELD: return "S";
            case SPEED: return ">";
            case WEAPON: return "W";
            case SCORE: return "*";
            default: return "?";
        }
    }
    
    public PowerUpType getType() {
        return type;
    }
}
