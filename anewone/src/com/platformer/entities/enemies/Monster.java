package com.platformer.entities.enemies;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Monster {
    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed = 1.5;
    private boolean alive = true;
    private int health = 5;
    private int maxHealth = 5;
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN_TIME = 60; // Frames between attacks
    
    public Monster(double x, double y) {
        this.x = x;
        this.y = y;
        
        try {
            Image monsterImage = new Image("file:ressources/monstre.png");
            view = new ImageView(monsterImage);
            view.setSmooth(false);
            
            double targetHeight = 187; // Smaller size for combat
            double targetWidth = monsterImage.getWidth() * (targetHeight / monsterImage.getHeight());
            width = targetWidth;
            height = targetHeight;
            
            view.setFitWidth(width);
            view.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("Could not load monster sprite: " + e.getMessage());
            width = 100;
            height = 150;
            view = new ImageView();
            view.setFitWidth(width);
            view.setFitHeight(height);
        }
        
        updateViewPosition();
    }
    
    public void update(double playerX, double playerY) {
        if (!alive) return;
        
        // Monster is stationary - just face the player
        boolean facingLeft = playerX < x;
        if (facingLeft) {
            view.setScaleX(-1);  // Flip to face left
        } else {
            view.setScaleX(1);   // Face right
        }
        
        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        updateViewPosition();
    }
    
    public boolean canAttack() {
        return attackCooldown == 0;
    }
    
    public void attack() {
        attackCooldown = ATTACK_COOLDOWN_TIME;
    }
    
    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
        if (health <= 0) {
            alive = false;
        }
    }
    
    private void updateViewPosition() {
        view.setX(x);
        view.setY(y);
    }
    
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
    
    public void setX(double x) {
        this.x = x;
        updateViewPosition();
    }
    
    public void setY(double y) {
        this.y = y;
        updateViewPosition();
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
}
