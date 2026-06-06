package com.platformer.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;

public class HomeScreen {
    private Pane container;
    private ImageView background;
    private ImageView startButton;
    private boolean startClicked = false;
    
    private static final double GAME_WIDTH = 960;
    private static final double GAME_HEIGHT = 500;
    
    public HomeScreen(EventHandler<MouseEvent> startButtonHandler) {
        container = new Pane();
        container.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        
        // Load background
        try {
            Image bgImage = new Image("file:ressources/home_screen.jpg");
            background = new ImageView(bgImage);
            background.setFitWidth(GAME_WIDTH);
            background.setFitHeight(GAME_HEIGHT);
            background.setPreserveRatio(false);
            background.setX(0);
            background.setY(0);
            container.getChildren().add(background);
        } catch (Exception e) {
            System.err.println("Could not load home_screen.jpg: " + e.getMessage());
        }
        
        // Load start button
        try {
            Image buttonImage = new Image("file:ressources/start_button.png");
            startButton = new ImageView(buttonImage);
            startButton.setSmooth(false);
            
            // Center the button on screen
            double buttonWidth = 200; // Adjust size as needed
            double buttonHeight = buttonImage.getHeight() * (buttonWidth / buttonImage.getWidth());
            startButton.setFitWidth(buttonWidth);
            startButton.setFitHeight(buttonHeight);
            
            // Position button in center
            double buttonX = (GAME_WIDTH - buttonWidth) / 2.0;
            double buttonY = GAME_HEIGHT * 0.7; // 70% down the screen
            startButton.setX(buttonX);
            startButton.setY(buttonY);
            
            // Make button clickable
            startButton.setPickOnBounds(true); // Enable mouse events on the ImageView
            startButton.setOnMouseClicked(startButtonHandler);
            startButton.setStyle("-fx-cursor: hand;"); // Show hand cursor on hover
            
            container.getChildren().add(startButton);
        } catch (Exception e) {
            System.err.println("Could not load start_button.png: " + e.getMessage());
        }
    }
    
    public Pane getView() {
        return container;
    }
    
    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }
    
    public boolean isVisible() {
        return container.isVisible();
    }
}
