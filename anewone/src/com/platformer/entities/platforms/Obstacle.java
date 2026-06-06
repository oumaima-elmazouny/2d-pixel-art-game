package com.platformer.entities.platforms;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Obstacle {
    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;
    
    public Obstacle(double x, double y) {
        this.x = x;
        this.y = y;
        
        try {
            Image obstacleImage = new Image("file:ressources/obstacle.png");
            view = new ImageView(obstacleImage);
            view.setSmooth(false);
            
            double targetHeight = 80;
            double targetWidth = obstacleImage.getWidth() * (targetHeight / obstacleImage.getHeight());
            width = targetWidth;
            height = targetHeight;
            
            view.setFitWidth(width);
            view.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("Could not load obstacle.png: " + e.getMessage());
            width = 60;
            height = 80;
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
