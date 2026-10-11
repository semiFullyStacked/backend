package org.example.smartbiobackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfBootstrapController {

    @GetMapping("/api/csrf-init")
    public ResponseEntity<Void> init() {
        return ResponseEntity.noContent().build(); // 204; CsrfCookieFilter writes the cookie
    }
}
