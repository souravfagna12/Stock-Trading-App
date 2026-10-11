package com.trading.userservice.service;

import com.trading.userservice.dto.AuthResponse;
import com.trading.userservice.dto.RegisterRequest;
import com.trading.userservice.entity.User;
import com.trading.userservice.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import jakarta.websocket.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String , Object> kafkaTemplate;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    private final String USER_REGISTERED_TOPIC = "user.registered";
    public AuthResponse register(RegisterRequest request){
        log.info("registering user: {}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("user already exist with this email: "+request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .walletBalance(request.getInitialDeposit() == null ? request.getInitialDeposit() : BigDecimal.valueOf(10000))
                .build();

        User savedUser = userRepository.save(user);

        log.info("user saved with this user id: {}", savedUser.getId());

        // publishing this user registration event
        Map<String, Object> userRegisterEvent = new HashMap<>();
        userRegisterEvent.put("userId", savedUser.getId());
        userRegisterEvent.put("email", savedUser.getEmail() );
        userRegisterEvent.put("firstName", savedUser.getFirstName() );
        userRegisterEvent.put("lastName", savedUser.getLastName() );
        userRegisterEvent.put("walletBalance", savedUser.getWalletBalance() );

        kafkaTemplate.send(USER_REGISTERED_TOPIC , savedUser.getId(), userRegisterEvent);

        String token = generateAccessToken(savedUser.getId(), savedUser.getEmail());
        String refreshToken = generateRefreshToken(savedUser.getId());

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    private AuthResponse buildAuthResponse(User savedUser, String token, String refreshToken) {
        AuthResponse authResponse = AuthResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .walletBalance(savedUser.getWalletBalance())
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
        return authResponse;
    }

    public String generateAccessToken(String userId, String email){
        return Jwts.builder()
                .claim("userId", userId)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId){
        return Jwts.builder()
                .claim("userId", userId)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
