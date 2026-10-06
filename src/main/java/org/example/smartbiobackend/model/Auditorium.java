package org.example.smartbiobackend.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Auditorium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String auditoriumName;

    @Nullable
    private LocalDateTime lastCleanedAt;

    public Auditorium() {
    }
    public Auditorium(String auditoriumName) {
        this.auditoriumName = auditoriumName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuditoriumName() {
        return auditoriumName;
    }

    public void setAuditoriumName(String auditoriumName) {
        this.auditoriumName = auditoriumName;
    }

    @Nullable
    public LocalDateTime getLastCleanedAt() {
        return lastCleanedAt;
    }

    public void setLastCleanedAt(@Nullable LocalDateTime lastCleanedAt) {
        this.lastCleanedAt = lastCleanedAt;
    }

    @Override
    public String toString() {
        return "Auditorium{" +
                "id=" + id +
                ", auditoriumName='" + auditoriumName + '\'' +
                '}';
    }
}
