# 2D Pixel-Art Platformer Game

Bienvenue dans le dépôt officiel de notre projet de jeu vidéo 2D développé en Java et JavaFX. Ce projet met en œuvre une architecture robuste basée sur plusieurs patrons de conception (design patterns) afin de séparer proprement la logique du jeu, la gestion des entités et l'interface utilisateur.
---

## 1. Prérequis

Pour exécuter ce projet, vous devez avoir installé :
1. **Java JDK 11 ou supérieur** (JDK 17 recommandé)
   - Vérifiez avec : `java -version`
2. **JavaFX SDK** (version compatible avec votre JDK)
   - JavaFX 17 recommandé si vous utilisez JDK 17
   - **IMPORTANT :** Si votre version de Java (JDK 17+) inclut JavaFX, vous n'avez pas besoin de télécharger JavaFX séparément.
3. *(Optionnel)* **Eclipse IDE** pour le développement.

---

## 2. Structure du Projet

```text
anewone/
│
├── src/                                   # Répertoire du code source
│   ├── Main.java                          # Point d'entrée de l'application
│   │
│   └── com/
│       └── platformer/                    # Package principal du jeu
│           │
│           ├── core/                      # Systèmes fondamentaux du moteur de jeu
│           │   ├── BackgroundManager.java # Gestion du défilement de l'arrière-plan (parallax)
│           │   ├── Camera.java            # Système de caméra de suivi du joueur
│           │   ├── GameManager.java       # Boucle de jeu principale (Game Loop) et états
│           │   ├── GameState.java         # Énumération des états du jeu (MENU, LEVEL_1, etc.)
│           │   └── Player.java            # Entité Joueur (physique, mouvements, animations, vie)
│           │
│           ├── entities/                  # Entités du monde de jeu
│           │   ├── FantasyGate.java       # Portail de fin de niveau
│           │   │
│           │   ├── enemies/               # Gestion des intelligences artificielles ennemies
│           │   │   ├── BugEnemy.java      # Ennemi de type "Insecte" pour les niveaux de plateforme
│           │   │   └── Monster.java       # Ennemi de type "Monstre" pour les phases de combat
│           │   │
│           │   ├── items/                 # Objets collectables et projectiles
│           │   │   ├── CrystalProjectile.java  # Projectile d'arme (Cristal lancé)
│           │   │   └── CrystalReward.java      # Cristal collectable pour le score/récompense
│           │   │
│           │   └── platforms/             # Éléments du décor et plateformes
│           │       ├── FlyingPlatform.java # Plateformes mobiles ou suspendues
│           │       ├── Obstacle.java       # Obstacles occasionnant des dégâts
│           │       └── Platform.java       # Plateformes de sol de base
│           │
│           ├── levels/                    # Système modulaire de gestion des niveaux
│           │   ├── LevelManager.java       # Interface commune pour les gestionnaires de niveau
│           │   ├── LevelData.java          # Structure de données de configuration des niveaux
│           │   ├── LevelConfig.java        # Fabrique statique des configurations de données
│           │   ├── LevelFactory.java       # Fabrique pour instancier les Level Managers
│           │   │
│           │   ├── platformer/             # Implémentation des niveaux de Plateforme
│           │   │   └── PlatformerLevelManager.java
│           │   │
│           │   └── combat/                 # Implémentation des niveaux de Combat (Arènes)
│           │       └── CombatLevelManager.java
│           │
│           └── ui/                        # Composants de l'Interface Utilisateur (HUD & Menus)
│               ├── HealthBar.java         # Barre de santé visuelle
│               ├── Heart.java             # Indicateur de points de vie sous forme de cœurs
│               └── HomeScreen.java        # Écran d'accueil et menu principal
│
├── ressources/                            # Actifs du jeu (Sprites, Arrière-plans, UI)
│   ├── back.jpg                           # Fond d'écran du mode combat
│   ├── background.png                     # Fond d'écran du mode plateforme
│   ├── bug_enemy.png / bug1.png...        # Sprites et frames d'animation de l'insecte
│   ├── Crystal_Reward.png                 # Sprite du cristal à collecter
│   ├── fantasy_gate.png                   # Sprite du portail magique
│   ├── flying_platformm.png               # Texture de la plateforme volante
│   ├── GAME_IS_OVER.png                   # Écran visuel de défaite
│   ├── heart.png                          # Visuel des cœurs de vie
│   ├── home_screen.jpg                    # Image de fond du menu principal
│   ├── monster.png                        # Sprite de l'ennemi de combat
│   ├── obstacle.png                       # Visuel des pièges/obstacles
│   ├── Plateforme.png / ...eer4.png       # Textures des sols et plateformes
│   ├── player.png / player_jump_...       # Sprites d'états et animations du joueur
│   ├── start_button.png                   # Bouton de lancement du menu
│   └── the_message.png                    # Bannière de transition textuelle
│
└── bin/                                   # Fichiers compilés (.class auto-générés)
```
> **IMPORTANT :** Les fichiers audio DOIVENT être accessibles depuis le classpath. Les fichiers images sont chargés depuis le dossier `ressources/` avec des chemins absolus (`file:ressources/...`).

**Package Organization**

 **com.platformer.core **

Core game systems and managers:

    GameManager: Main game loop, state management, input handling

    Player: Player entity with physics, animations, health system

    Camera: Camera system for following player

    BackgroundManager: Background scrolling and management

    GameState: Enum defining game states (MENU, LEVEL_1, COMBAT_LEVEL, etc.)

 **com.platformer.entities **

All game entities organized by type:

    enemies/: Enemy entities (BugEnemy, Monster)

    items/: Collectibles and projectiles (CrystalReward, CrystalProjectile)

    platforms/: Platform entities (Platform, FlyingPlatform, Obstacle)

    FantasyGate: Special entity for level completion

 **com.platformer.levels **

Level management system:

    LevelManager: Interface for level implementations

    LevelData: Configuration data structure for levels

    LevelConfig: Static factory for creating level configurations

    LevelFactory: Factory pattern for creating level managers

    platformer/: Platformer level implementation

    combat/: Combat level implementation

 **com.platformer.ui **

User interface components:

    HealthBar: Health display bar

    Heart: Individual heart/life indicator

    HomeScreen: Main menu screen
---
## 3. Architecture Globale

Le projet s'articule autour d'une boucle de jeu principale gérée par le GameManager via un AnimationTimer JavaFX. Cette architecture permet de synchroniser la physique du joueur, les mouvements des ennemis et la mise à jour graphique.
┌─────────────────────────────────────────────────────────┐
│                        Main.java                        │
│                (Application Entry Point)                │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                       GameManager                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │  • Boucle de jeu principale (AnimationTimer)     │  │
│  │  • Gestion fine des états (Enum GameState)       │  │
│  │  • Écoute et gestion des entrées Clavier/Souris  │  │
│  │  • Contrôle de l'UI (Barre de vie, menus HUD)    │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
         ▼                       ▼
┌──────────────────┐    ┌──────────────────────┐
│   LevelManager   │    │        Player        │
│   (Interface)    │    │  • Moteur physique   │
│                  │    │  • États & Animations│
│  ┌────────────┐  │    │  • Système de cœurs  │
│  │ Platformer │  │    │  • Mouvements/Sauts  │
│  │ Level      │  │    └──────────────────────┘
│  └────────────┘  │
│  ┌────────────┐  │
│  │ Combat     │  │
│  │ Level      │  │
│  └────────────┘  │
└──────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│                Entities                 │
│  • Ennemis (BugEnemy, Monster)          │
│  • Décors (Platform, FlyingPlatform)    │
│  • Objets (CrystalReward, Projectiles)  │
│  • Fin de niveau (FantasyGate)          │
└─────────────────────────────────────────┘
## 4. Configuration des Ressources Audio

Pour que les sons fonctionnent correctement, les fichiers audio doivent être copiés dans le dossier `bin/audio/` OU configurés dans le classpath.

Structure attendue pour les fichiers audio : `bin/audio/`
* `jump.wav` | `hit.mp3` | `coin.wav` | `music_theme.wav`
* `level1_to_level2.wav` | `game-over1.mp3` | `game-over2.mp3`
* `player_hit.mp3` | `monster_hit.mp3` | `button_click.mp3`

*Si les fichiers audio ne sont pas dans `bin/audio/`, copiez-les depuis `ressources/audio/` vers `bin/audio/`.*

---

## 4. Exécution avec Eclipse IDE

* **ÉTAPE 1 : Ouvrir le projet**
  - Lancez Eclipse IDE -> `File` > `Open Projects from File System...`
  - Sélectionnez le dossier `anewone` et cliquez sur `Finish`.
* **ÉTAPE 2 : Configurer JavaFX (si nécessaire)**
  - Clic droit sur le projet > `Properties` > `Java Build Path` > `Libraries` > `Modulepath` ou `Classpath`.
  - `Add External JARs...` > Sélectionnez les JARs JavaFX (`javafx-controls.jar`, `javafx-graphics.jar`, etc.).
* **ÉTAPE 3 : Copier les ressources audio** 
  - Assurez-vous que le dossier `bin/audio/` contient tous les fichiers audio.
* **ÉTAPE 4 : Exécuter**
  - Ouvrez le fichier `src/Main.java`
  - Clic droit sur `Main.java` > `Run As` > `Java Application`.

---

## 7. Contrôles du Jeu

### Menu Principal :
* Cliquez sur le bouton **START** pour commencer le jeu.

### Niveau Platformer (Niveau 1) :
* `FLECHE GAUCHE` / `A` : Déplacer à gauche
* `FLECHE DROITE` / `D` : Déplacer à droite
* `FLECHE HAUT` / `W` : Sauter
* `ESPACE` : Tirer un projectile de cristal

### Niveau Combat (Niveau 2) :
* Déplacements identiques + `ESPACE` pour Attaquer.

### Navigation :
* `ENTER` : Passer à l'écran suivant
* `ESPACE` : Redémarrer après un Game Over

---

## 8. Architecture du Projet

* **Main.java** : Crée la fenêtre JavaFX (960 x 500 pixels, 60 FPS) et initialise le `GameManager`.
* **GameManager** : Gère la boucle principale du jeu et les états (`MENU`, `LEVEL_1`, `COMBAT_LEVEL`).

---
### 🎮 BON JEU !

