package booking;

import enums.PaymentMethod;
import java.time.LocalDate;

public class Invoice {
    private static int counter = 1;
    private   int invoiceID;
    private Reservation reservation;
    private double amount;
    private boolean isPaid;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;

    public Invoice(Reservation reservation) {
        this.invoiceID = counter++;
        this.reservation = reservation;
        this.amount = reservation.calculateTotalCost();
        this.isPaid = false;
    }
    public void markAsPaid(){
        this.isPaid = true;
        System.out.println("Invoice no. " + invoiceID + "has been paid. ");
    }

    public void processPayment(PaymentMethod method)
    {
        this.paymentMethod = method;
        this.paymentDate = LocalDate.now();
        markAsPaid();
        System.out.println("PAYMENT METHOD: "+ paymentMethod);
    }

    public void printInvoice() {
        System.out.println("--- Hotel Invoice ---");
        System.out.println("Invoice ID: " + invoiceID);
        System.out.println("Guest: " + reservation.getGuest().getUsername());
        System.out.println("Room: " + reservation.getRoom().getRoomNumber());
        System.out.println("Room Type: " + reservation.getRoom().getRoomType().getName());
        System.out.println("Nights: " + reservation.calculateDuration());
        System.out.println("Room Cost: $" + reservation.calculateRoomCost());
        System.out.println("Amenities Cost: $" + reservation.calculateAmenitiesCost());

        System.out.println("Amenities:");
        if (reservation.getSelectedAmenities().isEmpty()) {
            System.out.println("None");
        } else {
            for (models.Amenity a : reservation.getSelectedAmenities()) {
                System.out.println("- " + a.getName() + " ($" + a.getAdditionalCost() + " per night)");
            }
        }

        System.out.println("Total Amount: $" + amount);
        System.out.println("Status: " + (isPaid ? "Paid" : "Unpaid"));
    }

    public LocalDate getPaymentDate() { return paymentDate; }

    public double getAmount() {
        return amount;
    }
}
