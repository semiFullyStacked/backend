package org.example.smartbiobackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errors/access-denied")
public class ErrorPage {

    @GetMapping()
    public String returnErrorPage() {
        return "403";
    }
}
