package com.platformer.core;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class BackgroundManager {
    private Pane container;
    private ImageView backgroundView;
    private double width;
    private double height;
    
    public BackgroundManager(double width, double height) {
        this.width = width;
        this.height = height;
        this.container = new Pane();
        
        loadBackground();
    }
    
    private void loadBackground() {
        try {
            Image bgImage = new Image("file:ressources/background.png");
            backgroundView = new ImageView(bgImage);
            backgroundView.setSmooth(false);
            backgroundView.setFitWidth(width);
            backgroundView.setFitHeight(height);
            backgroundView.setPreserveRatio(false);
            backgroundView.setX(0);
            backgroundView.setY(0);
            
            container.getChildren().add(backgroundView);
        } catch (Exception e) {
            System.err.println("Could not load background: " + e.getMessage());
            backgroundView = null;
        }
    }
    
    public void update(double cameraX) {
        // Background can scroll with parallax if needed, but for now it's static
        if (backgroundView != null) {
            // Optional: Add subtle parallax scrolling
            // backgroundView.setTranslateX(-cameraX * 0.1);
        }
    }
    
    public void resize(double newWidth, double newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        if (backgroundView != null) {
            backgroundView.setFitWidth(width);
            backgroundView.setFitHeight(height);
        }
    }
    
    public Pane getView() {
        return container;
    }
}
