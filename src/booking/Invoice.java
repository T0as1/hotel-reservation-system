package booking;

import enums.PaymentMethod;
import java.time.LocalDate;

public class Invoice {
    private int invoiceID;
    private Reservation reservation;
    private double amount;
    private boolean isPaid;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;

    public Invoice(int invoiceID, Reservation reservation) {
        this.invoiceID = invoiceID;
        this.reservation = reservation;
        this.amount = reservation.calculateTotalCost();
        this.isPaid = false;
    }
    public void markAsPaid(){
        this.isPaid = true;
        System.out.println("Invoice no. " + invoiceID + " has been paid. ");
    }

    public void processPayment(PaymentMethod method)
    {
        this.paymentMethod = method;
        this.paymentDate = reservation.getCheckOutDate();
        markAsPaid();
        System.out.println("PAYMENT METHOD: "+ method);
    }

    public void printInvoice() {
        System.out.println("---Hotel Invoice---");
        processPayment(this.paymentMethod);
        System.out.println("Guest: " + reservation.getGuest().getUsername());
        System.out.println("Total Amount: " + amount);
        System.out.println("Status: " + (isPaid ? "Paid" : "Unpaid"));
        System.out.println("----------------");
    }

    public LocalDate getPaymentDate() { return paymentDate; }

    public double getAmount() {
        return reservation.calculateTotalCost();
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
