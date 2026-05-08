package models;

import java.io.Serializable;

public class Amenity implements Serializable {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdditionalCost(double cost) {
        if (cost < 0)
            throw new IllegalArgumentException("Cost cannot be negative");
        this.additionalCost = cost;
    }
    @Override
    public String toString() {
        return "Name: " + getName() + " | Description: " + getDescription() + " | Additional Cost: " + getAdditionalCost();
    }
}
