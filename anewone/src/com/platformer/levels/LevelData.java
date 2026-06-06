package com.platformer.levels;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class that holds configuration for a level.
 * This makes it easy to add new levels without creating new manager classes.
 */
public class LevelData {
    
    public enum LevelType {
        PLATFORMER,
        COMBAT,
        PUZZLE  // For future use
    }
    
    private int levelId;
    private LevelType levelType;
    private String backgroundImage;
    private double worldWidth;
    
    // Platformer level data
    private List<PlatformData> platforms;
    private List<FlyingPlatformData> flyingPlatforms;
    private List<CrystalData> crystals;
    private List<BugEnemyData> bugEnemies;
    private List<ObstacleData> obstacles;
    private GateData gate;
    private double playerStartX;
    private double playerStartY;
    
    // Combat level data
    private double groundY;
    private double playerCombatX;
    private double monsterX;
    
    public LevelData(int levelId, LevelType levelType) {
        this.levelId = levelId;
        this.levelType = levelType;
        this.platforms = new ArrayList<>();
        this.flyingPlatforms = new ArrayList<>();
        this.crystals = new ArrayList<>();
        this.bugEnemies = new ArrayList<>();
        this.obstacles = new ArrayList<>();
    }
    
    // Getters and setters
    public int getLevelId() { return levelId; }
    public LevelType getLevelType() { return levelType; }
    public String getBackgroundImage() { return backgroundImage; }
    public void setBackgroundImage(String backgroundImage) { this.backgroundImage = backgroundImage; }
    public double getWorldWidth() { return worldWidth; }
    public void setWorldWidth(double worldWidth) { this.worldWidth = worldWidth; }
    
    // Platformer data
    public List<PlatformData> getPlatforms() { return platforms; }
    public List<FlyingPlatformData> getFlyingPlatforms() { return flyingPlatforms; }
    public List<CrystalData> getCrystals() { return crystals; }
    public List<BugEnemyData> getBugEnemies() { return bugEnemies; }
    public List<ObstacleData> getObstacles() { return obstacles; }
    public GateData getGate() { return gate; }
    public void setGate(GateData gate) { this.gate = gate; }
    public double getPlayerStartX() { return playerStartX; }
    public void setPlayerStartX(double playerStartX) { this.playerStartX = playerStartX; }
    public double getPlayerStartY() { return playerStartY; }
    public void setPlayerStartY(double playerStartY) { this.playerStartY = playerStartY; }
    
    // Combat data
    public double getGroundY() { return groundY; }
    public void setGroundY(double groundY) { this.groundY = groundY; }
    public double getPlayerCombatX() { return playerCombatX; }
    public void setPlayerCombatX(double playerCombatX) { this.playerCombatX = playerCombatX; }
    public double getMonsterX() { return monsterX; }
    public void setMonsterX(double monsterX) { this.monsterX = monsterX; }
    
    // Inner data classes
    public static class PlatformData {
        public double x, y, width;
        public PlatformData(double x, double y, double width) {
            this.x = x; this.y = y; this.width = width;
        }
    }
    
    public static class FlyingPlatformData {
        public double x, y, width;
        public FlyingPlatformData(double x, double y, double width) {
            this.x = x; this.y = y; this.width = width;
        }
    }
    
    public static class CrystalData {
        public double x, y;
        public CrystalData(double x, double y) {
            this.x = x; this.y = y;
        }
    }
    
    public static class BugEnemyData {
        public double x, y;
        public BugEnemyData(double x, double y) {
            this.x = x; this.y = y;
        }
    }
    
    public static class ObstacleData {
        public double x, y;
        public ObstacleData(double x, double y) {
            this.x = x; this.y = y;
        }
    }
    
    public static class GateData {
        public double x, y;
        public GateData(double x, double y) {
            this.x = x; this.y = y;
        }
    }
}
