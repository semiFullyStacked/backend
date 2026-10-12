package org.example.smartbiobackend.model.dto;

import java.util.List;

public record MovieWithShowingsDTO(int movieId, String title, int ageRestriction, List<ShowingSummaryDTO> showings) {}