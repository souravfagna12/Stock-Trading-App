package com.trading.userservice.contoller;

import com.trading.userservice.dto.AuthResponse;
import com.trading.userservice.dto.LoginRequest;
import com.trading.userservice.dto.RegisterRequest;
import com.trading.userservice.dto.UserResponse;
import com.trading.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/user-profile") // endpoint for external user
    public ResponseEntity<UserResponse> profile(@RequestHeader("X-User-id") String userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/{id}") // for internal calls
    public ResponseEntity<UserResponse> profile(@PathVariable String id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
