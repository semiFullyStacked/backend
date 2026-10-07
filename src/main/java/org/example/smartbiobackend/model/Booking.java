package org.example.smartbiobackend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn
    private Showing showing;

    @ManyToOne
    @JoinColumn
    private User user;


    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    // To handle unconfirmed bookings
    private boolean isPaid = false;
      public Booking(Showing showing, User user, List<BookingSeat> seats) {
        this.showing = showing;
        this.user = user;
        this.bookingSeats = seats;
    }



    public Booking(Showing showing, String customerName, String customerEmail) {
        this.showing = showing;
        this.customerName = customerName;
        this.customerEmail = customerEmail;

    }

    public Booking() {
        //TODO Auto-generated constructor stub
    }

    public Booking(Showing showing, User user) {
          this.showing = showing;
          this.user = user;
          this.customerName = user.getName();
          this.customerEmail = user.getEmail();
    }

    public void setBookingSeats(List<BookingSeat> bookingSeats) {
        this.bookingSeats = bookingSeats;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public int getId() {
        return id;
    }

    public void setId(int bookingId) {
        this.id = bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Showing getShowing() {
        return showing;
    }

    public void setShowing(Showing showing) {
        this.showing = showing;
    }
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", showing=" + showing +
                ", user=" + user +
                ", seat=" + bookingSeats +
                '}';
    }

    public List<BookingSeat> getBookingSeats() {
        return bookingSeats;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }


}
