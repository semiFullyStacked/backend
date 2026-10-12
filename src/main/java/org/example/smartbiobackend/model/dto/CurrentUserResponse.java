package org.example.smartbiobackend.model.dto;

import java.util.List;

public record CurrentUserResponse(String username, List<String> roles) {
}
