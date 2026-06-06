package com.platformer.core;

import com.platformer.levels.LevelManager;
import com.platformer.levels.LevelData;
import com.platformer.levels.LevelConfig;
import com.platformer.levels.LevelFactory;
import com.platformer.levels.platformer.PlatformerLevelManager;
import com.platformer.levels.combat.CombatLevelManager;
import com.platformer.ui.HealthBar;
import com.platformer.ui.Heart;
import com.platformer.ui.HomeScreen;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import com.platformer.audio.SoundManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameManager {

    private static final double GAME_WIDTH = 960;
    private static final double GAME_HEIGHT = 500;
    private static final double GRAVITY = 0.8;

    private Pane root;
    private Pane world;

    private Player player;
    private BackgroundManager backgroundManager;
    private Camera camera;
    private HealthBar healthBar;
    private List<Heart> hearts;
    private HomeScreen homeScreen;
    
    private GameState currentState = GameState.MENU; // Start with menu
    private LevelManager currentLevelManager;
    private int currentLevelId = 1;
    
    private ImageView messageImage;
    private ImageView nextLevelImage;
    private ImageView gameOverImage;

    private Set<KeyCode> pressedKeys = new HashSet<>();
    private AnimationTimer gameLoop;
    private Scene scene; // Store scene reference for combat level input handling
    public static SoundManager soundManager;
    private boolean transitionSoundPlayed = false;

    public GameManager() {
        root = new Pane();
        root.setPrefSize(GAME_WIDTH, GAME_HEIGHT);

        // Background
        backgroundManager = new BackgroundManager(GAME_WIDTH, GAME_HEIGHT);
        root.getChildren().add(backgroundManager.getView());

        // World (game objects)
        world = new Pane();
        world.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        root.getChildren().add(world);

        // Camera
        camera = new Camera(GAME_WIDTH, GAME_HEIGHT);

        // Create player
        player = new Player(0, 0); // Will be positioned by level manager

        // Initialize home screen (shown first)
        initializeHomeScreen();

        // UI: Health bar (top-left) - hidden initially
        healthBar = new HealthBar(20, 20, 150, 20, player.getMaxHealth());
        healthBar.update(player.getHealth());
        healthBar.getView().setVisible(false); // Hidden until game starts
        root.getChildren().add(healthBar.getView());
        
        // UI: Hearts (under health bar) - hidden initially
        initializeHearts();
        for (Heart heart : hearts) {
            heart.getView().setVisible(false); // Hidden until game starts
        }
        
        // Initialize message, next level, and game over images
        initializeMessageImage();
        initializeNextLevelImage();
        initializeGameOverImage();

     // Crée le SoundManager
        soundManager = new SoundManager();
        // Réduire le volume de la musique thème à 30%
        soundManager.setMusicVolume(0.3);
        // 🔹 Charger les SFX avec le chemin relatif depuis resources
        soundManager.loadSfx("jump", "/audio/jump.wav");
        soundManager.loadSfx("hit", "/audio/hit.mp3");
        soundManager.loadSfx("coin", "/audio/coin.wav");

        // 🔹 Charger la musique
        soundManager.loadMusic("theme", "/audio/music_theme.wav");
        // 🔊 SFX
        soundManager.loadSfx("level1_to_level2", "/audio/level1_to_level2.wav");
        soundManager.loadSfx("player_lose_soul", "/audio/player_lose_soul.mp3");
        soundManager.loadSfx("game-over1", "/audio/game-over1.mp3"); // victoire
        soundManager.loadSfx("game-over2", "/audio/game-over2.mp3"); // défaite
        soundManager.loadSfx("player_hit", "/audio/player_hit.mp3");
        soundManager.loadSfx("monster_hit", "/audio/monster_hit.mp3");
        soundManager.loadSfx("button_click", "/audio/button_click.mp3");

        // 🔹 Jouer le thème
        soundManager.playMusic("theme", true);
        startGameLoop();
        
    }
    
    private void initializeHomeScreen() {
        homeScreen = new HomeScreen(e -> {
            // 🔊 Jouer le son du clic du bouton
            if (soundManager != null) {
                soundManager.playOneShot("button_click");
            }
            startGame();
        });
        root.getChildren().add(homeScreen.getView());
        homeScreen.getView().toFront();
    }

    private void startGame() {
        // Hide home screen
        homeScreen.setVisible(false);
        
        // Show game UI
        healthBar.getView().setVisible(true);
        for (Heart heart : hearts) {
            heart.getView().setVisible(true);
        }
        
        // Change state to LEVEL_1
        currentState = GameState.LEVEL_1;
        
        // Load and initialize level 1
        LevelData level1Data = LevelConfig.getLevelData(1);
        currentLevelManager = LevelFactory.createLevelManager(level1Data, world, player, camera, backgroundManager);
        currentLevelManager.initialize();
        soundManager.playMusic("theme", true);
        System.out.println("Game started!");
    }


    public void setupInputHandlers(Scene scene) {
        this.scene = scene; // Store scene reference
        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            if (e.getCode() == KeyCode.SPACE) {
                handleSpaceKey();
            }
            if (e.getCode() == KeyCode.ENTER) {
                handleEnterKey();
            }
        });
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
        root.setFocusTraversable(true);
    }
    
    private void handleSpaceKey() {
        switch (currentState) {
            case GAME_OVER -> {
                restartGame();
            }
            case LEVEL_TRANSITION -> {
                // 🔹 Jouer le son de transition une seule fois
                if (!transitionSoundPlayed) {
                    transitionSoundPlayed = true;
                    soundManager.stopMusic(); // stop le thème avant la transition
                    soundManager.playSfx("level1_to_level2");
                }
                // Transition vers le prochain niveau
                transitionToNextLevel();
            }
            case LEVEL_1 -> {
                if (currentLevelManager instanceof PlatformerLevelManager) {
                    ((PlatformerLevelManager) currentLevelManager).shootCrystal();
                }
            }
            case COMBAT_LEVEL -> {
                if (currentLevelManager instanceof CombatLevelManager) {
                    ((CombatLevelManager) currentLevelManager).playerAttack();
                }
            }
        }
    }

    
    private void initializeMessageImage() {
        try {
            Image messageImg = new Image("file:ressources/the_message.png");
            messageImage = new ImageView(messageImg);
            messageImage.setFitWidth(GAME_WIDTH);
            messageImage.setFitHeight(GAME_HEIGHT);
            messageImage.setPreserveRatio(true);
            messageImage.setVisible(false);
            messageImage.setX(0);
            messageImage.setY(0);
            // Add to root and bring to front
            root.getChildren().add(messageImage);
            messageImage.toFront();
        } catch (Exception e) {
            System.err.println("Could not load the_message.png: " + e.getMessage());
            messageImage = new ImageView();
            messageImage.setVisible(false);
            root.getChildren().add(messageImage);
        }
    }
    
    private void initializeNextLevelImage() {
        try {
            Image nextLevelImg = new Image("file:ressources/back.jpg");
            nextLevelImage = new ImageView(nextLevelImg);
            nextLevelImage.setFitWidth(GAME_WIDTH);
            nextLevelImage.setFitHeight(GAME_HEIGHT);
            nextLevelImage.setPreserveRatio(true);
            nextLevelImage.setVisible(false);
            nextLevelImage.setX(0);
            nextLevelImage.setY(0);
            // Add to root and bring to front
            root.getChildren().add(nextLevelImage);
            nextLevelImage.toFront();
        } catch (Exception e) {
            System.err.println("Could not load back.jpg: " + e.getMessage());
            nextLevelImage = new ImageView();
            nextLevelImage.setVisible(false);
            root.getChildren().add(nextLevelImage);
        }
    }
    
    private void handleEnterKey() {
        if (currentState == GameState.LEVEL_1 && currentLevelManager != null && currentLevelManager.isComplete()) {
            // Show next level preview
            showNextLevelPreview();
        }
    }
    
    private void showLevelCompleteMessage() {
        currentState = GameState.LEVEL_TRANSITION;
        if (messageImage != null) {
            messageImage.setVisible(true);
            root.getChildren().remove(messageImage);
            root.getChildren().add(messageImage);
            messageImage.toFront();
        }
        System.out.println("Level complete! Press ENTER to continue to next level.");
    }
    
    private void showNextLevelPreview() {
        if (messageImage != null) {
            messageImage.setVisible(false);
        }
        if (nextLevelImage != null) {
            nextLevelImage.setVisible(true);
            root.getChildren().remove(nextLevelImage);
            root.getChildren().add(nextLevelImage);
            nextLevelImage.toFront();
        }
        System.out.println("Press SPACE to start combat level!");
    }
    
    private void transitionToNextLevel() {
    	soundManager.stopMusic();
        currentLevelId++;
        
        try {
            // Load next level data
            LevelData nextLevelData = LevelConfig.getLevelData(currentLevelId);
            
            // Determine state based on level type
            if (nextLevelData.getLevelType() == LevelData.LevelType.COMBAT) {
                currentState = GameState.COMBAT_LEVEL;
            } else if (nextLevelData.getLevelType() == LevelData.LevelType.PLATFORMER) {
                currentState = GameState.LEVEL_1;
            }
            
            // Hide transition images
            if (nextLevelImage != null) {
                nextLevelImage.setVisible(false);
            }
            if (messageImage != null) {
                messageImage.setVisible(false);
            }
            
            // Create and initialize new level manager
            currentLevelManager = LevelFactory.createLevelManager(nextLevelData, world, player, camera, backgroundManager);
            currentLevelManager.initialize();
         // Reset pour le prochain niveau
            transitionSoundPlayed = false;

            /*
            if (nextLevelData.getLevelType() == LevelData.LevelType.PLATFORMER) {
                soundManager.playMusic("theme", true);
            } */
         // Joue le thème pour les PLATFORMER et le Level 2 COMBAT
            if (nextLevelData.getLevelType() == LevelData.LevelType.PLATFORMER || currentLevelId == 2) {
                soundManager.setMusicVolume(0.3); // ajuste le volume si tu veux
                soundManager.playMusic("theme", true);
            }

            // Handle background for combat levels
            if (currentLevelManager instanceof CombatLevelManager) {
                root.getChildren().remove(backgroundManager.getView());
                ImageView combatBg = ((CombatLevelManager) currentLevelManager).getBackground();
                if (combatBg != null) {
                    root.getChildren().add(0, combatBg);
                    combatBg.toBack();
                }
                // Setup input handlers for combat level
                ((CombatLevelManager) currentLevelManager).setupInputHandlers(scene);
                // Hide hearts and health bar in combat level (combat has its own health bar)
                healthBar.getView().setVisible(false);
                for (Heart heart : hearts) {
                    heart.getView().setVisible(false);
                }
            } else {
                // Restore normal background for platformer levels
                if (!root.getChildren().contains(backgroundManager.getView())) {
                    root.getChildren().add(0, backgroundManager.getView());
                }
                // Show hearts and health bar in platformer levels
                healthBar.getView().setVisible(true);
                for (Heart heart : hearts) {
                    heart.getView().setVisible(true);
                }
            }
            
            System.out.println("Level " + currentLevelId + " started!");
        } catch (IllegalArgumentException e) {
            System.out.println("No more levels! Game complete!");
            // Could transition to game over or menu here
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    private void update() {
        // 1️⃣ Vérifier si le joueur est mort avant tout
        if (player.isDead() && currentState != GameState.GAME_OVER) {
            gameOver();
            return; // Stop update pour éviter conflits avec transition ou level complete
        }

        // 2️⃣ Pause updates pendant le menu, transition ou game over
        if (currentState == GameState.MENU || currentState == GameState.LEVEL_TRANSITION || currentState == GameState.GAME_OVER) {
            return;
        }

        // 3️⃣ Le reste de ton code update...
        boolean left = pressedKeys.contains(KeyCode.LEFT);
        boolean right = pressedKeys.contains(KeyCode.RIGHT);
        boolean jump = pressedKeys.contains(KeyCode.UP);
        
        if (currentLevelManager instanceof PlatformerLevelManager) {
            ((PlatformerLevelManager) currentLevelManager).handlePlayerInput(left, right, jump);
        } else if (currentLevelManager instanceof CombatLevelManager) {
            ((CombatLevelManager) currentLevelManager).handlePlayerInput(left, right, jump);
        }

        if (currentLevelManager != null) {
            currentLevelManager.update();
            if (currentLevelManager.isComplete()) {
                if (currentState == GameState.LEVEL_1) {
                    showLevelCompleteMessage();
                } else if (currentState == GameState.COMBAT_LEVEL) {
                    // Combat level complete
                }
            }
        }

        // UI updates
        if (currentLevelManager instanceof CombatLevelManager) {
            healthBar.getView().setVisible(false);
        } else {
            healthBar.getView().setVisible(true);
            healthBar.update(player.getHealth());
            updateHearts();
        }
    }

    
    private void initializeHearts() {
        hearts = new ArrayList<>();
        double heartX = 20;
        double heartY = 50; // Under the health bar (health bar is at Y=20, height=20, so start at 50)
        double heartSpacing = 35;
        
        for (int i = 0; i < 3; i++) {
            Heart heart = new Heart();
            heart.getView().setX(heartX + (i * heartSpacing));
            heart.getView().setY(heartY);
            hearts.add(heart);
            root.getChildren().add(heart.getView());
        }
    }
    
    private void updateHearts() {
        int currentHearts = player.getHearts();
        for (int i = 0; i < hearts.size(); i++) {
            hearts.get(i).setFilled(i < currentHearts);
        }
    }
    
    private void initializeGameOverImage() {
        try {
            Image gameOverImg = new Image("file:ressources/GAME_IS_OVER.png");
            gameOverImage = new ImageView(gameOverImg);
            // Make it slightly smaller (80% of screen size)
            gameOverImage.setFitWidth(GAME_WIDTH * 0.8);
            gameOverImage.setFitHeight(GAME_HEIGHT * 0.8);
            gameOverImage.setPreserveRatio(true);
            // Center it on screen
            gameOverImage.setX(GAME_WIDTH * 0.1);
            gameOverImage.setY(GAME_HEIGHT * 0.1);
            gameOverImage.setVisible(false);
            // Add to root and bring to front
            root.getChildren().add(gameOverImage);
            gameOverImage.toFront();
        } catch (Exception e) {
            System.err.println("Could not load GAME_IS_OVER.png: " + e.getMessage());
            gameOverImage = new ImageView();
            gameOverImage.setVisible(false);
            root.getChildren().add(gameOverImage);
        }
    }
    
    private void gameOver() {
        // Stop toutes les musiques en cours
        soundManager.stopMusic();

        // Jouer le game over correspondant
        if (currentLevelId == 1) {
            soundManager.playOneShot("gameover1"); // 🎵 correct
        } else {
            soundManager.playOneShot("gameover2"); // 🎵 correct
        }

        currentState = GameState.GAME_OVER;

        if (gameOverImage != null) {
            gameOverImage.setVisible(true);
            root.getChildren().remove(gameOverImage);
            root.getChildren().add(gameOverImage);
            gameOverImage.toFront();
        }

        System.out.println("Game Over! Press SPACE to restart.");
    }


    
    private void restartGame() {
        // Reset game state to menu
        currentState = GameState.MENU;
        currentLevelId = 1;
        
        // Hide game over image
        if (gameOverImage != null) {
            gameOverImage.setVisible(false);
        }
        
        // Show home screen
        if (homeScreen != null) {
            homeScreen.setVisible(true);
            homeScreen.getView().toFront();
        }
        
        // Hide game UI
        healthBar.getView().setVisible(false);
        for (Heart heart : hearts) {
            heart.getView().setVisible(false);
        }
        
        // Reset player
        player = new Player(0, 0);
        
        // Clear world
        world.getChildren().clear();
        currentLevelManager = null;
        
        // Restore background
        if (!root.getChildren().contains(backgroundManager.getView())) {
            root.getChildren().add(0, backgroundManager.getView());
        }
        
        System.out.println("Returned to main menu!");
    }


    public Pane getRoot() {
        return root;
    }
}
