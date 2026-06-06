package com.platformer.core;

import javafx.scene.layout.Pane;

public class Camera {
    private double x;
    private double y;
    private double width;
    private double height;
    private double worldWidth;
    private double followSpeed = 0.1; // Smooth camera following
    
    public Camera(double width, double height) {
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;
        this.worldWidth = 0; // Will be set later
    }
    
    public void setWorldWidth(double worldWidth) {
        this.worldWidth = worldWidth;
    }
    
    public void update(double targetX, double targetY) {
        // Center camera on target (player)
        double targetCameraX = targetX - width / 2;
        double targetCameraY = targetY - height / 2;
        
        // Smooth camera movement
        x += (targetCameraX - x) * followSpeed;
        y += (targetCameraY - y) * followSpeed;
        
        // Keep camera within bounds
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        // Clamp camera to prevent going beyond world (but allow showing end of world)
        if (worldWidth > 0 && x > worldWidth - width) {
            x = worldWidth - width;
        }
    }
    
    public void updateHorizontalOnly(double targetX) {
        // Center camera on target (player) horizontally only, keep Y at 0
        double targetCameraX = targetX - width / 2;
        
        // Smooth camera movement
        x += (targetCameraX - x) * followSpeed;
        y = 0; // Keep camera at top
        
        // Keep camera within bounds
        if (x < 0) x = 0;
        if (worldWidth > 0 && x > worldWidth - width) {
            x = worldWidth - width;
        }
    }
    
    public void applyTo(Pane world) {
        world.setTranslateX(-x);
        world.setTranslateY(-y);
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
}
