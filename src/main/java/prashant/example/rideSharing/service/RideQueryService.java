package prashant.example.rideSharing.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.repository.RideRepository;

import java.util.List;
@Service
public class RideQueryService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final DriverService driverService;
    RideQueryService(RideRepository rideRepository, DriverRepository driverRepository, PassengerRepository passengerRepository,DriverService driverService){
        this.rideRepository=rideRepository;
        this.driverRepository=driverRepository;
        this.passengerRepository=passengerRepository;
        this.driverService=driverService;
    }
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + id));
    }
    public List<Ride> getPassengerRideHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return rideRepository.findByPassengerEmail(email);
    }
    public List<Ride> getDriverRideHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return rideRepository.findByDriverEmail(email);
    }

}
