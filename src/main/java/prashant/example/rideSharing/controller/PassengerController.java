package prashant.example.rideSharing.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.service.PassengerService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/passengers")
public class PassengerController {
    private final PassengerService passengerService;
    PassengerController(PassengerService passengerService){
        this.passengerService=passengerService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        List<Passenger>savedPassengers= passengerService.getAllPassengers();
        return ResponseEntity.ok(savedPassengers);

    }
    @PreAuthorize("hasRole('PASSENGER')")

    @GetMapping("/me/details")
    public ResponseEntity<Passenger> getCurrentPassengerDetails() {
        Passenger passenger = passengerService.getCurrentPassengerDetails();
        return ResponseEntity.ok(passenger);
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @PutMapping("/me/update")
    public ResponseEntity<Passenger> updatePassenger( @RequestBody Passenger passenger) {
        Passenger savedPassenger = passengerService.updatePassenger(passenger);
        return ResponseEntity.ok(savedPassenger);
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @DeleteMapping("/me/delete")
    public ResponseEntity<Void> deletePassenger() {
        passengerService.deletePassenger();
        return ResponseEntity.noContent().build();
    }

}
