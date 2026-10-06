package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/{userId}/change/{name}")
    public void nameChange(@PathVariable int userId, @PathVariable String name){
        userService.changeName(userId,name);
    }

    @PostMapping("/{userId}/change/{email}")
    public void emailChange(@PathVariable int userId, @PathVariable String email){
        userService.changeEmail(userId,email);
    }
}
