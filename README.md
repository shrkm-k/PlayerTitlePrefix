# PlayerTitlePrefix

> A Paper plugin that allows players to earn, manage, and display titles before their names.

---

![The image below shows an example of how titles are displayed .](https://cdn.modrinth.com/data/cached_images/4e47cbfe9857a83cd32581d43b4e9a274eb67902.png)

## ✨ Features

### 🏷 Title System
- Display a **title before the player's name**
- Players can **freely select** from the titles they own
- Titles are defined in `config.yml`

### 🔑 Permission-based Titles
- Each title can have its own **permission**
- Suitable for:
    - Quest completion
    - Item acquisition
    - Achievements
    - Rank progression

### 🛡 Admin-only Titles
- Titles can be marked as **Admin-only**
- Separate control from normal permission-based titles

### 🧭 In-game Management
- **Add / remove titles directly in-game**
- No server restart required
- Includes a **clear and intuitive GUI**

### 📊 Collection Progress
- Players can see **how many titles they own**
- Displays the **percentage of collected titles**
- Encourages competition and long-term play

### 💾 Player Data Storage
- Data for **all players is automatically saved**
- Stored per-player in the plugin’s `data` folder
- Safe and persistent across restarts

---

## 📦 Installation
1. Download the plugin jar
2. Place it into your server's `plugins` folder
3. Start or restart the server

---

## ⚙ Configuration (`config.yml`)
Example:
```yml
Titles:
  Beginner:
    name: "Beginner"
    permission: "none"
    color: yellow

  Admin:
    name: "Admin"
    permission: "PlayerTitlePrefix.admin"
    color: red
    isAdmin: true
```

- `name`: Display name of the title
- `permission`: Permission required to unlock the title
- `color`: Display color of the title
- `isAdmin`: Marks the title as admin-only
- Titles can be freely added or removed

---

## 📜 Commands
| Command                              | Permission | Description                                               |
| ------------------------------------ | ---------- | --------------------------------------------------------- |
| `/playertitleprefix reload`          | OP         | Reloads the config and language files                     |
| `/playertitleprefix config`          | OP         | Opens a GUI to add or remove titles in-game               |
| `/playertitleprefix lang <language>` | OP         | Changes the plugin language in-game (Default: `en`, `ja`) |
| `/playertitleprefix change`          |    | Opens a GUI to view, equip, or unequip titles             |

## 🔹 Alias

- `/playertitleprefix` can also be used as `/ptp`

---

## 🎮 In-game GUI
- Easy-to-use GUI for:
    - Selecting titles
    - Managing available titles
- Designed to be understandable even for new players

---

## 🧩 Requirements
- Paper 1.21+ or Purpur 1.21+
- Java 21+

---

## ⚠ Notes
- This is a personal project
- No guarantee of support or future updates
- Use at your own risk

---

## 📄 License
MIT License

