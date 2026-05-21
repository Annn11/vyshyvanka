package model;

public class HistoryPeriod {
    private final String year;
    private final String title;
    private final String description;

    public HistoryPeriod(String year, String title, String description) {
        this.year = year;
        this.title = title;
        this.description = description;
    }

    public String getYear() { return year; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
