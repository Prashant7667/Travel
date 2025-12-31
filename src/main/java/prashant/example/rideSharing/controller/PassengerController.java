package prashant.example.rideSharing.controller;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        List<Passenger>savedPassengers= passengerService.getAllPassengers();
        return ResponseEntity.ok(savedPassengers);

    }
    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        Passenger passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(@PathVariable Long id, @Valid @RequestBody Passenger passengerDTO) {
        Passenger updatedEntity = new Passenger();
        updatedEntity.setName(passengerDTO.getName());
        updatedEntity.setEmail(passengerDTO.getEmail());
        updatedEntity.setPassword(passengerDTO.getPassword());
        updatedEntity.setPhoneNumber(passengerDTO.getPhoneNumber());

        Passenger savedPassenger = passengerService.updatePassenger(id, updatedEntity);
        return ResponseEntity.ok(savedPassenger);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }

}
