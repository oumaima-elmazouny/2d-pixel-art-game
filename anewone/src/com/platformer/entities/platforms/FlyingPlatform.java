package com.platformer.entities.platforms;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FlyingPlatform {

    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;

    public FlyingPlatform(double x, double y, double width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 80;

        try {
            Image platformImage = new Image("file:ressources/flying_platformm.png");
            view = new ImageView(platformImage);
            view.setSmooth(false);
            view.setPreserveRatio(false);

            double originalWidth = platformImage.getWidth();
            double originalHeight = platformImage.getHeight();

            double heightScale = height / originalHeight;
            this.height = originalHeight * heightScale;

            view.setFitWidth(width);
            view.setFitHeight(this.height);

        } catch (Exception e) {
            System.err.println("Could not load flying platform sprite: " + e.getMessage());
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
