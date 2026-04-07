package com.hotel.booking;
import com.hotel.enums.ReservationStatus;
import com.hotel.models.Guest;
import com.hotel.models.Room;


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
    public Reservation(int reservationID, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate, ReservationStatus status)
    {
        this.reservationID = reservationID;
        this.guest = guest;
        this.room = room;
        this. checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;



    }

    public long calculateDuration()
    {
        return ChronoUnit.DAYS.between(checkInDate,checkOutDate);
    }

    public double calculateTotalCost()
    {
        return calculateDuration()*room.getRoomType().getPricePerNight();
    }

    public ReservationStatus getStatus()
    {
        return status;
    }
    public void setStatus(ReservationStatus status)
    {
        this.status = status;
    }


}
