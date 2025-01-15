package com.example.librarymanagementsystem.Backend.Models;

import java.sql.Date;

public class BorrowRecord {
    private int id;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private int overdueDays;
    private Book book;
    private Member member;

    public BorrowRecord(int id, Date borrowDate, Date dueDate, Book book, Member member) {
        this.id = id;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.book = book;
        this.member = member;
        this.overdueDays = 0;
    }
    public Date getBorrowDate() {

        return borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public int getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public int calculateOverdueDays(Date day) {
        if (day == null || !day.after(dueDate)) {
            return 0;
        }

        // Calculate the difference in milliseconds
        long differenceInMillis = day.getTime() - dueDate.getTime();

        // Convert milliseconds to days
        this.overdueDays = (int) (differenceInMillis / (24 * 60 * 60 * 1000));

        return this.overdueDays;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Member getMember() {
        return member;
    }
}
