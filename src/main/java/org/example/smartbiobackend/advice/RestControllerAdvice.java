package org.example.smartbiobackend.advice;

import org.example.smartbiobackend.exceptions.BookingNotFound;
import org.example.smartbiobackend.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {

        System.out.println("GLOBAL EXCEPTION KALDT");

        ErrorMessage message = new ErrorMessage(

                HttpStatus.INTERNAL_SERVER_ERROR.value(),

                new Date(),

                ex.getMessage() + " name:" + ex.getClass().getName(),

                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BookingNotFound.class)
    public ResponseEntity<ErrorMessage> handleBookingNotFound(BookingNotFound e, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                e.getMessage() + " exception:" + e.getClass().getName(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);

    }


    @ExceptionHandler(UserNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFound e) {
        return e.getMessage();
    }


}
