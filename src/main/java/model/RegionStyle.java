package model;

import java.awt.Color;

public class RegionStyle {
    private final String name;
    private final String description;
    private final Color[] mainColors;
    private final String ornaments;
    private final String techniques;

    public RegionStyle(String name, String description, Color[] mainColors, String ornaments, String techniques) {
        this.name = name;
        this.description = description;
        this.mainColors = mainColors;
        this.ornaments = ornaments;
        this.techniques = techniques;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Color[] getMainColors() { return mainColors; }
    public String getOrnaments() { return ornaments; }
    public String getTechniques() { return techniques; }
}
