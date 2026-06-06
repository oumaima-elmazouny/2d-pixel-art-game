package com.platformer.core;

import com.platformer.entities.platforms.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.platformer.core.GameManager;

public class Player {
    private ImageView view;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private boolean onGround;
    private boolean facingRight = true;
    private int health = 3;
    private int maxHealth = 5;
    private int hearts = 3; // Player has 3 hearts (souls)
    private int crystalAmmo = 0;
    private int invincibilityFrames = 0;  // Frames of invincibility after being hit
    
    // Images for animation
    private Image idleImage;
    private Image walkingImage;
    private Image attackImage;  // player_attack_position.png
    private Image jumpImage;    // player_jump_position.png
    private int attackFrames = 0;  // Counter for attack animation duration
    private javafx.scene.effect.ColorAdjust damageEffect = new javafx.scene.effect.ColorAdjust();
    
    private static final double MOVE_SPEED = 5;
    private static final double JUMP_STRENGTH = -15;
    private static final double GRAVITY = 0.8;
    
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = false;
        
        // Load player sprites
        try {
            idleImage = new Image("file:ressources/player.png");
            walkingImage = new Image("file:ressources/player_walking_position.png");
            attackImage = new Image("file:ressources/player_attack_position.png");
            try {
                jumpImage = new Image("file:ressources/player_jump_position.png");
                System.out.println("Jump image loaded successfully");
            } catch (Exception e) {
                System.err.println("Could not load player_jump_position.png: " + e.getMessage());
                jumpImage = null;
            }
            
            view = new ImageView(idleImage);
            view.setSmooth(false);
            view.setPreserveRatio(false);
            
            // Set target size (default 120px for platformer level)
            double targetHeight = 120;
            double targetWidth = idleImage.getWidth() * (targetHeight / idleImage.getHeight());
            
            width = targetWidth;
            height = targetHeight;
            
            view.setFitWidth(width);
            view.setFitHeight(height);
            
        } catch (Exception e) {
            System.err.println("Could not load player sprite: " + e.getMessage());
            width = 40;
            height = 50;
            view = new ImageView();
            view.setFitWidth(width);
            view.setFitHeight(height);
        }
        
        // Configure red damage effect
        damageEffect.setHue(-1.0);         // shift towards red
        damageEffect.setSaturation(0.8);
        damageEffect.setBrightness(0.1);
        
        updateViewPosition();
    }
    
    public void moveLeft() {
        velocityX = -MOVE_SPEED;
        x += velocityX;
        
        // Change to walking sprite (only if not attacking and on ground)
        // Don't override jump animation if player is in the air
        if (attackFrames == 0 && onGround && walkingImage != null) {
            view.setImage(walkingImage);
        }
        // If in air, keep jump image (handled by updateJumpAnimation)
        
        // Flip sprite to face left
        if (facingRight) {
            facingRight = false;
            view.setScaleX(-1);
        }
        
        updateViewPosition();
    }
    
    public void moveRight() {
        velocityX = MOVE_SPEED;
        x += velocityX;
        
        // Change to walking sprite (only if not attacking and on ground)
        // Don't override jump animation if player is in the air
        if (attackFrames == 0 && onGround && walkingImage != null) {
            view.setImage(walkingImage);
        }
        // If in air, keep jump image (handled by updateJumpAnimation)
        
        // Flip sprite to face right
        if (!facingRight) {
            facingRight = true;
            view.setScaleX(1);
        }
        
        updateViewPosition();
    }
    
    public void stopHorizontalMovement() {
        velocityX = 0;
        
        // Change back to idle sprite (only if not attacking and on ground)
        if (attackFrames == 0 && onGround && idleImage != null) {
            view.setImage(idleImage);
        }
    }
    
    public void startAttack() {
        attackFrames = 15;  // Show attack animation for 15 frames (~0.25 seconds at 60fps)
        if (attackImage != null) {
            view.setImage(attackImage);
        }
    }
    
    public void updateAttack() {
        if (attackFrames > 0) {
            attackFrames--;
            if (attackFrames == 0) {
                // Return to appropriate sprite after attack
                updateJumpAnimation(); // This will handle the correct sprite based on state
            }
        }
    }
    
    public boolean isAttacking() {
        return attackFrames > 0;
    }
    /*
    public void jump() {
        if (onGround) {
            velocityY = JUMP_STRENGTH;
            onGround = false;
            // Show jump image immediately when jumping (only if not attacking)
            if (attackFrames == 0) {
                if (jumpImage != null) {
                    view.setImage(jumpImage);
                    System.out.println("Jump image set!");
                } else {
                    System.out.println("Warning: Jump image is null!");
                }
            }
        }
    } */
    public void jump() {
        if (onGround) {
            velocityY = JUMP_STRENGTH;
            onGround = false;

            // 🔊 SON DE SAUT
            if (GameManager.soundManager != null) {
                GameManager.soundManager.playSfx("jump");
            }

            if (attackFrames == 0 && jumpImage != null) {
                view.setImage(jumpImage);
            }
        }
    }

    
    public void applyGravity() {
        velocityY += GRAVITY;
        y += velocityY;
        updateViewPosition();
        
        // Update jump animation state (show jump image when in air, unless attacking)
        updateJumpAnimation();
    }
    
    private void updateJumpAnimation() {
        // If attacking, don't change to jump image
        if (attackFrames > 0) {
            return;
        }
        
        // Show jump image when in the air (not on ground)
        if (!onGround) {
            if (jumpImage != null) {
                // Always set jump image when in air (don't check current image)
                view.setImage(jumpImage);
            } else {
                System.out.println("Jump image is null! Cannot show jump animation.");
            }
        } else {
            // When on ground, return to appropriate sprite based on movement
            if (velocityX == 0) {
                if (idleImage != null) {
                    view.setImage(idleImage);
                }
            } else {
                if (walkingImage != null) {
                    view.setImage(walkingImage);
                }
            }
        }
    }
    
    /**
     * Public method to update jump animation state.
     * Called from level managers after collision checks.
     */
    public void updateJumpAnimationState() {
        updateJumpAnimation();
    }
    
    public boolean intersects(Platform platform) {
        return x < platform.getX() + platform.getWidth() &&
               x + width > platform.getX() &&
               y < platform.getY() + platform.getHeight() &&
               y + height > platform.getY();
    }
    
    private void updateViewPosition() {
        view.setX(x);
        view.setY(y);
    }
    
    // Getters and setters
    public ImageView getView() {
        return view;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public double getVelocityY() {
        return velocityY;
    }

    public int getHealth() {
        return health;
    }

    public void addHealth(int amount) {
        health = Math.min(maxHealth, health + amount);
        System.out.println("Player health: " + health);
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    /*
    public void takeDamage(int amount) {
        if (invincibilityFrames > 0) return;  // Can't take damage while invincible
        
        // Reduce hearts when taking damage
        hearts = Math.max(0, hearts - amount);
        health = Math.max(0, health - amount);
        invincibilityFrames = 60;  // 60 frames of invincibility (~1 second at 60fps)
        // Apply red damage tint
        view.setEffect(damageEffect);
        System.out.println("Player took damage! Hearts: " + hearts + ", Health: " + health);
    }*/
    public void takeDamage(int amount) {
        if (invincibilityFrames > 0) return;

        health -= amount;
        hearts -= amount;  // 🔹 Décrémenter les cœurs
        invincibilityFrames = 60;

        // 🔊 Son perte de vie
        if (GameManager.soundManager != null) {
            GameManager.soundManager.playSfx("player_lose_soul");
        }

        if (health <= 0) {
            health = 0;
        }

        if (hearts <= 0) {
            hearts = 0;
            die();
        }

        // Appliquer effet rouge
        view.setEffect(damageEffect);
        System.out.println("Player took damage! Hearts: " + hearts + ", Health: " + health);
    }

    private void die() {
        // Réduire les cœurs à 0
        hearts = 0;

        // Informer le GameManager que le joueur est mort
        // 🔹 Assurez-vous que GameManager.player.isDead() sera true dans update()
        System.out.println("Player has died!");
    }

    
    public int getHearts() {
        return hearts;
    }
    
    public boolean isDead() {
        return hearts <= 0;
    }
    
    public void updateInvincibility() {
        if (invincibilityFrames > 0) {
            invincibilityFrames--;
            if (invincibilityFrames == 0) {
                // Remove red tint when invincibility ends
                view.setEffect(null);
            }
        }
    }
    
    public boolean isInvincible() {
        return invincibilityFrames > 0;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public int getCrystalAmmo() {
        return crystalAmmo;
    }

    public void addCrystalAmmo(int amount) {
        crystalAmmo += amount;
        System.out.println("Crystal ammo: " + crystalAmmo);
    }

    public boolean hasCrystalAmmo() {
        return crystalAmmo > 0;
    }

    public void useCrystalAmmo() {
        if (crystalAmmo > 0) {
            crystalAmmo--;
            System.out.println("Crystal ammo: " + crystalAmmo);
        }
    }
    
    public void setX(double x) {
        this.x = x;
        updateViewPosition();
    }
    
    public void setY(double y) {
        this.y = y;
        updateViewPosition();
    }
    
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
    
    public boolean isOnGround() {
        return onGround;
    }
    
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.velocityY = 0;
        updateViewPosition();
    }
    
    /**
     * Resize the player to a new height (width scales proportionally)
     */
    public void setSize(double newHeight) {
        if (idleImage != null) {
            double originalWidth = idleImage.getWidth();
            double originalHeight = idleImage.getHeight();
            double scale = newHeight / originalHeight;
            
            this.height = newHeight;
            this.width = originalWidth * scale;
            
            view.setFitWidth(width);
            view.setFitHeight(height);
            updateViewPosition();
        }
    }
}
