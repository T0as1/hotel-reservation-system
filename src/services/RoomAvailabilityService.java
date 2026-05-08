package services;

import data.HotelDatabase;
import models.Room;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.time.LocalDate;
import java.util.List;


public class RoomAvailabilityService extends Service<List<Room>> {

    private LocalDate checkIn  = LocalDate.now();
    private LocalDate checkOut = LocalDate.now().plusDays(1);

    public void setDates(LocalDate in, LocalDate out) {
        this.checkIn  = in;
        this.checkOut = out;
    }

    @Override
    protected Task<List<Room>> createTask() {
        final LocalDate in  = checkIn;
        final LocalDate out = checkOut;

        return new Task<>() {
            @Override
            protected List<Room> call() throws Exception {
                updateMessage("Checking availability...");
                // Simulate slight network/DB delay so threading is visible
                Thread.sleep(600);
                List<Room> rooms = HotelDatabase.getAvailableRooms(in, out);
                updateMessage("Found " + rooms.size() + " available rooms.");
                return rooms;
            }
        };
    }
}
