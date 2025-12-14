# 🗡️ PacmanV

**PacmanV** is a 2D top-down action game built with **JavaFX**, featuring procedural map generation, monster AI with pathfinding, skill effects, and a dynamic level progression system.

The game focuses on **grid-based movement**, **real-time combat**, and **roguelike-style progression**.

---

## 🎮 Gameplay Overview

* Move the player on a tile-based map
* Fight monsters using mouse and keyboard skills
* Clear all monsters to spawn a portal
* Enter the portal to advance to the next level
* Game ends when player HP reaches 0

---

## 🧠 Core Features

### 🗺️ Procedural Map Generation

* Maps are generated dynamically each level
* Wall clusters create natural obstacles
* Flood Fill validation ensures the map is always fully playable
* Unreachable areas are automatically removed

### 👾 Monster AI

* Monsters spawn randomly (up to 10 per level)
* Idle movement within a limited range
* Switch between horizontal and vertical patrol
* Chase the player using **BFS pathfinding** when nearby
* Each monster has HP and dies only when HP reaches 0

### ⚔️ Combat System

* **Left Click**: Basic attack (slash effect)
* **Q Skill**: Area damage ability with cooldown
* **E Skill**: Temporary shield that protects the player for 5 seconds
* Visual skill effects rendered in real time

### 🧩 Level Progression

* Portal appears when all monsters are defeated
* Portal location is randomized
* Player progresses to a harder level with more monsters
* Monster HP and count scale with level

### 🎨 Graphics & Animation

* Tile-based rendering with camera tracking (10×10 view)
* Sprite sheet animation for:

  * Player idle animation
  * Monster idle animation
  * Gold animation
* Smooth frame-based animation using `AnimationTimer`

### 🧭 Camera System

* Camera follows the player
* Always renders a fixed 10×10 view window
* Prevents out-of-bounds rendering

### 🖥️ HUD (Heads-Up Display)

Displays real-time information:

* Player HP
* Gold count
* Current level
* Remaining monsters
* Skill cooldowns (Q / E)

---

## 🎮 Controls

| Input            | Action            |
| ---------------- | ----------------- |
| Arrow Keys       | Move player       |
| Mouse Left Click | Basic attack      |
| Q                | Area damage skill |
| E                | Activate shield   |
| Enter Portal     | Next level        |

---

## 🧱 Technical Stack

* **Language:** Java
* **Framework:** JavaFX
* **Rendering:** Canvas + GraphicsContext
* **Pathfinding:** BFS (Breadth-First Search)
* **Architecture:** MVC-style separation (core / ui / controller)

---

## 🧪 Algorithms Used

* **Flood Fill (DFS)** – map validation
* **Breadth-First Search (BFS)** – monster pathfinding
* **Procedural Generation** – dynamic map creation
* **Frame-based Animation** – sprite animation system

---

## 🚀 How to Run

1. Open the project in **IntelliJ IDEA**
2. Make sure JavaFX is properly configured
3. Run the main JavaFX application class
4. Enjoy the game 🎉

---

## 📌 Future Improvements

* Attack animations & hit effects
* Sound effects and background music
* Boss monsters
* Save/load system
* Multiple biomes (grass, snow, lava)
* Better enemy behavior states

---

## 👤 Author

Developed as a learning and game development project using JavaFX.
Perfect for understanding **game loops**, **AI**, and **procedural generation**.

---

## 🖼️ Assets & License

This project uses **free graphical assets** provided by **CraftPix**.

### Asset Source

* Website: [https://craftpix.net](https://craftpix.net)

### License

All visual assets (sprites, tiles, animations) are used under the
**CraftPix Free License**.

According to the CraftPix license:

* ✅ Assets may be used for **personal, educational, and commercial projects**
* ❌ Assets **may not be redistributed or resold** as standalone files
* ⚠️ **Attribution is required** when using free assets

Full license details:
[https://craftpix.net/file-licenses/](https://craftpix.net/file-licenses/)

### Attribution

Graphics used in this project were created by **CraftPix.net**
© CraftPix – [https://craftpix.net](https://craftpix.net)

---

## 📌 Notes

* This project is for **educational purposes**
* All assets remain the property of their original creators
* No asset files are redistributed outside this repository

---


