package ferrefix.ms_auth.controller;

import ferrefix.ms_auth.model.User;
import ferrefix.ms_auth.repository.UserRepository;
import ferrefix.ms_auth.service.HashService;
import ferrefix.ms_auth.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final HashService hashService;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, HashService hashService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.hashService = hashService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already taken");
        }
        user.setPassword(hashService.hashPassword(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent() && hashService.checkPassword(loginRequest.getPassword(), userOpt.get().getPassword())) {
            String token = jwtService.generateToken(userOpt.get().getEmail());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        try {
            jwtService.validateToken(token);
            return ResponseEntity.ok("Token is valid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
