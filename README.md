<img src="icons/banner.png" width="100%" height="100%" alt="Project Banner">

![Static Badge](https://img.shields.io/badge/1.20.1-information?style=for-the-badge&label=version&color=%2393002F)
![Static Badge](https://img.shields.io/badge/fabric-%2300CC00?style=for-the-badge)
![Static Badge](https://img.shields.io/badge/continued-%23C7007D?style=for-the-badge&label=status)

# Elemental Weapons

**A modification for Minecraft that adds powerful elemental weapons with unique passive and active abilities, using custom game mechanics.**

## 🌟 Features

### 🗡️ Elemental Weapon
|       Weapon       |                 Element                    |                    Passive ability                   |                                       Active ability                                       |
|:------------------:|:------------------------------------------:|:----------------------------------------------------:|:------------------------------------------------------------------------------------------:|
| The ancient sword  |                 🌿 Dendro                  |     Causes increased damage in the jungle biomes     |                     Creates blooms around the player, imposes effects                      |
|   Electric sword   |            ⚡ Electro + 🌊 Hydro            | Can summon lightning to a target with a small chance |           Summons a storm into the world, striking lightning at nearby entities            |
|     Fire sword     |                  🔥 Pyro                   |        With a chance, sets fire to the enemy         |                                   Summons a fire charge                                    |
|     Ice sword      | ❄️ Krio / With a chance, freezes the enemy |             Freezes all entities around              |                                Freezes all entities around                                 |
|  Hurricane Sword   |       🌪️ Anemo / Repels the target        |        Repels all entities around the player         |                           Repels all entities around the player                            |
|    Magic Sword     |                 ⚡ Electro                  |          Deals magical damage to the target          | Creates a magic circle, imposing negative effects on opponents and positive ones on allies |
|   Mountain Sword   |                    Geo                     |                Gives a defense bonus                 |        Summons an "earthquake" by throwing enemies up and placing spikes under them        |
|     Sea Sword      |                  🌊 Hydro                  |                Gives a swimming bonus                |                           Allows you to make a dash in the water                           |

### 🔮 Ability system
- **Passive abilities** - permanent bonuses
- **Active abilities** - it is activated by right-clicking

### ⚡ Custom mechanics
- Unique particles and visual effects
- Special sounds for each element
- Rollback system for balancing abilities

## 📦 Installation

### Requirements
- Minecraft 1.20.1
- Fabric Loader 0.17.2+
- Java 17+

## ⚙️ Configuration file

**The mod has a complete configuration system via the file** ```config/elementary-weapons.json```

### Basic settings
```json
"general": {
  "README": "Select entities affected by ability: (all, players, animals, monsters)",
  "entityScopes": [
    "all"
  ]
}
```

### Setting rollback on abilities
```json
"fireSword": {
    "activeCooldown": 45, // Rollback of active ability
    "passiveCooldown": 30, // Rollback of passive ability
    "passiveChance": 0.6, // Chance of activating a passive ability
    "xpPost": 16, // Experience spent to activate an active ability
    "durabilityCost": 2 // Weapon wear for passive ability
},
```