package model;

import java.awt.Color;

public class ProjectPreview {
    private final String name;
    private final String lastModified;
    private final Color[][] patternPreview; // Матриця кольорів для міні-схеми

    public ProjectPreview(String name, String lastModified, Color[][] patternPreview) {
        this.name = name;
        this.lastModified = lastModified;
        this.patternPreview = patternPreview;
    }

    public String getName() { return name; }
    public String getLastModified() { return lastModified; }
    public Color[][] getPatternPreview() { return patternPreview; }
}
