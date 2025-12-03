package prashant.example.rideSharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import prashant.example.rideSharing.config.JwtUtils;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.service.UserDetailsImpl;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @PostMapping("/passenger/register")
    public Passenger registerPassenger(@RequestBody Passenger passenger){
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        return passengerRepository.save(passenger);
    }
    @PostMapping("/driver/register")
    public Driver registerDriver(@RequestBody Driver driver){
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        return driverRepository.save(driver);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateToken(userDetails.getUsername(),userDetails.getRole());

        return "Bearer " + jwt;
    }
}
