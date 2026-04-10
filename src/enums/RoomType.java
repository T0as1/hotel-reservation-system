package enums;

public enum RoomType {
    SINGLE (100.0),
    DOUBLE  (200.0),
    SUITE   (300.0) // different price per night for every room type


    private final double pricePerNight;

    RoomType(double price) {
        this.pricePerNight = price;
    }


    public double getPricePerNight() {
        return pricePerNight;
    }


 }



