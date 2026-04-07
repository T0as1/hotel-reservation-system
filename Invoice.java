package com.hotel.booking;

public class Invoice {
    private int invoiceID;
    private Reservation reservation;
    private double amount;
    private boolean isPaid;
    private String paymentMethod;

    public Invoice(int invoiceID, Reservation reservation) {
        this.invoiceID = invoiceID;
        this.reservation = reservation;
        this.amount = reservation.calculateTotalCost();
        this.isPaid = false;
    }

    public void printInvoice() {
        System.out.println("--- Hotel Invoice ---");
        System.out.println("Guest: " + reservation.getGuest().getUsername());
        System.out.println("Total Amount: $" + amount);
        System.out.println("Status: " + (isPaid ? "Paid" : "Unpaid"));
    }

}
