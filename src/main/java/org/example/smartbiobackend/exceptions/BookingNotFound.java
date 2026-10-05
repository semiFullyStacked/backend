package org.example.smartbiobackend.exceptions;

public class BookingNotFound extends RuntimeException {
    public BookingNotFound(String message) {
        super(message);
    }
}
