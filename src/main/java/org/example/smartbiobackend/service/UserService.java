package org.example.smartbiobackend.service;

import org.example.smartbiobackend.exceptions.EmailAlreadyInUseException;
import org.example.smartbiobackend.exceptions.UserNotFoundException;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public void changeEmail(int userId, String email) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));
            user.setEmail(email);
            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyInUseException("Error processing user request to change email. Printing stack" +
                    "trace: " + e);
        }
    }

    @Transactional
    public void changeName(int userId, String name) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));
            user.setName(name);
            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error processing user request to change name. Printing stack" +
                    "trace: " + e);
        }
    }


}
