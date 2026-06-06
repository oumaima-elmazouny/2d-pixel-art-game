package com.platformer.levels.combat;

import com.platformer.levels.LevelManager;
import com.platformer.levels.LevelData;
import com.platformer.core.Camera;
import com.platformer.combat.Fighter;
import com.platformer.combat.FighterState;
import com.platformer.combat.InputState;
import com.platformer.combat.MagicBallProjectile;
import com.platformer.combat.AnimationLoader;
import com.platformer.combat.MonsterAIState;
import com.platformer.ui.HealthBar;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.platformer.core.GameManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CombatLevelManager implements LevelManager {
    
    private static final double GAME_WIDTH = 960;
    private static final double GAME_HEIGHT = 500;
    private static final double MONSTER_RIGHTWARD_OFFSET = 50;
    private static final double DOWNWARD_OFFSET = 150; // Downward displacement for player and monster to align with background surface
    private static final double MONSTER_VERTICAL_OFFSET = -50; // Vertical offset upward for monster (negative = upward)
    
    // Fighter parameters
    private static final double TILE_SIZE = 128;
    private static final double GRAVITY = 800.0; // pixels per second squared
    private static final double MOVE_SPEED = 200.0; // pixels per second
    private static final double JUMP_SPEED = -400.0; // pixels per second (negative = up)
    private static final double ATTACK_DURATION = 0.5; // seconds
    private static final double ATTACK_COOLDOWN = 1.0; // seconds
    private static final double FRICTION = 0.8; // friction coefficient
    
    private Pane world;
    private Camera camera;
    private LevelData levelData;
    private ImageView background;
    
    private Fighter playerFighter;
    private Fighter monsterFighter;
    private InputState inputState;
    private List<MagicBallProjectile> projectiles;
    private Image magicBallImage;
    
    // Health bars
    private HealthBar playerHealthBar;
    private HealthBar monsterHealthBar;
    
    // Result announcement
    private Text resultText;
    
    private boolean levelComplete = false;
    private boolean playerLost = false;
    private double lastMonsterAttackTime = 0;
    private static final double MONSTER_PROJECTILE_COOLDOWN = 2.0; // seconds
    private double lastPlayerAttackTime = 0;
    private static final double PLAYER_PROJECTILE_COOLDOWN = 1.0; // seconds
    private boolean playerJustAttacked = false;
    
    // Monster AI state machine
    private MonsterAIState monsterAIState = MonsterAIState.IDLE;
    private boolean playerHasMoved = false;
    private double playerLastX = 0;
    private double startAttackTimer = 0;
    private static final double START_ATTACK_DURATION = 0.6; // Duration for start attack animation
    
    
    public CombatLevelManager(Pane world, com.platformer.core.Player player, Camera camera, LevelData levelData) {
        this.world = world;
        this.camera = camera;
        this.levelData = levelData;
        this.inputState = new InputState();
        this.projectiles = new ArrayList<>();
    }
    
    @Override
    public void initialize() {
        // Clear world
        world.getChildren().clear();
        world.setPrefWidth(levelData.getWorldWidth());
        camera.setWorldWidth(levelData.getWorldWidth());
        
        // Load magic ball image
        try {
            magicBallImage = new Image("file:ressources/magicball.png");
        } catch (Exception e) {
            System.err.println("Could not load magicball.png: " + e.getMessage());
        }
        
        // Create background
        try {
            Image backImg = new Image("file:ressources/" + levelData.getBackgroundImage());
            background = new ImageView(backImg);
            background.setFitWidth(GAME_WIDTH);
            background.setFitHeight(GAME_HEIGHT);
            background.setPreserveRatio(false);
            background.setX(0);
            background.setY(0);
        } catch (Exception e) {
            System.err.println("Could not load " + levelData.getBackgroundImage() + ": " + e.getMessage());
        }
        
        // Load animations
        AnimationLoader.PlayerAnimations playerAnims = new AnimationLoader.PlayerAnimations();
        AnimationLoader.MonsterAnimations monsterAnims = new AnimationLoader.MonsterAnimations();
        
        // Calculate ground position - Fighter uses y as bottom (ground level)
        // Position fighters upward by 90 pixels from near bottom
        double groundY = GAME_HEIGHT - 50 - 90; // Position fighters with upward offset
        
        // Create player fighter
        double playerX = levelData.getPlayerCombatX();
        playerFighter = new Fighter(
            TILE_SIZE, 100, // maxHealth
            playerAnims.idle, playerAnims.run, playerAnims.jump,
            playerAnims.attack, playerAnims.down, playerAnims.defense,
            groundY, GRAVITY, MOVE_SPEED, JUMP_SPEED,
            ATTACK_DURATION, ATTACK_COOLDOWN
        );
        playerFighter.setPosition(playerX, groundY);
        playerFighter.setSize(1.17); // ~150px scale (128 * 1.17 ≈ 150)
        world.getChildren().add(playerFighter.getView());
        
        // Initialize AI tracking - start monster in ATTACK state to throw projectiles immediately
        playerLastX = playerX;
        playerHasMoved = false;
        monsterAIState = MonsterAIState.ATTACK; // Start throwing projectiles immediately
        lastMonsterAttackTime = System.currentTimeMillis() / 1000.0; // Initialize attack timer
        startAttackTimer = 0;
        
        // Create monster fighter (AI controlled)
        double monsterX = levelData.getMonsterX() + MONSTER_RIGHTWARD_OFFSET;
        double monsterY = groundY + MONSTER_VERTICAL_OFFSET; // Apply vertical offset upward
        monsterFighter = new Fighter(
            TILE_SIZE, 100, // maxHealth
            monsterAnims.idle, monsterAnims.run, monsterAnims.jump,
            monsterAnims.attack, monsterAnims.down, monsterAnims.defense,
            monsterY, GRAVITY, MOVE_SPEED, JUMP_SPEED,
            ATTACK_DURATION, ATTACK_COOLDOWN
        );
        monsterFighter.setPosition(monsterX, monsterY);
        monsterFighter.setSize(1.46); // ~187px scale (128 * 1.46 ≈ 187)
        world.getChildren().add(monsterFighter.getView());
        
        // Create health bars - player in top-left (blue), monster in top-right (red)
        playerHealthBar = new HealthBar(20, 20, 200, 25, 100, Color.rgb(80, 160, 255)); // Blue
        monsterHealthBar = new HealthBar(GAME_WIDTH - 220, 20, 200, 25, 100, Color.rgb(255, 80, 80)); // Red
        world.getChildren().add(playerHealthBar.getView());
        world.getChildren().add(monsterHealthBar.getView());
        // Bring health bars to front so they're visible
        playerHealthBar.getView().toFront();
        monsterHealthBar.getView().toFront();
        
        // Create result text (initially hidden)
        resultText = new Text();
        resultText.setFont(Font.font("Arial", 48));
        resultText.setFill(Color.WHITE);
        resultText.setStroke(Color.BLACK);
        resultText.setStrokeWidth(2);
        resultText.setVisible(false);
        resultText.setLayoutX(GAME_WIDTH / 2 - 200); // Will be centered when shown
        resultText.setLayoutY(GAME_HEIGHT / 2);
        world.getChildren().add(resultText);
        resultText.toFront();
    }
    
    public void setupInputHandlers(javafx.scene.Scene scene) {
        inputState.bind(scene);
    }
    
    @Override
    public void update() {
        // ✅ Si le niveau est fini, ne rien mettre à jour
        if (levelComplete) return;

        double dt = 1.0 / 60.0; // Assuming 60 FPS
        double currentTime = System.currentTimeMillis() / 1000.0;
        double groundY = GAME_HEIGHT - 50 - 90; // Position fighters with upward offset

        // Get player input
        InputState.PlayerInput playerInput = inputState.player1();

        // Update player fighter
        playerFighter.update(dt, playerInput, groundY, FRICTION);

        // Check if player is attacking and throw projectile
        if (playerFighter.isAttackActive() && !playerJustAttacked && 
            (currentTime - lastPlayerAttackTime) >= PLAYER_PROJECTILE_COOLDOWN) {
            throwPlayerMagicBall();
            playerJustAttacked = true;
            lastPlayerAttackTime = currentTime;
        }

        // Reset player attack flag when attack ends
        if (!playerFighter.isAttackActive()) {
            playerJustAttacked = false;
        }

        // Update monster fighter (AI)
        updateMonsterAI(dt, groundY, currentTime);

        // Ensure fighters stay on their ground level when on ground
        if (playerFighter.isOnGroundPublic()) {
            playerFighter.setY(groundY);
        }
        if (monsterFighter != null && monsterFighter.isAlive() && monsterFighter.isOnGroundPublic()) {
            double monsterGroundY = groundY + MONSTER_VERTICAL_OFFSET;
            monsterFighter.setY(monsterGroundY);
        }

        // Update projectiles
        updateProjectiles(dt);

        // Check collisions
        checkCombatCollisions();

        // Check if monster is defeated (player wins)
        if (monsterFighter != null && !monsterFighter.isAlive() && !levelComplete) {
            levelComplete = true;
            playerLost = false;
            showResult(true);
        }

        // Check if player is defeated (player loses)
        if (playerFighter != null && !playerFighter.isAlive() && !levelComplete) {
            levelComplete = true;
            playerLost = true;
            showResult(false);
        }

        // Update camera - only follow horizontally in combat, keep Y at 0
        camera.updateHorizontalOnly(playerFighter.getX());
        camera.applyTo(world);

        // Clamp fighters to stage
        playerFighter.clampToStage(GAME_WIDTH, 1.17);
        if (monsterFighter != null) {
            monsterFighter.clampToStage(GAME_WIDTH, 1.46);
        }

        // Update health bars
        if (playerHealthBar != null) {
            playerHealthBar.update((int)playerFighter.getHealth());
        }
        if (monsterHealthBar != null && monsterFighter != null) {
            monsterHealthBar.update((int)monsterFighter.getHealth());
        }
    }

    
    private void updateMonsterAI(double dt, double groundY, double currentTime) {
        if (monsterFighter == null || !monsterFighter.isAlive()) return;
        
        // Monster's ground level with vertical offset
        double monsterGroundY = groundY + MONSTER_VERTICAL_OFFSET;
        
        // Create a dummy input for monster (monster is fully AI controlled - no player input)
        InputState.PlayerInput aiInput = new InputState.PlayerInput();
        
        // Track player movement
        double currentPlayerX = playerFighter.getX();
        if (Math.abs(currentPlayerX - playerLastX) > 1.0) {
            playerHasMoved = true;
        }
        playerLastX = currentPlayerX;
        
        // Normal behavior - monster faces player
        if (playerFighter.getX() < monsterFighter.getX()) {
            monsterFighter.setFacing(-1);
        } else {
            monsterFighter.setFacing(1);
        }
        
        // Check if player is walking backward (away from monster)
        // Player is walking backward if they're moving away from the monster
        double playerVx = playerFighter.getVx();
        boolean playerWalkingBackward = false;
        if (Math.abs(playerVx) > 5) { // Player is moving
            if (playerFighter.getX() < monsterFighter.getX() && playerVx < 0) {
                // Player is to the left of monster and moving left (away)
                playerWalkingBackward = true;
            } else if (playerFighter.getX() > monsterFighter.getX() && playerVx > 0) {
                // Player is to the right of monster and moving right (away)
                playerWalkingBackward = true;
            }
        }
        
        // Make monster follow player when player is walking backward
        // Only follow if not in DEFENSE state (monster should stay put when defending)
        if (playerWalkingBackward && monsterAIState != MonsterAIState.DEFENSE) {
            // Calculate direction to player
            double dx = playerFighter.getX() - monsterFighter.getX();
            if (dx < 0) {
                // Player is to the left, move left
                aiInput.setLeft(true);
            } else if (dx > 0) {
                // Player is to the right, move right
                aiInput.setRight(true);
            }
        }
        
        // AI State Machine (always runs, no skipping)
        {
            switch (monsterAIState) {
                case IDLE:
                    // Skip IDLE - go straight to ATTACK to throw projectiles
                    monsterAIState = MonsterAIState.ATTACK;
                    lastMonsterAttackTime = currentTime;
                    break;
                    
                case START_ATTACK:
                    // Check if player is attacking - switch to defense
                    if (playerFighter.isAttackActive()) {
                        monsterAIState = MonsterAIState.DEFENSE;
                        aiInput.setDefense(true);
                    } else {
                        // Player moved for first time - perform melee attack
                        // This triggers attack animation which includes monstreattaque1.png
                        aiInput.setAttack(true);
                        startAttackTimer -= dt;
                        
                        if (startAttackTimer <= 0) {
                            monsterAIState = MonsterAIState.ATTACK;
                            lastMonsterAttackTime = currentTime;
                        }
                    }
                    break;
                    
                case ATTACK:
                    // Always defend if player is attacking
                    if (playerFighter.isAttackActive()) {
                        monsterAIState = MonsterAIState.DEFENSE;
                        aiInput.setDefense(true);
                    } else {
                        // Throw projectiles every 2 seconds continuously while in ATTACK state
                        if (currentTime - lastMonsterAttackTime >= MONSTER_PROJECTILE_COOLDOWN) {
                            throwMagicBall();
                            lastMonsterAttackTime = currentTime;
                        }
                    }
                    break;
                    
                case DEFENSE:
                    // Player is attacking - monster blocks
                    // Defense animation (monstre_deffence.png) is used
                    aiInput.setDefense(true);
                    
                    // Return to attack when player stops attacking
                    if (!playerFighter.isAttackActive()) {
                        monsterAIState = MonsterAIState.ATTACK;
                        lastMonsterAttackTime = currentTime;
                    }
                    break;
            }
        }
        
        // Update monster with AI-controlled input (use offset ground level)
        monsterFighter.update(dt, aiInput, monsterGroundY, FRICTION);
    }
    
    private void throwMagicBall() {
        if (magicBallImage == null || monsterFighter == null || !monsterFighter.isAlive()) return;
        
        // Calculate direction from monster to player
        double dx = playerFighter.getX() - monsterFighter.getX();
        double dy = playerFighter.getY() - monsterFighter.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 1) return; // Avoid division by zero
        
        // Normalize direction
        double speed = 300.0; // pixels per second
        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;
        
        // Start position (from monster's position, slightly forward)
        double startX = monsterFighter.getX() + (monsterFighter.getFacing() > 0 ? 50 : -50);
        double startY = monsterFighter.getY() - 60; // Slightly above ground
        
        MagicBallProjectile projectile = new MagicBallProjectile(
            magicBallImage, startX, startY, vx, vy, 10, false // false = from monster
        );
        projectiles.add(projectile);
        world.getChildren().add(projectile.getView());
        projectile.getView().toFront(); // Ensure projectile is visible
    }
    
    private void throwPlayerMagicBall() {
        if (magicBallImage == null || playerFighter == null || !playerFighter.isAlive()) return;
        
        // Calculate direction from player to monster
        double dx = monsterFighter.getX() - playerFighter.getX();
        double dy = monsterFighter.getY() - playerFighter.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 1) return; // Avoid division by zero
        
        // Normalize direction
        double speed = 300.0; // pixels per second
        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;
        
        // Start position (from player's position, slightly forward in facing direction)
        double startX = playerFighter.getX() + (playerFighter.getFacing() > 0 ? 50 : -50);
        double startY = playerFighter.getY() - 60; // Slightly above ground
        
        MagicBallProjectile projectile = new MagicBallProjectile(
            magicBallImage, startX, startY, vx, vy, 10, true // true = from player
        );
        projectiles.add(projectile);
        world.getChildren().add(projectile.getView());
        projectile.getView().toFront(); // Ensure projectile is visible
    }
    
    private void updateProjectiles(double dt) {
        Iterator<MagicBallProjectile> it = projectiles.iterator();
        while (it.hasNext()) {
            MagicBallProjectile proj = it.next();
            
            if (!proj.isActive()) {
                world.getChildren().remove(proj.getView());
                it.remove();
                continue;
            }
            
            proj.update(dt);
            // Keep projectiles visible on top
            proj.getView().toFront();
            
            // Remove if off screen
            if (proj.getX() < -100 || proj.getX() > GAME_WIDTH + 100 ||
                proj.getY() < -100 || proj.getY() > GAME_HEIGHT + 100) {
                proj.deactivate();
                world.getChildren().remove(proj.getView());
                it.remove();
            }
        }
    }
    
    private void checkCombatCollisions() {
        // Player attaque monster
        if (playerFighter.isAttackActive() && !playerFighter.hasRegisteredHit()) {
            Rectangle2D playerAttackBounds = playerFighter.getAttackBounds(1.17);
            Rectangle2D monsterBounds = monsterFighter.getBounds(1.46);

            if (playerAttackBounds.intersects(monsterBounds)) {
                monsterFighter.takeDamage(10);
                playerFighter.registerHit();

                // 🔊 Son quand le monster est frappé
                if (GameManager.soundManager != null) {
                    GameManager.soundManager.playOneShot("monster_hit");
                }

                System.out.println("Monster health: " + monsterFighter.getHealth() + "/" + monsterFighter.getMaxHealth());
            }
        }

        // Projectiles
        Iterator<MagicBallProjectile> it = projectiles.iterator();
        while (it.hasNext()) {
            MagicBallProjectile proj = it.next();
            if (!proj.isActive()) continue;

            Rectangle2D projBounds = proj.getBounds();
            Rectangle2D playerBounds = playerFighter.getBounds(1.17);
            Rectangle2D monsterBounds = monsterFighter.getBounds(1.46);

            if (proj.isFromPlayer()) {
                // Projectile joueur
                if (projBounds.intersects(monsterBounds)) {
                    if (monsterFighter.getState() == FighterState.DEFENSE) {
                        proj.deactivate();
                        world.getChildren().remove(proj.getView());
                        it.remove();
                    } else {
                        monsterFighter.takeDamage(proj.getDamage());
                        proj.deactivate();
                        world.getChildren().remove(proj.getView());
                        it.remove();

                        // 🔊 Son quand le monster est frappé par projectile
                        if (GameManager.soundManager != null) {
                            GameManager.soundManager.playOneShot("monster_hit");
                        }
                    }
                }
            } else {
                // Projectile monstre
                if (projBounds.intersects(playerBounds)) {
                    if (playerFighter.getState() == FighterState.DEFENSE) {
                        proj.deactivate();
                        world.getChildren().remove(proj.getView());
                        it.remove();
                    } else {
                        playerFighter.takeDamage(proj.getDamage());
                        proj.deactivate();
                        world.getChildren().remove(proj.getView());
                        it.remove();

                        // 🔊 Son quand le player est frappé par projectile
                        if (GameManager.soundManager != null) {
                            GameManager.soundManager.playOneShot("player_hit");
                        }

                        System.out.println("Player health: " + playerFighter.getHealth() + "/" + playerFighter.getMaxHealth());
                    }
                }
            }
        }

        // Monster attaque player
        if (monsterFighter.isAttackActive() && !monsterFighter.hasRegisteredHit()) {
            Rectangle2D monsterAttackBounds = monsterFighter.getAttackBounds(1.46);
            Rectangle2D playerBounds = playerFighter.getBounds(1.17);

            if (monsterAttackBounds.intersects(playerBounds)) {
                if (playerFighter.getState() != FighterState.DEFENSE) {
                    playerFighter.takeDamage(10);

                    // 🔊 Son quand le player est frappé
                    if (GameManager.soundManager != null) {
                        GameManager.soundManager.playOneShot("player_hit");
                    }

                    System.out.println("Player health: " + playerFighter.getHealth() + "/" + playerFighter.getMaxHealth());
                }
                monsterFighter.registerHit();
            }
        }

        // Collision fighters (push apart)
        Rectangle2D playerBounds = playerFighter.getBounds(1.17);
        Rectangle2D monsterBounds = monsterFighter.getBounds(1.46);

        if (playerBounds.intersects(monsterBounds)) {
            double monsterHealthPercent = (monsterFighter.getHealth() / monsterFighter.getMaxHealth()) * 100.0;
            boolean monsterRetreating = monsterHealthPercent <= 50.0;

            double separation = 100;

            double playerVx = playerFighter.getVx();
            double monsterVx = monsterFighter.getVx();

            if (monsterRetreating) {
                double dx = playerFighter.getX() - monsterFighter.getX();
                if (dx < 0) monsterFighter.setX(playerFighter.getX() + separation);
                else monsterFighter.setX(playerFighter.getX() - separation);

                if (Math.abs(playerVx) > 5) {
                    if (playerVx < 0 && playerFighter.getX() < monsterFighter.getX())
                        playerFighter.setX(monsterFighter.getX() - separation);
                    else if (playerVx > 0 && playerFighter.getX() > monsterFighter.getX())
                        playerFighter.setX(monsterFighter.getX() + separation);
                }
            } else {
                double centerX = (playerFighter.getX() + monsterFighter.getX()) / 2.0;
                double playerSize = TILE_SIZE * 1.17;
                double monsterSize = TILE_SIZE * 1.46;

                if (playerFighter.getX() < monsterFighter.getX()) {
                    playerFighter.setX(centerX - separation / 2 - playerSize / 2);
                    monsterFighter.setX(centerX + separation / 2 + monsterSize / 2);
                } else {
                    playerFighter.setX(centerX + separation / 2 + playerSize / 2);
                    monsterFighter.setX(centerX - separation / 2 - monsterSize / 2);
                }
            }
        }

        // Clear one-shot inputs
        inputState.clearFrame();
    }

    
    public void handlePlayerInput(boolean left, boolean right, boolean jump) {
        // Input is now handled through InputState in update()
        // This method kept for compatibility but may be called from GameManager
    }
    
    public void playerAttack() {
        // Attack is now handled through InputState (SPACE key)
        // This method kept for compatibility
    }
    
    private void showResult(boolean playerWon) {
        if (resultText == null) return;

        // Stoppe la musique de fond avant de jouer le one-shot
        if (GameManager.soundManager != null) {
            GameManager.soundManager.stopMusic(); // arrête uniquement la musique looping
        }

        // Personnalisation des textes et sons
        if (playerWon) {
            resultText.setText("Bravo! Tu as gagné ce niveau!"); // Phrase personnalisée pour victoire
            resultText.setFill(Color.GREEN);
            if (GameManager.soundManager != null) {
                GameManager.soundManager.playOneShot("game-over1"); // Son de victoire
            }
        } else {
            resultText.setText("Dommage! Tu as perdu ce niveau!"); // Phrase personnalisée pour défaite
            resultText.setFill(Color.RED);
            if (GameManager.soundManager != null) {
                GameManager.soundManager.playOneShot("game-over2"); // Son de défaite
            }
        }

        // Centrer le texte horizontalement
        double textWidth = resultText.getLayoutBounds().getWidth();
        resultText.setLayoutX((GAME_WIDTH - textWidth) / 2);
        resultText.setLayoutY(GAME_HEIGHT / 2);
        resultText.setVisible(true);
        resultText.toFront();
    }


    
    public ImageView getBackground() {
        return background;
    }
    
    @Override
    public Pane getWorld() {
        return world;
    }
    
    @Override
    public void cleanup() {
        // Cleanup projectiles
        for (MagicBallProjectile proj : projectiles) {
            world.getChildren().remove(proj.getView());
        }
        projectiles.clear();
    }
    
    @Override
    public boolean isComplete() {
        return levelComplete;
    }
    
    public InputState getInputState() {
        return inputState;
    }
}

