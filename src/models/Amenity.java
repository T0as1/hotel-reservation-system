package models;

public class Amenity {
    private String name;
    private String description;
    private double additionalCost;

    public Amenity(String name, String description, double additionalCost) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Amenity name cannot be empty");
        this.name = name;
        this.description = description;
        this.additionalCost = additionalCost;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public double getAdditionalCost() {
        return additionalCost;
    }
}
