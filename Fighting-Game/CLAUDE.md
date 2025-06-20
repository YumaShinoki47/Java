# CLAUDE.md
必ず日本語で回答してください。
This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.


## Project Overview

This is a 2D fighting game built in Java using Swing. The game features two players battling on a single screen with basic attacks, special moves (projectiles), and animated sprites.

## Build and Run Commands

```bash
# Compile the game
javac *.java

# Run the game
java FightingGame2D
```

## Architecture

### Core Classes

- **FightingGame2D**: Main game class extending JPanel. Handles game loop (60FPS), input processing, rendering, and game state management. Contains the main() method and serves as the entry point.

- **Player**: Represents a player character with position, health, movement, attacks, and animation states. Handles sprite loading for Player 1 (image/ folder) and geometric rendering for Player 2.

- **SpecialMove**: Projectile class for special attacks. Handles movement, collision detection, visual effects, and different move types (fireball, energy_blast, shockwave).

### Game Mechanics

**Player Controls:**
- Player 1: A/D (move), W (jump), S (attack), Q (special)
- Player 2: Arrow keys (move/jump), Down (attack), / (special)

**State Systems:**
- Health system (100 HP each)
- Invulnerability frames (30 frames after taking damage)
- Attack cooldowns (30 frames for basic attacks, 36 frames for special attacks)
- Knockback system (20 frames of movement restriction)
- Automatic opponent-facing (players always face each other)

**Collision Detection:**
- Rectangle-based collision for both attacks and projectiles
- Ground collision at y=650 (GROUND_LEVEL)
- Screen boundary constraints

### Image Assets

Images are loaded from the `image/` directory:
- Player 1 sprites: player_1.jpg, walk_1_1.jpg through walk_1_4.jpg
- Background: background.jpg, background2.jpg, background3.jpg
- Automatic horizontal flipping for left-facing sprites (no separate left-facing images needed)

### Key Implementation Details

**Movement System:**
- Players stop immediately when movement keys are released (no momentum except during knockback)
- Walking animation only plays when on ground and moving
- Jump strength: -30, Gravity: 1.4, Move speed: 5

**Special Attacks:**
- Projectiles spawn from player's front edge based on facing direction
- Different visual effects per move type
- 600 pixel max range, 2-second lifetime
- Knockback prevents all player input during effect

**Rendering:**
- 1280x720 window resolution
- Custom geometric fallback rendering when images fail to load
- Frame-based animations with proper sprite cycling