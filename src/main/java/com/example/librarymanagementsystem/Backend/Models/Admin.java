package com.example.librarymanagementsystem.Backend.Models;

public class Admin implements User {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    public Admin(int id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getId() {
        return id;
    }
}
