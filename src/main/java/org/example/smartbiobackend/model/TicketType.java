package org.example.smartbiobackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String ticketName;

    @Column(nullable = false)
    private int price;

    public TicketType() {
    }

    public TicketType(String ticketName, int price) {
        this.ticketName = ticketName;
        this.price = price;
    }

    public String getTicketName() {
        return ticketName;
    }

    public int getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}