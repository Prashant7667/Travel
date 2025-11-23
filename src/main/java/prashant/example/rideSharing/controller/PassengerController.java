package prashant.example.rideSharing.controller;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;
    @GetMapping
    public List<Passenger> getAllPassengers() {
        return passengerService.getAllPassengers()
                .stream()
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public Passenger getPassengerById(@PathVariable Long id) {
        Passenger passenger = passengerService.getPassengerById(id);
        return passenger;
    }
    @PutMapping("/{id}")
    public Passenger updatePassenger(@PathVariable Long id, @Valid @RequestBody Passenger passengerDTO) {
        Passenger updatedEntity = new Passenger();
        updatedEntity.setName(passengerDTO.getName());
        updatedEntity.setEmail(passengerDTO.getEmail());
        updatedEntity.setPassword(passengerDTO.getPassword());
        updatedEntity.setPhoneNumber(passengerDTO.getPhoneNumber());

        Passenger savedPassenger = passengerService.updatePassenger(id, updatedEntity);
        return savedPassenger;
    }
    @DeleteMapping("/{id}")
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }

}
