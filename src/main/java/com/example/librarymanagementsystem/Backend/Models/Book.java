package com.example.librarymanagementsystem.Backend.Models;

public class Book {
    private int id;
    private boolean availability;
    private String publicationDate;
    private String title;
    private String genre;
    private Author author;

    public Book(int id, String publicationDate, String title, String genre, Author author, boolean availability) {
        this.availability = availability;
        this.id = id;
        this.publicationDate = publicationDate;
        this.title = title;
        this.genre = genre;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public boolean getAvailability() {
        return availability;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Author getAuthor() {
        return author;
    }
}
