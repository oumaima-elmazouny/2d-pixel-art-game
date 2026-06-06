package com.platformer.entities.items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CrystalReward {

    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;

    public CrystalReward(double x, double y) {
        this.x = x;
        this.y = y;

        try {
            Image crystalImage = new Image("file:ressources/Crystal_Reward.png");
            view = new ImageView(crystalImage);
            view.setSmooth(false);

            // Scale crystal a bit smaller
            double targetHeight = 40;
            double targetWidth = crystalImage.getWidth() * (targetHeight / crystalImage.getHeight());
            width = targetWidth;
            height = targetHeight;

            view.setFitWidth(width);
            view.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("Could not load crystal reward sprite: " + e.getMessage());
            width = 40;
            height = 40;
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
