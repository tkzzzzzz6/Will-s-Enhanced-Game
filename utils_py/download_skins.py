#!/usr/bin/env python3
"""
下载玩家皮肤图片
"""

import os
import urllib.request
from PIL import Image, ImageDraw
import io

def create_images_directory():
    """创建图片目录"""
    images_dir = "src/images"
    if not os.path.exists(images_dir):
        os.makedirs(images_dir)
    return images_dir

def create_player_skin(color, filename):
    """创建玩家皮肤图片"""
    # 创建48x48的图片
    img = Image.new('RGBA', (48, 48), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 绘制玩家形状（简单的飞船形状）
    # 主体
    draw.ellipse([8, 12, 40, 36], fill=color, outline=(255, 255, 255, 255))
    
    # 驾驶舱
    draw.ellipse([16, 16, 32, 32], fill=(100, 100, 100, 255), outline=(255, 255, 255, 255))
    
    # 引擎
    draw.rectangle([20, 36, 28, 44], fill=(255, 100, 100, 255))
    
    # 翅膀
    draw.polygon([(8, 20), (4, 28), (8, 32)], fill=color)
    draw.polygon([(40, 20), (44, 28), (40, 32)], fill=color)
    
    return img

def create_enemy_image():
    """创建敌人图片"""
    img = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 绘制敌人形状（三角形）
    draw.polygon([(16, 4), (4, 28), (28, 28)], fill=(255, 50, 50, 255), outline=(255, 255, 255, 255))
    draw.polygon([(16, 8), (8, 24), (24, 24)], fill=(200, 0, 0, 255))
    
    return img

def create_powerup_images():
    """创建道具图片"""
    powerups = {
        'health': (0, 255, 0, 255),      # 绿色
        'shield': (0, 255, 255, 255),    # 青色
        'speed': (255, 255, 0, 255),     # 黄色
        'weapon': (255, 165, 0, 255),    # 橙色
        'score': (255, 0, 255, 255)      # 紫色
    }
    
    images = {}
    for name, color in powerups.items():
        img = Image.new('RGBA', (24, 24), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # 绘制道具形状（六边形）
        points = []
        import math
        for i in range(6):
            angle = i * math.pi / 3
            x = 12 + 8 * math.cos(angle)
            y = 12 + 8 * math.sin(angle)
            points.append((x, y))
        
        draw.polygon(points, fill=color, outline=(255, 255, 255, 255))
        
        # 添加符号
        symbols = {
            'health': '+',
            'shield': 'S',
            'speed': '>',
            'weapon': 'W',
            'score': '*'
        }
        
        # 这里简化，不绘制文字符号，因为PIL的文字渲染比较复杂
        images[name] = img
    
    return images

def create_background_image():
    """创建背景图片"""
    img = Image.new('RGB', (640, 480), (10, 10, 30))
    draw = ImageDraw.Draw(img)
    
    # 绘制星星
    import random
    random.seed(42)  # 固定种子以获得一致的结果
    
    for _ in range(200):
        x = random.randint(0, 639)
        y = random.randint(0, 479)
        brightness = random.randint(100, 255)
        size = random.randint(1, 3)
        
        color = (brightness, brightness, brightness)
        draw.ellipse([x, y, x + size, y + size], fill=color)
    
    return img

def main():
    """主函数"""
    print("正在创建游戏图片资源...")
    
    # 创建图片目录
    images_dir = create_images_directory()
    
    try:
        # 创建玩家皮肤
        player_skins = {
            'player.png': (100, 100, 255, 255),      # 默认蓝色
            'player_blue.png': (0, 100, 255, 255),   # 深蓝色
            'player_red.png': (255, 100, 100, 255)   # 红色
        }
        
        for filename, color in player_skins.items():
            img = create_player_skin(color, filename)
            filepath = os.path.join(images_dir, filename)
            img.save(filepath, 'PNG')
            print(f"已创建: {filepath}")
        
        # 创建敌人图片
        enemy_img = create_enemy_image()
        enemy_path = os.path.join(images_dir, 'enemy.png')
        enemy_img.save(enemy_path, 'PNG')
        print(f"已创建: {enemy_path}")
        
        # 创建道具图片
        powerup_images = create_powerup_images()
        for name, img in powerup_images.items():
            filepath = os.path.join(images_dir, f'powerup_{name}.png')
            img.save(filepath, 'PNG')
            print(f"已创建: {filepath}")
        
        # 创建背景图片
        bg_img = create_background_image()
        bg_path = os.path.join(images_dir, 'background.png')
        bg_img.save(bg_path, 'PNG')
        print(f"已创建: {bg_path}")
        
        print("所有图片资源创建完成！")
        print("\n图片说明:")
        print("- player.png: 默认玩家皮肤")
        print("- player_blue.png: 蓝色玩家皮肤")
        print("- player_red.png: 红色玩家皮肤")
        print("- enemy.png: 敌人图片")
        print("- powerup_*.png: 各种道具图片")
        print("- background.png: 背景图片")
        
    except ImportError:
        print("错误: 需要安装PIL库")
        print("请运行: pip install Pillow")
        
        # 创建简单的占位符文件
        print("创建占位符文件...")
        placeholder_files = [
            'player.png', 'player_blue.png', 'player_red.png',
            'enemy.png', 'background.png',
            'powerup_health.png', 'powerup_shield.png', 'powerup_speed.png',
            'powerup_weapon.png', 'powerup_score.png'
        ]
        
        for filename in placeholder_files:
            filepath = os.path.join(images_dir, filename)
            with open(filepath, 'w') as f:
                f.write(f"# 占位符文件: {filename}\n")
            print(f"已创建占位符: {filepath}")

if __name__ == "__main__":
    main()
