package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.BookingSummaryDTO;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.example.smartbiobackend.service.ShowingBookingsService;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/showings")
public class ShowingBookingsController {

    private final ShowingBookingsService showingBookingsService;
    private final ShowingRepository showingRepository;

    public ShowingBookingsController(ShowingBookingsService showingBookingsService, ShowingRepository showingRepository) {
        this.showingBookingsService = showingBookingsService;
        this.showingRepository = showingRepository;
    }

    @GetMapping("/showingsort")
    public ResponseEntity<Map<String, Object>> getPageOfShowings
            (@RequestParam(defaultValue = "movie.name,asc") String[] sort,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size) {

        for (String s : sort) {
            System.out.println(s);
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        Pageable paging = PageRequest.of(page, size, Sort.by(orders));

        Page<Showing> showings = showingRepository.findAll(paging);

        Map<String, Object> response = new HashMap<>();
        response.put("users", showings);
        response.put("currentUserPage", showings.getNumber());
        response.put("totalItems", showings.getTotalElements());
        response.put("totalPages", showings.getTotalPages());
        if (showings.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    @GetMapping("/{showingId}/bookings")
    public List<BookingSummaryDTO> getBookings(@PathVariable int showingId) {
        return showingBookingsService.getBookingsForShowing(showingId);
    }
}