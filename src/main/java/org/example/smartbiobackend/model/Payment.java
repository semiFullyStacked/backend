package org.example.smartbiobackend.model;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

@Entity
public class Payment {

    public Payment(float amount, User user, Booking booking) {
        this.amount = amount;
        this.user = user;
        this.booking = booking;
    }

    public Payment() {
    }
    @OneToOne
    @JoinColumn(name = "booking_id")
    Booking booking;

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public int id;

   public float amount;

   @Value("DKK")
   public String currency;



   @ManyToOne
   @JoinColumn(name = "user_id")
   public User user;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
