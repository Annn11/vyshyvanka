package model;

import java.awt.Color;

public class ProjectPreview {
    private final String name;
    private final String lastModified;
    private final Color[][] patternPreview; // Міні-схема для картки у вкладці «Моя колекція»
    private final Color[][] fullPattern;    // Повна схема, щоб можна було відкрити її назад у конструкторі

    public ProjectPreview(String name, String lastModified, Color[][] patternPreview) {
        this(name, lastModified, patternPreview, patternPreview);
    }

    public ProjectPreview(String name, String lastModified, Color[][] patternPreview, Color[][] fullPattern) {
        this.name = name;
        this.lastModified = lastModified;
        this.patternPreview = copyMatrix(patternPreview);
        this.fullPattern = copyMatrix(fullPattern);
    }

    public String getName() { return name; }
    public String getLastModified() { return lastModified; }
    public Color[][] getPatternPreview() { return copyMatrix(patternPreview); }
    public Color[][] getFullPattern() { return copyMatrix(fullPattern); }

    private static Color[][] copyMatrix(Color[][] source) {
        if (source == null) return new Color[0][0];

        Color[][] copy = new Color[source.length][];
        for (int r = 0; r < source.length; r++) {
            copy[r] = new Color[source[r].length];
            System.arraycopy(source[r], 0, copy[r], 0, source[r].length);
        }
        return copy;
    }
}
