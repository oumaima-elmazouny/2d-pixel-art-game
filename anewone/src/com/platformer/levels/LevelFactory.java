package com.platformer.levels;

import com.platformer.core.Player;
import com.platformer.core.Camera;
import com.platformer.core.BackgroundManager;
import com.platformer.levels.platformer.PlatformerLevelManager;
import com.platformer.levels.combat.CombatLevelManager;
import javafx.scene.layout.Pane;

/**
 * Factory class that creates the appropriate LevelManager based on level data.
 */
public class LevelFactory {
    
    public static LevelManager createLevelManager(LevelData levelData, Pane world, Player player, Camera camera, BackgroundManager backgroundManager) {
        switch (levelData.getLevelType()) {
            case PLATFORMER:
                return new PlatformerLevelManager(world, player, camera, backgroundManager, levelData);
            case COMBAT:
                return new CombatLevelManager(world, player, camera, levelData);
            default:
                throw new IllegalArgumentException("Unknown level type: " + levelData.getLevelType());
        }
    }
}
