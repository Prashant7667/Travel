package prashant.example.rideSharing.service;

import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.repository.PassengerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public Passenger createPassenger(Passenger passenger) {
        if(passenger.getPassword()!=null && !passenger.getPassword().isBlank()){
            passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        }
        return passengerRepository.save(passenger);
    }
    public Passenger getPassengerById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + id));
    }
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }
    public Passenger updatePassenger(Long id, Passenger updatedData) {
        Passenger existingPassenger = getPassengerById(id);
        existingPassenger.setName(updatedData.getName());
        existingPassenger.setEmail(updatedData.getEmail());
        if(updatedData.getPassword()!=null && !updatedData.getPassword().isBlank()){
            existingPassenger.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }

        existingPassenger.setPhoneNumber(updatedData.getPhoneNumber());
        return passengerRepository.save(existingPassenger);
    }
    public void deletePassenger(Long id) {
        Passenger passenger = getPassengerById(id);
        passengerRepository.delete(passenger);
    }
}
