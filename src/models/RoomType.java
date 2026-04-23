package models;


public class RoomType {
    private String name;
    private double pricePerNight;
    private String description;
    private int capacity;
    public RoomType(String name, double pricePerNight, int capacity, String description) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");

        if (pricePerNight < 0)
            throw new IllegalArgumentException("Price cannot be negative");

        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive");

        this.name = name;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public double getPricePerNight() {
        return pricePerNight;
    }
    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + " | Price Per Night: " + getPricePerNight() + " | Description: "
                + getDescription() + " | Capacity: " + getCapacity();
    }
}