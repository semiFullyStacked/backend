package org.example.smartbiobackend.advice;

import org.example.smartbiobackend.exceptions.BookingNotFound;
import org.example.smartbiobackend.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {

    @ExceptionHandler(BookingNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBookingNotFound(BookingNotFound e) {
        return e.getMessage();
    }


    @ExceptionHandler(UserNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFound e) {
        return e.getMessage();
    }


   }
