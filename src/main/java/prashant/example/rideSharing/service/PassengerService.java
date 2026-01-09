package prashant.example.rideSharing.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.repository.PassengerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    PassengerService(PassengerRepository passengerRepository,PasswordEncoder passwordEncoder){
        this.passengerRepository=passengerRepository;
        this.passwordEncoder=passwordEncoder;
    }
    public Passenger createPassenger(Passenger passenger) {
        if(passenger.getPassword()!=null && !passenger.getPassword().isBlank()){
            passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        }
        return passengerRepository.save(passenger);
    }
    public Passenger getCurrentPassengerDetails() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        return passengerRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
    }
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }
    public Passenger updatePassenger(Passenger updatedData) {
        Passenger existingPassenger = getCurrentPassengerDetails();
        if(updatedData.getName()!=null) {
            existingPassenger.setName(updatedData.getName());
        }
        if(updatedData.getPassword()!=null && !updatedData.getPassword().isBlank()){
            existingPassenger.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        if(updatedData.getPhoneNumber()!=null){
            existingPassenger.setPhoneNumber(updatedData.getPhoneNumber());
        }

        return passengerRepository.save(existingPassenger);
    }
    public void deletePassenger() {
        Passenger passenger = getCurrentPassengerDetails();
        passengerRepository.delete(passenger);
    }
}
