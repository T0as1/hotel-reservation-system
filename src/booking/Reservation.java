package booking;
import enums.ReservationStatus;
import models.Guest;
import models.Room;
import models.Amenity;
import java.util.List;
import java.util.ArrayList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Reservation {
    private static int counter=1;
    private final int reservationID;
    private Guest guest;
    private Room room;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private ReservationStatus status;
    private List<Amenity> selectedAmenities;

    //constructor:
    public Reservation( Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate, List<Amenity> selectedAmenities)
    {
        this.reservationID = counter++  ;

        this.guest = guest;
        this.room = room;

        if (checkOutDate.isBefore(checkInDate) || checkOutDate.equals(checkInDate)) {
            throw new IllegalArgumentException("Invalid reservation dates");
        }

        this. checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.PENDING;
        this.selectedAmenities = new ArrayList<>(selectedAmenities);



    }

    public long calculateDuration()
    {

        return ChronoUnit.DAYS.between(checkInDate,checkOutDate);
    }
    public double calculateRoomCost() {
        return calculateDuration() * room.getRoomType().getPricePerNight();
    }

    public double calculateAmenitiesCost() {
        double amenitiesPerNight = 0;
        for (Amenity amenity : selectedAmenities) {
            amenitiesPerNight += amenity.getAdditionalCost();
        }
        return amenitiesPerNight * calculateDuration();
    }

    public double calculateTotalCost() {
        return calculateRoomCost() + calculateAmenitiesCost();
    }


    public boolean overlapsWith(LocalDate newStart , LocalDate newEnd)
    {
        return newStart.isBefore(this.checkOutDate) && newEnd.isAfter(this.checkInDate);
    }

//getters
    public int getReservationID() {
        return reservationID;
    }

    public ReservationStatus getStatus()
    {
        return status;
    }

    //update reservation status
    public void setStatus(ReservationStatus newStatus)
    {
        this.status = newStatus;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }
    public void cancel()
    {
        this.status = ReservationStatus.CANCELLED;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public List<Amenity> getSelectedAmenities() {
        return new ArrayList<>(selectedAmenities);
    }
}
