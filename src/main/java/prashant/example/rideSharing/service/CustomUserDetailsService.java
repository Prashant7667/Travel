package prashant.example.rideSharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Driver driver=driverRepository.findByEmail(email).orElse(null);
        if (driver != null) {
            return new UserDetailsImpl(driver.getEmail(), driver.getPassword());
        }

       Passenger passenger=passengerRepository.findByEmail(email).orElse(null);
        if (passenger != null) {
            return new UserDetailsImpl(passenger.getEmail(), passenger.getPassword());
        }

        throw new UsernameNotFoundException("No driver or passenger found with email: " + email);
    }
}
