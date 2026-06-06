# 2D Platformer Game - Guide d'exécution

Ce document explique la manière exacte d'exécuter le projet de jeu 2D Platformer développé en Java avec JavaFX.

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
anewone_789final/
├── src/                    # Code source Java
│   ├── Main.java           # Point d'entrée du programme
│   └── com/platformer/     # Packages du jeu
├── ressources/             # Ressources du jeu (images, sons)
│   ├── *.png, *.jpg        # Images (sprites, backgrounds, etc.)
│   └── audio/              # Fichiers audio (sons, musiques)
├── bin/                    # Fichiers compilés (.class) et ressources
│   ├── com/                # Classes compilées
│   ├── *.png, *.jpg        # Images copiées
│   └── audio/              # Fichiers audio copiés
└── README.md               # Ce fichier
```
> **IMPORTANT :** Les fichiers audio DOIVENT être accessibles depuis le classpath. Les fichiers images sont chargés depuis le dossier `ressources/` avec des chemins absolus (`file:ressources/...`).

---

## 3. Configuration des Ressources Audio

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
  - Sélectionnez le dossier `anewone_789final` et cliquez sur `Finish`.
* **ÉTAPE 2 : Configurer JavaFX (si nécessaire)**
  - Clic droit sur le projet > `Properties` > `Java Build Path` > `Libraries` > `Modulepath` ou `Classpath`.
  - `Add External JARs...` > Sélectionnez les JARs JavaFX (`javafx-controls.jar`, `javafx-graphics.jar`, etc.).
* **ÉTAPE 3 : Copier les ressources audio**
  - Assurez-vous que le dossier `bin/audio/` contient tous les fichiers audio.
* **ÉTAPE 4 : Exécuter**
  - Ouvrez le fichier `src/Main.java`
  - Clic droit sur `Main.java` > `Run As` > `Java Application`.

---

## 5. Exécution en Ligne de Commande

### ÉTAPE 1 : Compiler le projet
Ouvrez votre terminal dans le dossier du projet.

**Windows (CMD/PowerShell) :**
```cmd
cd "chemin\vers\anewone_789final"
javac -d bin --module-path "C:\chemin\vers\javafx-sdk-XX\lib" --add-modules javafx.controls,javafx.media -cp bin src/Main.java src/com/platformer/**/*.java

### ÉTAPE 2 : Copier les ressources
Ouvrez votre terminal dans le dossier du projet.

**Windows (CMD/PowerShell) :**
```Windows :
DOS

xcopy /E /I ressources bin

ÉTAPE 3 : Exécuter le programme

Windows :
DOS
java --module-path "C:\chemin\vers\javafx-sdk-XX\lib" --add-modules javafx.controls,javafx.media -cp bin Main
## 6. Résolution des Erreurs (Dépannage)

* **Exception `IllegalAccessError – module javafx.base does not export com.sun.javafx`** Ajoutez cet argument VM lors de l'exécution :  
  `--module-path "C:\path\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media --enable-native-access=javafx.graphics`
* **"Error: JavaFX runtime components are missing"** : Vérifiez que le `--module-path` pointe vers le bon dossier `lib` de JavaFX.
* **Le jeu démarre mais les contrôles ne fonctionnent pas** : Cliquez sur la fenêtre du jeu pour lui donner le focus clavier.

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

