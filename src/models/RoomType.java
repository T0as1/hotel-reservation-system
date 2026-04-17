package models;


public class RoomType {
    private String name;
    private double pricePerNight;
    private int capacity;
    public RoomType(String name, double pricePerNight, int capacity) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");

        if (pricePerNight < 0)
            throw new IllegalArgumentException("Price cannot be negative");

        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive");

        this.name = name;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
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
}