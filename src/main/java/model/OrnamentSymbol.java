package model;

public class OrnamentSymbol {
    private final String name;
    private final String meaning;
    private final String description;

    public OrnamentSymbol(String name, String meaning, String description) {
        this.name = name;
        this.meaning = meaning;
        this.description = description;
    }

    public String getName() { return name; }
    public String getMeaning() { return meaning; }
    public String getDescription() { return description; }
}
