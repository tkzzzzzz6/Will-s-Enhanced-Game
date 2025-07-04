package com.willcodes.main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioPlayer {
    private static Map<String, Clip> clips = new HashMap<>();
    private static boolean soundEnabled = true;
    
    public static void loadAudio() {
        try {
            // 加载背景音乐
            loadSound("background", "src/audio/background.wav");
            // 加载音效
            loadSound("hit", "src/audio/hit.wav");
            loadSound("powerup", "src/audio/powerup.wav");
            loadSound("gameover", "src/audio/gameover.wav");
            loadSound("shoot", "src/audio/shoot.wav");
            loadSound("explosion", "src/audio/explosion.wav");
            loadSound("enemyShoot", "src/audio/enemy_shoot.wav");
            loadSound("levelup", "src/audio/levelup.wav");
        } catch (Exception e) {
            System.err.println("音频加载失败: " + e.getMessage());
        }
    }
    
    private static void loadSound(String name, String path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clips.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("无法加载音频 " + path + ": " + e.getMessage());
        }
    }
    
    public static void play(String name) {
        if (!soundEnabled) return;
        
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public static void loop(String name) {
        if (!soundEnabled) return;
        
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public static void stop(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.stop();
        }
    }
    
    public static void toggleSound() {
        soundEnabled = !soundEnabled;
        if (!soundEnabled) {
            for (Clip clip : clips.values()) {
                clip.stop();
            }
        } else {
            loop("background");
        }
    }
}