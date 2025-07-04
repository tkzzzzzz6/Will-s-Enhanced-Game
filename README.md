# Will's Enhanced Game

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Game](https://img.shields.io/badge/Game-2D%20Shooter-blue.svg)](https://github.com/yourusername/wills-enhanced-game)

A 2D shooting game developed with Java Swing, featuring rich game mechanics, multiple enemy types, wave system, and audio support.

## Features

### Core Gameplay

- **Infinite Wave Challenge**: Continuous enemy waves with increasing difficulty
- **Multiple Enemy Types**: Basic, Smart, Shooter, Fast, Tank, and Splitter enemies
- **Power-up System**: Shield, speed boost, weapon upgrade, and more
- **Audio Support**: Complete background music and sound effects
- **Particle Effects**: Explosion, trail, and other visual effects

### Game Mechanics

- **Wave System**: Dynamic difficulty adjustment with increasing enemy count and types
- **Weapon Upgrade**: Weapon system from single shot to triple shot
- **Health System**: 1000 HP with shield protection
- **Score System**: Score points by defeating enemies with level progression
- **Difficulty Selection**: Easy, Normal, and Hard difficulty levels

### Visual Effects

- **Starfield Background**: Dynamic starfield background effect
- **Trail System**: Player movement trail effects
- **Explosion Particles**: Enemy death explosion effects
- **UI Interface**: Clear game status display

## Technical Architecture

### Project Structure

```
src/
├── com/willcodes/main/
│   ├── Game.java              # Main game class, game loop and state management
│   ├── Window.java            # Game window management
│   ├── GameObject.java        # Abstract base class for game objects
│   ├── Player.java            # Player class
│   ├── Handler.java           # Game object manager
│   ├── Menu.java              # Game menu system
│   ├── HUD.java               # Game interface display
│   ├── AudioPlayer.java       # Audio player
│   ├── ImageLoader.java       # Image resource loader
│   ├── KeyInput.java          # Keyboard input handling
│   ├── GameState.java         # Game state enumeration
│   ├── ID.java                # Game object type enumeration
│   ├── Bullet.java            # Bullet class
│   ├── PowerUp.java           # Power-up class
│   ├── Trail.java             # Trail effect class
│   ├── Particle.java          # Particle effect class
│   └── enemies/               # Enemy types
│       ├── BasicEnemy.java    # Basic enemy
│       ├── SmartEnemy.java    # Smart enemy
│       ├── ShooterEnemy.java  # Shooter enemy
│       ├── FastEnemy.java     # Fast enemy
│       ├── TankEnemy.java     # Tank enemy
│       └── SplitterEnemy.java # Splitter enemy
├── audio/                     # Audio resources
│   ├── background.wav         # Background music
│   ├── shoot.wav              # Shooting sound effect
│   ├── explosion.wav          # Explosion sound effect
│   └── ...
└── images/                    # Image resources
    ├── player.png             # Player image
    ├── enemy.png              # Enemy image
    └── ...
```

### Core Technologies

- **Java Swing**: Graphics interface and game rendering
- **Multi-threading**: Game loop and audio playback
- **Object-Oriented Design**: Clear class hierarchy structure
- **Event-Driven**: Keyboard and mouse event handling
- **Resource Management**: Image and audio resource loading

## Quick Start

### System Requirements

- Java 11 or higher
- System with audio playback support

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/wills-enhanced-game.git
   cd wills-enhanced-game
   ```
2. **Use build scripts (Recommended)**

   **Windows:**

   ```bash
   build.bat
   ```

   **Linux/Mac:**

   ```bash
   chmod +x build.sh
   ./build.sh
   ```
3. **Manual compilation (Optional)**

   ```bash
   # Create bin directory
   mkdir -p bin

   # Compile project
   javac -d bin -cp "src" src/com/willcodes/main/*.java

   # Run game
   java -cp bin com.willcodes.main.Game
   ```

### Game Controls

| Key       | Function       |
| --------- | -------------- |
| `WASD`  | Move player    |
| `Space` | Shoot          |
| `P`     | Pause/Resume   |
| `ESC`   | Return to menu |
| `M`     | Mute/Unmute    |

## Gameplay

### Basic Gameplay

1. **Start Game**: Select difficulty level to begin
2. **Control Player**: Use WASD keys to move, Space to shoot
3. **Defeat Enemies**: Eliminate all enemies to complete current wave
4. **Collect Power-ups**: Gather power-ups for special abilities
5. **Survival Challenge**: Survive as long as possible in infinite waves

### Wave System

- **Wave 1**: 4-6 basic enemies
- **Wave 5**: 12-14 enemies, smart enemies start appearing
- **Wave 10**: 22 enemies, many shooter and smart enemies
- **Wave 15+**: 20 enemies, highest difficulty mix

### Power-up System

- **Shield**: Temporary invincibility, immune to damage
- **Speed Boost**: Increased movement speed
- **Weapon Upgrade**: Increased bullet count and damage

## Contributing

We welcome all forms of contributions!

### Reporting Issues

If you find bugs or have feature suggestions, please [create an Issue](https://github.com/tkzzzzzz6/Will-s-Enhanced-Game/issues).

### Submitting Code

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java naming conventions
- Add appropriate comments
- Ensure code compiles and runs correctly
- Test new features

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to all developers who contributed to this project
- Thanks to the open source community for inspiration and support
- Special thanks to the Java Swing framework developers

## Contact

- Project Homepage: [https://github.com/tkzzzzzz6/Will-s-Enhanced-Game](https://github.com/tkzzzzzz6/Will-s-Enhanced-Game)
- Email: tk2535550189@gmail.com

---

If this project helps you, please give us a star!
