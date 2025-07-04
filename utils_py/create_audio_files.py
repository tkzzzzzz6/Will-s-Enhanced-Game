#!/usr/bin/env python3
"""
简单的音效生成器
为游戏创建基本的WAV音效文件
"""

import numpy as np
import wave
import os

def create_audio_directory():
    """创建音频目录"""
    audio_dir = "src/audio"
    if not os.path.exists(audio_dir):
        os.makedirs(audio_dir)
    return audio_dir

def generate_tone(frequency, duration, sample_rate=44100, amplitude=0.3):
    """生成纯音调"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    wave_data = amplitude * np.sin(2 * np.pi * frequency * t)
    return wave_data

def generate_noise(duration, sample_rate=44100, amplitude=0.1):
    """生成白噪声"""
    samples = int(sample_rate * duration)
    noise = amplitude * np.random.normal(0, 1, samples)
    return noise

def apply_envelope(wave_data, attack=0.1, decay=0.1, sustain=0.7, release=0.2):
    """应用ADSR包络"""
    length = len(wave_data)
    envelope = np.ones(length)
    
    # Attack
    attack_samples = int(length * attack)
    envelope[:attack_samples] = np.linspace(0, 1, attack_samples)
    
    # Decay
    decay_samples = int(length * decay)
    decay_end = attack_samples + decay_samples
    envelope[attack_samples:decay_end] = np.linspace(1, sustain, decay_samples)
    
    # Release
    release_samples = int(length * release)
    release_start = length - release_samples
    envelope[release_start:] = np.linspace(sustain, 0, release_samples)
    
    return wave_data * envelope

def save_wav(filename, wave_data, sample_rate=44100):
    """保存WAV文件"""
    # 转换为16位整数
    wave_data = np.clip(wave_data, -1, 1)
    wave_data = (wave_data * 32767).astype(np.int16)
    
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # 单声道
        wav_file.setsampwidth(2)  # 16位
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave_data.tobytes())

def create_shoot_sound():
    """创建射击音效"""
    # 高频短促的音效
    base_freq = 800
    duration = 0.15
    
    # 创建频率下降的效果
    t = np.linspace(0, duration, int(44100 * duration), False)
    frequency_sweep = base_freq * np.exp(-t * 8)
    wave_data = 0.3 * np.sin(2 * np.pi * frequency_sweep * t)
    
    # 添加一些噪声
    noise = generate_noise(duration, amplitude=0.05)
    wave_data += noise
    
    # 应用快速衰减
    wave_data = apply_envelope(wave_data, attack=0.01, decay=0.1, sustain=0.3, release=0.89)
    
    return wave_data

def create_explosion_sound():
    """创建爆炸音效"""
    duration = 0.8
    
    # 低频轰鸣 + 高频噪声
    low_freq = generate_tone(60, duration, amplitude=0.2)
    mid_freq = generate_tone(150, duration, amplitude=0.15)
    noise = generate_noise(duration, amplitude=0.3)
    
    # 混合所有频率
    wave_data = low_freq + mid_freq + noise
    
    # 应用爆炸式包络
    wave_data = apply_envelope(wave_data, attack=0.01, decay=0.3, sustain=0.2, release=0.69)
    
    return wave_data

def create_hit_sound():
    """创建撞击音效"""
    duration = 0.2
    
    # 中频撞击声
    impact = generate_tone(200, duration, amplitude=0.4)
    noise = generate_noise(duration, amplitude=0.2)
    
    wave_data = impact + noise
    wave_data = apply_envelope(wave_data, attack=0.01, decay=0.2, sustain=0.1, release=0.79)
    
    return wave_data

def create_powerup_sound():
    """创建道具收集音效"""
    duration = 0.5
    
    # 上升的音调
    t = np.linspace(0, duration, int(44100 * duration), False)
    frequency_sweep = 400 + 600 * t / duration  # 从400Hz上升到1000Hz
    wave_data = 0.3 * np.sin(2 * np.pi * frequency_sweep * t)
    
    # 添加和声
    harmony = 0.15 * np.sin(2 * np.pi * frequency_sweep * 1.5 * t)
    wave_data += harmony
    
    wave_data = apply_envelope(wave_data, attack=0.1, decay=0.1, sustain=0.8, release=0.0)
    
    return wave_data

def create_levelup_sound():
    """创建升级音效"""
    duration = 1.0
    
    # 胜利的和弦进行
    notes = [523, 659, 784, 1047]  # C5, E5, G5, C6
    wave_data = np.zeros(int(44100 * duration))
    
    for i, freq in enumerate(notes):
        start_time = i * 0.2
        note_duration = 0.3
        if start_time + note_duration <= duration:
            start_sample = int(44100 * start_time)
            note_samples = int(44100 * note_duration)
            end_sample = start_sample + note_samples
            
            note_wave = generate_tone(freq, note_duration, amplitude=0.2)
            note_wave = apply_envelope(note_wave, attack=0.1, decay=0.1, sustain=0.7, release=0.1)
            
            if end_sample <= len(wave_data):
                wave_data[start_sample:end_sample] += note_wave
    
    return wave_data

def create_gameover_sound():
    """创建游戏结束音效"""
    duration = 1.5
    
    # 下降的音调表示失败
    t = np.linspace(0, duration, int(44100 * duration), False)
    frequency_sweep = 400 * np.exp(-t * 2)  # 从400Hz快速下降
    wave_data = 0.3 * np.sin(2 * np.pi * frequency_sweep * t)
    
    # 添加低频轰鸣
    low_rumble = generate_tone(80, duration, amplitude=0.1)
    wave_data += low_rumble
    
    wave_data = apply_envelope(wave_data, attack=0.1, decay=0.2, sustain=0.5, release=0.2)
    
    return wave_data

def create_enemy_shoot_sound():
    """创建敌人射击音效"""
    duration = 0.12
    
    # 比玩家射击更低沉的音效
    base_freq = 300
    t = np.linspace(0, duration, int(44100 * duration), False)
    frequency_sweep = base_freq * np.exp(-t * 6)
    wave_data = 0.25 * np.sin(2 * np.pi * frequency_sweep * t)
    
    # 添加更多噪声
    noise = generate_noise(duration, amplitude=0.08)
    wave_data += noise
    
    wave_data = apply_envelope(wave_data, attack=0.01, decay=0.15, sustain=0.2, release=0.84)
    
    return wave_data

def create_background_music():
    """创建简单的背景音乐"""
    duration = 10.0  # 10秒循环
    
    # 简单的和弦进行 (C-Am-F-G)
    chord_progression = [
        [261, 329, 392],  # C major
        [220, 261, 329],  # A minor  
        [174, 220, 261],  # F major
        [196, 246, 293],  # G major
    ]
    
    wave_data = np.zeros(int(44100 * duration))
    chord_duration = duration / 4
    
    for i, chord in enumerate(chord_progression):
        start_time = i * chord_duration
        start_sample = int(44100 * start_time)
        chord_samples = int(44100 * chord_duration)
        end_sample = start_sample + chord_samples
        
        # 为每个和弦创建音符
        chord_wave = np.zeros(chord_samples)
        for freq in chord:
            note = generate_tone(freq, chord_duration, amplitude=0.1)
            chord_wave += note
        
        # 应用柔和的包络
        chord_wave = apply_envelope(chord_wave, attack=0.1, decay=0.1, sustain=0.8, release=0.0)
        
        if end_sample <= len(wave_data):
            wave_data[start_sample:end_sample] = chord_wave
    
    return wave_data

def main():
    """主函数"""
    print("正在创建游戏音效文件...")
    
    # 创建音频目录
    audio_dir = create_audio_directory()
    
    # 创建所有音效
    sounds = {
        'shoot.wav': create_shoot_sound(),
        'explosion.wav': create_explosion_sound(),
        'hit.wav': create_hit_sound(),
        'powerup.wav': create_powerup_sound(),
        'levelup.wav': create_levelup_sound(),
        'gameover.wav': create_gameover_sound(),
        'enemy_shoot.wav': create_enemy_shoot_sound(),
        'background.wav': create_background_music(),
    }
    
    # 保存所有音效文件
    for filename, wave_data in sounds.items():
        filepath = os.path.join(audio_dir, filename)
        save_wav(filepath, wave_data)
        print(f"已创建: {filepath}")
    
    print("所有音效文件创建完成！")
    print("\n音效说明:")
    print("- shoot.wav: 玩家射击音效")
    print("- explosion.wav: 爆炸音效") 
    print("- hit.wav: 撞击音效")
    print("- powerup.wav: 道具收集音效")
    print("- levelup.wav: 升级音效")
    print("- gameover.wav: 游戏结束音效")
    print("- enemy_shoot.wav: 敌人射击音效")
    print("- background.wav: 背景音乐 (10秒循环)")

if __name__ == "__main__":
    main()
