package com.platformer.entities.items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CrystalProjectile {

    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed;
    private boolean active = true;

    public CrystalProjectile(double x, double y, boolean toRight) {
        this.x = x;
        this.y = y;
        this.speed = toRight ? 8.0 : -8.0;

        try {
            Image crystalImage = new Image("file:ressources/Crystal_Reward.png");
            view = new ImageView(crystalImage);
            view.setSmooth(false);

            double targetHeight = 30;
            double targetWidth = crystalImage.getWidth() * (targetHeight / crystalImage.getHeight());
            width = targetWidth;
            height = targetHeight;

            view.setFitWidth(width);
            view.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("Could not load crystal projectile sprite: " + e.getMessage());
            width = 20;
            height = 20;
            view = new ImageView();
            view.setFitWidth(width);
            view.setFitHeight(height);
        }

        updateViewPosition();
    }

    public void update() {
        if (!active) return;
        x += speed;
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

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
