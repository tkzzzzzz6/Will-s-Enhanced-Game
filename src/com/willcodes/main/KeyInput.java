package com.willcodes.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    private final Handler handler;

    public KeyInput(Handler handler) {
        this.handler = handler;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 暂停功能 - P键
        if (key == KeyEvent.VK_P) {
            if (Game.gameState == GameState.GAME) {
                Game.setGameState(GameState.PAUSE);
            } else if (Game.gameState == GameState.PAUSE) {
                Game.setGameState(GameState.GAME);
            }
            return;
        }

        // ESC键处理
        if (key == KeyEvent.VK_ESCAPE) {
            if (Game.gameState == GameState.PAUSE) {
                Game.setGameState(GameState.GAME);
            } else if (Game.gameState == GameState.GAME) {
                Game.setGameState(GameState.PAUSE);
            } else {
                System.exit(0);
            }
            return;
        }

        // 音效切换
        if (key == KeyEvent.VK_M) {
            AudioPlayer.toggleSound();
            return;
        }

        // 只有在游戏状态下才处理玩家控制
        if (Game.gameState != GameState.GAME) {
            return;
        }

        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);

            if(tempObject.getId() == ID.Player) {
                Player player = (Player) tempObject;
                // WASD移动
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) tempObject.setVelY(-5);
                if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) tempObject.setVelY(5);
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) tempObject.setVelX(-5);
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) tempObject.setVelX(5);
                if (key == KeyEvent.VK_SPACE) player.shoot(); // 空格键射击
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);


            if (tempObject.getId() == ID.Player) {
                if (key == KeyEvent.VK_W) tempObject.setVelY(0);
                if (key == KeyEvent.VK_S) tempObject.setVelY(0);
                if (key == KeyEvent.VK_A) tempObject.setVelX(0);
                if (key == KeyEvent.VK_D) tempObject.setVelX(0);
            }
        }
    }
}
