package com.hostel.controller;

import com.hostel.dto.UserDTO;
import com.hostel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String role = request.get("role");
        UserDTO user = userService.createUser(name, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
