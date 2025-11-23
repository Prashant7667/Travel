package prashant.example.rideSharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import prashant.example.rideSharing.config.JwtUtils;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.repository.PassengerRepository;

@RestController
@RequestMapping("/auth/passenger")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PassengerRepository passengerRepository;

    @PostMapping("/register")
    public Passenger registerPassenger(@RequestBody Passenger passenger){
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        return passengerRepository.save(passenger);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {

        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        String jwt = jwtUtils.generateToken(loginRequest.getEmail());

        return "Bearer " + jwt;
    }
}
