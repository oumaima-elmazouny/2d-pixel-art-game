package com.platformer.ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HealthBar {

    private Pane container;
    private Rectangle background;
    private Rectangle foreground;

    private double x;
    private double y;
    private double width;
    private double height;
    private int maxHealth;

    public HealthBar(double x, double y, double width, double height, int maxHealth) {
        this(x, y, width, height, maxHealth, Color.rgb(80, 160, 255)); // Default blue
    }
    
    public HealthBar(double x, double y, double width, double height, int maxHealth, Color healthColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;

        container = new Pane();
        container.setLayoutX(x);
        container.setLayoutY(y);

        background = new Rectangle(0, 0, width, height);
        background.setFill(Color.rgb(20, 20, 30, 0.8));
        background.setStroke(Color.WHITE);
        background.setArcWidth(8);
        background.setArcHeight(8);

        foreground = new Rectangle(0, 0, width, height);
        foreground.setFill(healthColor);
        foreground.setArcWidth(8);
        foreground.setArcHeight(8);

        container.getChildren().addAll(background, foreground);
    }

    public void update(int currentHealth) {
        double ratio = Math.max(0, Math.min(1, currentHealth / (double) maxHealth));
        foreground.setWidth(width * ratio);
    }
    
    public void update(double currentHealth) {
        double ratio = Math.max(0, Math.min(1, currentHealth / (double) maxHealth));
        foreground.setWidth(width * ratio);
    }

    public Pane getView() {
        return container;
    }
}
