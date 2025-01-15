package com.example.librarymanagementsystem.Backend.Models;

public class Fine {
    private int id;
    private double amount;
    private boolean isPaid;

    public Fine(int id, double amount, boolean isPaid) {
        this.id = id;
        this.amount = amount;
        this.isPaid = isPaid;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
