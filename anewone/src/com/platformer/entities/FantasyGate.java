package com.platformer.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FantasyGate {

    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;

    public FantasyGate(double x, double groundY) {
        this.x = x;

        try {
            Image gateImage = new Image("file:ressources/fantasy_gate.png");
            view = new ImageView(gateImage);
            view.setSmooth(false);

            double targetHeight = 280; // Increased from 180 to make gate bigger
            double targetWidth = gateImage.getWidth() * (targetHeight / gateImage.getHeight());
            width = targetWidth;
            height = targetHeight;

            view.setFitWidth(width);
            view.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("Could not load fantasy gate sprite: " + e.getMessage());
            width = 180;
            height = 280;
            view = new ImageView();
            view.setFitWidth(width);
            view.setFitHeight(height);
        }

        // Place gate so its base sits on the ground platform, lowered a bit
        this.y = groundY - height + 30; // Added 30 pixels to lower the gate
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
