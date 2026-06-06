package com.platformer.levels;

import javafx.scene.layout.Pane;

/**
 * Interface for all level managers.
 * Each level manager handles its own initialization, updates, and cleanup.
 */
public interface LevelManager {
    /**
     * Initialize the level - create objects, set up the world
     */
    void initialize();
    
    /**
     * Update the level each frame
     */
    void update();
    
    /**
     * Get the world pane for this level
     */
    Pane getWorld();
    
    /**
     * Clean up resources when leaving the level
     */
    void cleanup();
    
    /**
     * Check if the level is complete
     */
    boolean isComplete();
}
