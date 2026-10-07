package org.example.smartbiobackend.repository;

import org.example.smartbiobackend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    int countByAuditorium_Id(int auditoriumId);


    List<Seat> findAllByAuditorium_IdAndSeatCodeIn(int id, List<String> seatCodesFromRequest);

    Seat findBySeatCode(String seatCode);

    Seat findBySeatCodeAndAuditorium_Id(String s, int id);
}
