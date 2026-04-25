package booking;
import enums.ReservationStatus;
import models.Amenity;
import models.Guest;
import models.Room;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Reservation {
    private final int reservationID;
    private Guest guest;
    private Room room;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private ReservationStatus status;


    //constructor:
    public Reservation(int reservationID, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate)
    {
        this.reservationID = reservationID;
        this.guest = guest;
        this.room = room;
        this. checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.PENDING;



    }

    public long calculateDuration()
    {

        return ChronoUnit.DAYS.between(checkInDate,checkOutDate);
    }

    public double calculateTotalCost()
    {
        double totalCost = calculateDuration()*room.getRoomType().getPricePerNight();
        for (Amenity a : this.room.getAmenities()) {
            totalCost += a.getAdditionalCost();
        }
        return totalCost;
    } // price per night is determined by room type




    public boolean overlapsWith(LocalDate newStart , LocalDate newEnd)
    {
        return newStart.isBefore(this.checkOutDate) && newEnd.isAfter(this.checkInDate);
    }

    @Override
    public String toString() {
        return "ID: " + getReservationID() + " | Guest: " + getGuest().getUsername() + " | Room: "
                + getRoom().getRoomNumber() + " | Check in Date: " + this.checkInDate + " | Check out Date: "
                + this.checkOutDate + " | Status: " + getStatus();
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
    public  LocalDate getCheckInDate(){return checkInDate;}
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
}
