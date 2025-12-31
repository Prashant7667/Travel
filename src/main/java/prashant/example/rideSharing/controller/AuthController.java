package prashant.example.rideSharing.controller;

import ch.qos.logback.core.joran.sanity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.Map;

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
    public ResponseEntity<Passenger>registerPassenger(@RequestBody Passenger passenger){
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        Passenger savedPassenger=passengerRepository.save(passenger);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPassenger);

    }
    @PostMapping("/driver/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody Driver driver){
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        Driver savedDriver=driverRepository.save(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(authToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateToken(userDetails.getUsername(),userDetails.getRole());
        return ResponseEntity.ok(Map.of("token","Bearer: "+jwt,"role",userDetails.getRole()));
    }
}
