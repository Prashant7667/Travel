package prashant.example.rideSharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import prashant.example.rideSharing.config.JwtUtils;
import prashant.example.rideSharing.dto.LoginRequestDTO;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO loginRequest) {

        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        String jwt = jwtUtils.generateToken(loginRequest.getEmail());

        return "Bearer " + jwt;
    }
}
