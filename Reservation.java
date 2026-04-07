package src;
import enums.ReservationStatus;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Reservation {
    private int reservationID;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
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
        return calculateDuration()*room.getRoomType().getPricePerNight();
    } //price per night is determined by room type




    public boolean overlapsWith(LocalDate newStart , LocalDate newEnd)
    {
        if (newStart.isBefore(this.checkOutDate)&& newEnd.isAfter(this.checkInDate))
            return true;
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


}
