#!/usr/bin/env python3
"""
简单的音效生成器 - 不依赖外部库
"""

import wave
import math
import os

def create_audio_directory():
    """创建音频目录"""
    audio_dir = "src/audio"
    if not os.path.exists(audio_dir):
        os.makedirs(audio_dir)
    return audio_dir

def generate_tone(frequency, duration, sample_rate=22050, amplitude=0.3):
    """生成纯音调"""
    frames = int(duration * sample_rate)
    wave_data = []
    
    for i in range(frames):
        t = i / sample_rate
        value = amplitude * math.sin(2 * math.pi * frequency * t)
        # 转换为16位整数
        sample = int(value * 32767)
        # 确保在范围内
        sample = max(-32768, min(32767, sample))
        wave_data.append(sample)
    
    return wave_data

def apply_fade(wave_data, fade_in=0.1, fade_out=0.1):
    """应用淡入淡出效果"""
    length = len(wave_data)
    fade_in_samples = int(length * fade_in)
    fade_out_samples = int(length * fade_out)
    
    # 淡入
    for i in range(fade_in_samples):
        factor = i / fade_in_samples
        wave_data[i] = int(wave_data[i] * factor)
    
    # 淡出
    for i in range(fade_out_samples):
        idx = length - fade_out_samples + i
        factor = 1.0 - (i / fade_out_samples)
        wave_data[idx] = int(wave_data[idx] * factor)
    
    return wave_data

def save_wav(filename, wave_data, sample_rate=22050):
    """保存WAV文件"""
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # 单声道
        wav_file.setsampwidth(2)  # 16位
        wav_file.setframerate(sample_rate)
        
        # 转换为字节
        for sample in wave_data:
            wav_file.writeframes(sample.to_bytes(2, byteorder='little', signed=True))

def create_shoot_sound():
    """创建射击音效"""
    duration = 0.1
    sample_rate = 22050
    frames = int(duration * sample_rate)
    wave_data = []
    
    for i in range(frames):
        t = i / sample_rate
        # 频率从800Hz下降到200Hz
        freq = 800 * math.exp(-t * 8)
        value = 0.3 * math.sin(2 * math.pi * freq * t)
        # 添加快速衰减
        value *= math.exp(-t * 10)
        sample = int(value * 32767)
        sample = max(-32768, min(32767, sample))
        wave_data.append(sample)
    
    return wave_data

def create_explosion_sound():
    """创建爆炸音效"""
    duration = 0.5
    sample_rate = 22050
    frames = int(duration * sample_rate)
    wave_data = []
    
    for i in range(frames):
        t = i / sample_rate
        # 低频轰鸣
        low_freq = 0.2 * math.sin(2 * math.pi * 60 * t)
        # 中频噪声模拟
        mid_freq = 0.1 * math.sin(2 * math.pi * 150 * t * (1 + 0.5 * math.sin(t * 100)))
        # 高频噪声
        high_freq = 0.05 * math.sin(2 * math.pi * 800 * t * (1 + math.sin(t * 200)))
        
        value = low_freq + mid_freq + high_freq
        # 爆炸式衰减
        value *= math.exp(-t * 3)
        
        sample = int(value * 32767)
        sample = max(-32768, min(32767, sample))
        wave_data.append(sample)
    
    return wave_data

def create_hit_sound():
    """创建撞击音效"""
    duration = 0.15
    sample_rate = 22050
    frames = int(duration * sample_rate)
    wave_data = []
    
    for i in range(frames):
        t = i / sample_rate
        # 撞击声
        value = 0.4 * math.sin(2 * math.pi * 200 * t)
        # 快速衰减
        value *= math.exp(-t * 15)
        
        sample = int(value * 32767)
        sample = max(-32768, min(32767, sample))
        wave_data.append(sample)
    
    return wave_data

def create_powerup_sound():
    """创建道具收集音效"""
    duration = 0.4
    sample_rate = 22050
    frames = int(duration * sample_rate)
    wave_data = []
    
    for i in range(frames):
        t = i / sample_rate
        # 上升音调
        freq = 400 + 600 * t / duration
        value = 0.3 * math.sin(2 * math.pi * freq * t)
        # 添加和声
        value += 0.15 * math.sin(2 * math.pi * freq * 1.5 * t)
        
        sample = int(value * 32767)
        sample = max(-32768, min(32767, sample))
        wave_data.append(sample)
    
    return apply_fade(wave_data, 0.1, 0.1)

def create_simple_background():
    """创建简单的背景音乐"""
    duration = 8.0
    sample_rate = 22050
    frames = int(duration * sample_rate)
    wave_data = []
    
    # 简单的C大调和弦进行
    chord_notes = [261, 329, 392]  # C, E, G
    
    for i in range(frames):
        t = i / sample_rate
        value = 0
        
        # 添加和弦音符
        for freq in chord_notes:
            value += 0.1 * math.sin(2 * math.pi * freq * t)
        
        # 添加低音
        value += 0.15 * math.sin(2 * math.pi * 130 * t)  # C3
        
        sample = int(value * 32767)
        sample = max(-32768, min(32767, sample))
        wave_data.append(sample)
    
    return apply_fade(wave_data, 0.2, 0.2)

def main():
    """主函数"""
    print("正在创建游戏音效文件...")
    
    # 创建音频目录
    audio_dir = create_audio_directory()
    
    # 创建音效
    sounds = {
        'shoot.wav': create_shoot_sound(),
        'explosion.wav': create_explosion_sound(),
        'hit.wav': create_hit_sound(),
        'powerup.wav': create_powerup_sound(),
        'background.wav': create_simple_background(),
    }
    
    # 为其他音效创建简单版本
    sounds['enemy_shoot.wav'] = create_shoot_sound()  # 重用射击音效
    sounds['levelup.wav'] = create_powerup_sound()    # 重用道具音效
    sounds['gameover.wav'] = create_explosion_sound() # 重用爆炸音效
    
    # 保存所有音效文件
    for filename, wave_data in sounds.items():
        filepath = os.path.join(audio_dir, filename)
        save_wav(filepath, wave_data)
        print(f"已创建: {filepath}")
    
    print("所有音效文件创建完成！")

if __name__ == "__main__":
    main()
