package com.platformer.entities.enemies;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BugEnemy {

    private ImageView view;
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed = 1.0;  // Reduced speed to make bugs slower
    private boolean alive = true;
    private boolean playerHasMoved = false;  // Track if player has moved
    
    // Walking animation: two positions
    private Image bugImage1;  // bug1.png - first walking position
    private Image bugImage2;  // bug2.png - second walking position
    private int animationStep = 0;  // Counter to alternate between positions

    public BugEnemy(double x, double groundY) {
        this.x = x;

        try {
            // Load both walking animation frames
            bugImage1 = new Image("file:ressources/bug1.png");
            bugImage2 = new Image("file:ressources/bug2.png");
            
            view = new ImageView(bugImage1);  // Start with first frame
            view.setSmooth(false);

            double targetHeight = 80;
            double targetWidth = bugImage1.getWidth() * (targetHeight / bugImage1.getHeight());
            width = targetWidth;
            height = targetHeight;

            view.setFitWidth(width);
            view.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("Could not load bug enemy sprite: " + e.getMessage());
            width = 60;
            height = 60;
            view = new ImageView();
            view.setFitWidth(width);
            view.setFitHeight(height);
        }

        // Place bug so its feet are on the ground platform
        this.y = groundY - height;
        updateViewPosition();
    }

    public void update(double playerX, boolean playerMoved) {
        if (!alive) return;
        
        // Update player movement status
        if (playerMoved) {
            playerHasMoved = true;
        }
        
        // Don't move until player has moved
        if (!playerHasMoved) return;

        // Determine direction and set scale BEFORE animation
        boolean facingLeft = playerX < x;
        if (facingLeft) {
            x -= speed;
            view.setScaleX(-1);  // Flip to face left (sprite faces right by default)
        } else {
            x += speed;
            view.setScaleX(1);   // Face right (no flip needed)
        }
        
        // Walking animation: alternate between bug1.png and bug2.png every step
        animationStep++;
        if (animationStep % 10 == 0) {  // Change frame every 10 steps (adjust for animation speed)
            if (view.getImage() == bugImage1) {
                view.setImage(bugImage2);
            } else {
                view.setImage(bugImage1);
            }
            // Re-apply the scale after changing image to maintain direction
            if (facingLeft) {
                view.setScaleX(-1);  // Face left (flip)
            } else {
                view.setScaleX(1);   // Face right (no flip)
            }
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

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }
    
    public void setX(double x) {
        this.x = x;
        updateViewPosition();
    }
}
