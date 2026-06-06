package com.platformer.levels;

/**
 * Configuration class that defines all levels in the game.
 * To add a new level, just create a new method here and add it to getLevelData().
 */
public class LevelConfig {
    
    private static final double GAME_WIDTH = 960;
    private static final double GAME_HEIGHT = 500;
    
    /**
     * Get level data for a specific level ID.
     */
    public static LevelData getLevelData(int levelId) {
        switch (levelId) {
            case 1:
                return createLevel1();
            case 2:
                return createCombatLevel1();
            default:
                throw new IllegalArgumentException("Level " + levelId + " does not exist");
        }
    }
    
    /**
     * Create Level 1 (Platformer level)
     */
    private static LevelData createLevel1() {
        LevelData data = new LevelData(1, LevelData.LevelType.PLATFORMER);
        data.setBackgroundImage("background.png");
        
        double platformHeight = 100;
        double platformY = GAME_HEIGHT - platformHeight;
        double platformPieceWidth = 120;
        double worldWidth = 3000;
        double platformEndX = worldWidth + GAME_WIDTH;
        
        data.setWorldWidth(platformEndX);
        data.setPlayerStartX(100);
        data.setPlayerStartY(platformY - 120);
        
        // Create platforms
        int numberOfPieces = (int) Math.ceil(platformEndX / platformPieceWidth);
        for (int i = 0; i < numberOfPieces; i++) {
            double x = i * platformPieceWidth;
            data.getPlatforms().add(new LevelData.PlatformData(x, platformY, platformPieceWidth));
        }
        
        // Gate
        double gateX = platformEndX - 350;
        data.setGate(new LevelData.GateData(gateX, platformY));
        
        // Flying platforms
        double flyingYLow = platformY - 120;
        double flyingYMid = platformY - 170;
        double flyingYHigh = platformY - 220;
        data.getFlyingPlatforms().add(new LevelData.FlyingPlatformData(350, flyingYLow, 220));
        data.getFlyingPlatforms().add(new LevelData.FlyingPlatformData(650, flyingYMid, 220));
        data.getFlyingPlatforms().add(new LevelData.FlyingPlatformData(950, flyingYLow, 220));
        data.getFlyingPlatforms().add(new LevelData.FlyingPlatformData(1350, flyingYMid, 220));
        data.getFlyingPlatforms().add(new LevelData.FlyingPlatformData(1700, flyingYHigh, 220));
        data.getFlyingPlatforms().add(new LevelData.FlyingPlatformData(2100, flyingYMid, 220));
        
        // Crystals
        double minX = 150;
        double maxX = GAME_WIDTH - 150;
        for (int i = 0; i < 3; i++) {
            double offset = i * (GAME_WIDTH / 2.0);
            double baseX = minX + Math.random() * (maxX - minX);
            double crystalX = baseX + offset;
            data.getCrystals().add(new LevelData.CrystalData(crystalX, platformY - 40));
        }
        
        // Crystals on flying platforms
        for (LevelData.FlyingPlatformData fp : data.getFlyingPlatforms()) {
            double cx = fp.x + fp.width / 2.0 - 20;
            double cy = fp.y - 40;
            data.getCrystals().add(new LevelData.CrystalData(cx, cy));
        }
        
        // Bugs
        data.getBugEnemies().add(new LevelData.BugEnemyData(900, platformY));
        data.getBugEnemies().add(new LevelData.BugEnemyData(1700, platformY));
        data.getBugEnemies().add(new LevelData.BugEnemyData(2500, platformY));
        
        // Obstacle after the 6th flying platform (after X=2100) as barrier
        // Place it on the main platform, moved to the right, positioned lower
        double obstacleX = 2600; // Moved further to the right
        double obstacleY = platformY - 50; // Lower on the platform (was -80, now -50)
        
        // Add only 1 obstacle as barrier
        data.getObstacles().add(new LevelData.ObstacleData(obstacleX, obstacleY));
        
        return data;
    }
    
    /**
     * Create Combat Level 1
     */
    private static LevelData createCombatLevel1() {
        LevelData data = new LevelData(2, LevelData.LevelType.COMBAT);
        data.setBackgroundImage("combat_back.png");
        data.setWorldWidth(GAME_WIDTH);
        // Ground Y position to match the horizon in back.jpg (approximately 480px from top, 20px from bottom)
        // Positioned so character feet align with the ground collision layer
        data.setGroundY(480);
        data.setPlayerCombatX(GAME_WIDTH / 4.0);
        data.setMonsterX(GAME_WIDTH * 0.6); // Shifted left from 0.75 to 0.6
        return data;
    }
}
