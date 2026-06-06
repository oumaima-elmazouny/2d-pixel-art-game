package com.platformer.levels.platformer;

import com.platformer.levels.LevelManager;
import com.platformer.levels.LevelData;
import com.platformer.core.Player;
import com.platformer.core.Camera;
import com.platformer.core.BackgroundManager;
import com.platformer.entities.platforms.Platform;
import com.platformer.entities.platforms.FlyingPlatform;
import com.platformer.entities.platforms.Obstacle;
import com.platformer.entities.items.CrystalReward;
import com.platformer.entities.items.CrystalProjectile;
import com.platformer.entities.enemies.BugEnemy;
import com.platformer.entities.FantasyGate;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;
import com.platformer.core.GameManager;

public class PlatformerLevelManager implements LevelManager {
    
    private Pane world;
    private Player player;
    private Camera camera;
    private BackgroundManager backgroundManager;
    private LevelData levelData;
    
    private List<Platform> platforms;
    private List<FlyingPlatform> flyingPlatforms;
    private List<CrystalReward> crystals;
    private List<BugEnemy> bugEnemies;
    private List<Obstacle> obstacles;
    private List<CrystalProjectile> projectiles;
    private FantasyGate gate;
    
    private boolean levelComplete = false;
    private boolean playerMovedThisFrame = false;
    
    public PlatformerLevelManager(Pane world, Player player, Camera camera, BackgroundManager backgroundManager, LevelData levelData) {
        this.world = world;
        this.player = player;
        this.camera = camera;
        this.backgroundManager = backgroundManager;
        this.levelData = levelData;
        
        platforms = new ArrayList<>();
        flyingPlatforms = new ArrayList<>();
        crystals = new ArrayList<>();
        bugEnemies = new ArrayList<>();
        obstacles = new ArrayList<>();
        projectiles = new ArrayList<>();
    }
    
    @Override
    public void initialize() {
        // Resize player to 120px for platformer level
        player.setSize(120);
        
        // Set world width
        world.setPrefWidth(levelData.getWorldWidth());
        camera.setWorldWidth(levelData.getWorldWidth());
        
        // Gate (behind everything)
        if (levelData.getGate() != null) {
            LevelData.GateData gateData = levelData.getGate();
            gate = new FantasyGate(gateData.x, gateData.y);
            world.getChildren().add(gate.getView());
        }
        
        // Obstacles (behind platforms)
        for (LevelData.ObstacleData obstacleData : levelData.getObstacles()) {
            Obstacle obstacle = new Obstacle(obstacleData.x, obstacleData.y);
            obstacles.add(obstacle);
            world.getChildren().add(obstacle.getView());
        }
        
        // Platforms (in front of obstacles)
        for (LevelData.PlatformData platformData : levelData.getPlatforms()) {
            Platform piece = new Platform(platformData.x, platformData.y, platformData.width);
            platforms.add(piece);
            world.getChildren().add(piece.getView());
        }
        
        // Player
        player.setPosition(levelData.getPlayerStartX(), levelData.getPlayerStartY());
        world.getChildren().add(player.getView());
        
        // Flying platforms
        for (LevelData.FlyingPlatformData fpData : levelData.getFlyingPlatforms()) {
            FlyingPlatform fp = new FlyingPlatform(fpData.x, fpData.y, fpData.width);
            flyingPlatforms.add(fp);
            world.getChildren().add(fp.getView());
        }
        
        // Crystals
        for (LevelData.CrystalData crystalData : levelData.getCrystals()) {
            CrystalReward crystal = new CrystalReward(crystalData.x, crystalData.y);
            crystals.add(crystal);
            world.getChildren().add(crystal.getView());
        }
        
        // Bugs
        for (LevelData.BugEnemyData bugData : levelData.getBugEnemies()) {
            BugEnemy bug = new BugEnemy(bugData.x, bugData.y);
            bugEnemies.add(bug);
            world.getChildren().add(bug.getView());
        }
    }
    
    @Override
    public void update() {
        if (levelComplete) return;
        
        player.applyGravity();
        checkPlatformCollisions();
        checkObstacleCollisions();
        checkCrystalCollisions();
        updateProjectilesAndEnemy();
        checkBugCollision();
        checkGateCollision();
        
        player.updateInvincibility();
        player.updateAttack();
        
        // Ensure jump animation is updated after all collisions
        // This ensures jump image is shown when player is in the air
        player.updateJumpAnimationState();
        
        camera.update(player.getX(), player.getY());
        camera.applyTo(world);
        backgroundManager.update(camera.getX());
        
        if (player.getY() > 500 * 2) {
            player.setPosition(player.getX(), 100);
        }
        
    }
    
    public void handlePlayerInput(boolean left, boolean right, boolean jump) {
        playerMovedThisFrame = false;
        
        if (left) {
            player.moveLeft();
            playerMovedThisFrame = true;
        } else if (right) {
            player.moveRight();
            playerMovedThisFrame = true;
        } else {
            player.stopHorizontalMovement();
        }
        
        if (jump && player.isOnGround()) {
            player.jump();
        }
    }
    
    public void shootCrystal() {
        player.startAttack();
        
        if (!player.hasCrystalAmmo()) return;
        
        player.useCrystalAmmo();
        boolean toRight = player.isFacingRight();
        double startX = player.getX() + (toRight ? player.getWidth() : 0);
        double startY = player.getY() + player.getHeight() / 2.0;
        
        CrystalProjectile projectile = new CrystalProjectile(startX, startY, toRight);
        projectiles.add(projectile);
        world.getChildren().add(projectile.getView());
    }
    
    private void checkPlatformCollisions() {
        player.setOnGround(false);
        
        for (Platform platform : platforms) {
            if (player.intersects(platform)) {
                double playerBottom = player.getY() + player.getHeight();
                double platformTop = platform.getY();
                
                if (playerBottom > platformTop && player.getVelocityY() >= 0) {
                    player.setY(platformTop - player.getHeight());
                    player.setVelocityY(0);
                    player.setOnGround(true);
                }
            }
        }
        
        for (FlyingPlatform fp : flyingPlatforms) {
            boolean intersects =
                    player.getX() < fp.getX() + fp.getWidth() &&
                    player.getX() + player.getWidth() > fp.getX() &&
                    player.getY() < fp.getY() + fp.getHeight() &&
                    player.getY() + player.getHeight() > fp.getY();
            
            if (intersects) {
                double playerBottom = player.getY() + player.getHeight();
                double platformTop = fp.getY();
                
                if (playerBottom > platformTop && player.getVelocityY() >= 0) {
                    player.setY(platformTop - player.getHeight());
                    player.setVelocityY(0);
                    player.setOnGround(true);
                }
            }
        }
    }
    
    private void checkObstacleCollisions() {
        // Player collision with obstacles (can jump over)
        for (Obstacle obstacle : obstacles) {
            boolean intersects =
                    player.getX() < obstacle.getX() + obstacle.getWidth() &&
                    player.getX() + player.getWidth() > obstacle.getX() &&
                    player.getY() < obstacle.getY() + obstacle.getHeight() &&
                    player.getY() + player.getHeight() > obstacle.getY();
            
            if (intersects) {
                // Player can stand on top of obstacle
                double playerBottom = player.getY() + player.getHeight();
                double obstacleTop = obstacle.getY();
                
                if (playerBottom > obstacleTop && player.getVelocityY() >= 0) {
                    player.setY(obstacleTop - player.getHeight());
                    player.setVelocityY(0);
                    player.setOnGround(true);
                } else {
                    // Push player away if hitting from sides
                    if (player.getX() < obstacle.getX()) {
                        player.setX(obstacle.getX() - player.getWidth());
                    } else {
                        player.setX(obstacle.getX() + obstacle.getWidth());
                    }
                }
            }
        }
        
        // Bug collision with obstacles (bugs cannot pass through)
        for (BugEnemy bug : bugEnemies) {
            if (bug == null || !bug.isAlive()) continue;
            
            for (Obstacle obstacle : obstacles) {
                boolean intersects =
                        bug.getX() < obstacle.getX() + obstacle.getWidth() &&
                        bug.getX() + bug.getWidth() > obstacle.getX() &&
                        bug.getY() < obstacle.getY() + obstacle.getHeight() &&
                        bug.getY() + bug.getHeight() > obstacle.getY();
                
                if (intersects) {
                    // Stop bug from moving through obstacle
                    // Push bug away from obstacle
                    if (bug.getX() < obstacle.getX()) {
                        bug.setX(obstacle.getX() - bug.getWidth() - 5);
                    } else {
                        bug.setX(obstacle.getX() + obstacle.getWidth() + 5);
                    }
                }
            }
        }
    }
    
    private void checkCrystalCollisions() {
        List<CrystalReward> collected = new ArrayList<>();
        for (CrystalReward crystal : crystals) {
            boolean intersects =
                    player.getX() < crystal.getX() + crystal.getWidth() &&
                    player.getX() + player.getWidth() > crystal.getX() &&
                    player.getY() < crystal.getY() + crystal.getHeight() &&
                    player.getY() + player.getHeight() > crystal.getY();
            /*
            if (intersects) {
                player.addHealth(1);
                player.addCrystalAmmo(1);
                world.getChildren().remove(crystal.getView());
                collected.add(crystal);
            }*/
            if (intersects) {
                player.addHealth(1);
                player.addCrystalAmmo(1);

                // 🔊 SON DU COIN
                if (GameManager.soundManager != null) {
                    GameManager.soundManager.playSfx("coin");
                }

                world.getChildren().remove(crystal.getView());
                collected.add(crystal);
            }

        }
        crystals.removeAll(collected);
    }
    
    private void updateProjectilesAndEnemy() {
        for (BugEnemy bug : bugEnemies) {
            if (bug != null && bug.isAlive()) {
                bug.update(player.getX(), playerMovedThisFrame);
            }
        }
        
        List<CrystalProjectile> toRemove = new ArrayList<>();
        for (CrystalProjectile projectile : projectiles) {
            if (!projectile.isActive()) {
                toRemove.add(projectile);
                continue;
            }
            
            projectile.update();
            
            if (projectile.getX() < 0 || projectile.getX() > world.getPrefWidth()) {
                projectile.deactivate();
                world.getChildren().remove(projectile.getView());
                toRemove.add(projectile);
                continue;
            }
            
            for (BugEnemy bug : bugEnemies) {
                if (bug != null && bug.isAlive()) {
                    boolean hit =
                            projectile.getX() < bug.getX() + bug.getWidth() &&
                            projectile.getX() + projectile.getWidth() > bug.getX() &&
                            projectile.getY() < bug.getY() + bug.getHeight() &&
                            projectile.getY() + projectile.getHeight() > bug.getY();
                    
                    if (hit) {
                        bug.kill();
                        world.getChildren().remove(bug.getView());
                        projectile.deactivate();
                        world.getChildren().remove(projectile.getView());
                        toRemove.add(projectile);
                        break;
                    }
                }
            }
        }
        
        projectiles.removeAll(toRemove);
    }
    
    private void checkBugCollision() {
        if (player.isInvincible()) return;
        
        for (BugEnemy bug : bugEnemies) {
            if (bug == null || !bug.isAlive()) continue;
            
            boolean intersects =
                    player.getX() < bug.getX() + bug.getWidth() &&
                    player.getX() + player.getWidth() > bug.getX() &&
                    player.getY() < bug.getY() + bug.getHeight() &&
                    player.getY() + player.getHeight() > bug.getY();
            
            if (intersects) {
                player.takeDamage(1);
                break;
            }
        }
    }
    
    private void checkGateCollision() {
        if (gate == null || levelComplete) return;
        
        boolean intersects =
                player.getX() < gate.getX() + gate.getWidth() &&
                player.getX() + player.getWidth() > gate.getX() &&
                player.getY() < gate.getY() + gate.getHeight() &&
                player.getY() + player.getHeight() > gate.getY();
        
        if (intersects) {
            levelComplete = true;
        }
       

    }
    
    @Override
    public Pane getWorld() {
        return world;
    }
    
    @Override
    public void cleanup() {
        // Cleanup if needed
    }
    
    @Override
    public boolean isComplete() {
        return levelComplete;
    }
}
