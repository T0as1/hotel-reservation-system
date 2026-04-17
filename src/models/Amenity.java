package models;

public class Amenity {
    private String name;
    private String description;

    public Amenity(String name, String description) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Amenity name cannot be empty");
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
