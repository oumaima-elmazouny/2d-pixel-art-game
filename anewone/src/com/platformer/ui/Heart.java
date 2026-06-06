package com.platformer.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Heart {
    private ImageView view;
    private boolean filled;
    
    public Heart() {
        try {
            Image heartImage = new Image("file:ressources/heart.png");
            view = new ImageView(heartImage);
            view.setSmooth(false);
            view.setFitWidth(30);
            view.setFitHeight(30);
            filled = true;
        } catch (Exception e) {
            System.err.println("Could not load heart.png: " + e.getMessage());
            view = new ImageView();
            view.setFitWidth(30);
            view.setFitHeight(30);
            filled = true;
        }
    }
    
    public void setFilled(boolean filled) {
        this.filled = filled;
        if (filled) {
            view.setOpacity(1.0);
        } else {
            view.setOpacity(0.3); // Dimmed when empty
        }
    }
    
    public boolean isFilled() {
        return filled;
    }
    
    public ImageView getView() {
        return view;
    }
}
