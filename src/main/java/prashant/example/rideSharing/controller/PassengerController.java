package prashant.example.rideSharing.controller;

import prashant.example.rideSharing.dto.PassengerDTO;
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
    @PostMapping
    public PassengerDTO createPassenger(@Valid @RequestBody PassengerDTO passengerDTO) {
        Passenger passengerEntity = new Passenger();
        passengerEntity.setName(passengerDTO.getName());
        passengerEntity.setEmail(passengerDTO.getEmail());
        passengerEntity.setPassword(passengerDTO.getPassword());
        passengerEntity.setPhoneNumber(passengerDTO.getPhoneNumber());

        Passenger savedPassenger = passengerService.createPassenger(passengerEntity);
        return convertEntityToDTO(savedPassenger);
    }

    @GetMapping
    public List<PassengerDTO> getAllPassengers() {
        return passengerService.getAllPassengers()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public PassengerDTO getPassengerById(@PathVariable Long id) {
        Passenger passenger = passengerService.getPassengerById(id);
        return convertEntityToDTO(passenger);
    }
    @PutMapping("/{id}")
    public PassengerDTO updatePassenger(@PathVariable Long id, @Valid @RequestBody PassengerDTO passengerDTO) {
        Passenger updatedEntity = new Passenger();
        updatedEntity.setName(passengerDTO.getName());
        updatedEntity.setEmail(passengerDTO.getEmail());
        updatedEntity.setPassword(passengerDTO.getPassword());
        updatedEntity.setPhoneNumber(passengerDTO.getPhoneNumber());

        Passenger savedPassenger = passengerService.updatePassenger(id, updatedEntity);
        return convertEntityToDTO(savedPassenger);
    }
    @DeleteMapping("/{id}")
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }
    private PassengerDTO convertEntityToDTO(Passenger passenger) {
        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setName(passenger.getName());
        dto.setEmail(passenger.getEmail());
        dto.setPassword(passenger.getPassword());
        dto.setPhoneNumber(passenger.getPhoneNumber());
        return dto;
    }
}
