package com.platformer.entities.platforms;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Platform {
    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;
    
    public Platform(double x, double y, double width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 100; // Bigger platform height
        
        // Load platform sprite - just the raw image, no modifications
        try {
            Image platformImage = new Image("file:ressources/Plateforme.png");
            view = new ImageView(platformImage);
            view.setSmooth(false);
            view.setPreserveRatio(false);
            
            // Scale platform to desired width and height
            double originalWidth = platformImage.getWidth();
            double originalHeight = platformImage.getHeight();
            
            // Calculate scale to make height 50px
            double heightScale = height / originalHeight;
            this.height = originalHeight * heightScale;
            
            // Set the width and height
            view.setFitWidth(width);
            view.setFitHeight(this.height);
            
        } catch (Exception e) {
            System.err.println("Could not load platform sprite: " + e.getMessage());
            // Create a placeholder if image fails
            view = new ImageView();
            view.setFitWidth(width);
            view.setFitHeight(height);
        }
        
        updateViewPosition();
    }
    
    private void updateViewPosition() {
        view.setX(x);
        view.setY(y);
    }
    
    // Getters
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
}
