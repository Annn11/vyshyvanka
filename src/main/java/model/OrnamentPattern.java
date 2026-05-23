package model;

import java.awt.Color;

public class OrnamentPattern {
    private final String name;
    private final String type;
    private final String difficulty;
    private final Color[][] gridData;

    public OrnamentPattern(String name, String type, String difficulty, Color[][] gridData) {
        this.name = name;
        this.type = type;
        this.difficulty = difficulty;
        this.gridData = gridData;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getDifficulty() { return difficulty; }
    public Color[][] getGridData() { return gridData; }
}

