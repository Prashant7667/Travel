package prashant.example.rideSharing.controller;

import prashant.example.rideSharing.dto.RideDTO;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private RideService rideService;
    @PostMapping
    public RideDTO createRide(@Valid @RequestBody RideDTO rideDTO) {
        Ride savedRide = rideService.createRide(
                rideDTO.getPassengerId(),
                rideDTO.getStartLocation(),
                rideDTO.getEndLocation(),
                rideDTO.getFare()
        );
        return convertEntityToDTO(savedRide);
    }

    @GetMapping
    public List<RideDTO> getAllRides() {
        return rideService.getAllRides()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RideDTO getRideById(@PathVariable Long id) {
        Ride ride = rideService.getRideById(id);
        return convertEntityToDTO(ride);
    }
    @PutMapping("/{id}")
    public RideDTO updateRide(@PathVariable Long id, @Valid @RequestBody RideDTO rideDTO) {
        Ride updatedEntity = new Ride();
        updatedEntity.setStartLocation(rideDTO.getStartLocation());
        updatedEntity.setEndLocation(rideDTO.getEndLocation());
        if (rideDTO.getStatus() != null) {
            updatedEntity.setStatus(Ride.RideStatus.valueOf(rideDTO.getStatus().toUpperCase()));
        }

        updatedEntity.setFare(rideDTO.getFare());

        Ride savedRide = rideService.updateRide(id, updatedEntity);
        return convertEntityToDTO(savedRide);
    }
    @DeleteMapping("/{id}")
    public void deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
    }
    @PatchMapping("/{id}/status")
    public RideDTO updateRideStatus(@PathVariable Long id, @RequestParam String status) {
        Ride.RideStatus newStatus = Ride.RideStatus.valueOf(status.toUpperCase());
        Ride updatedRide = rideService.updateRideStatus(id, newStatus);
        return convertEntityToDTO(updatedRide);
    }
    private RideDTO convertEntityToDTO(Ride ride) {
        RideDTO dto = new RideDTO();
        dto.setId(ride.getId());
        dto.setStartLocation(ride.getStartLocation());
        dto.setEndLocation(ride.getEndLocation());
        dto.setStatus(ride.getStatus().name());
        dto.setFare(ride.getFare());
        if (ride.getDriver() != null) {
            dto.setDriverId(ride.getDriver().getId());
        }
        if (ride.getPassenger() != null) {
            dto.setPassengerId(ride.getPassenger().getId());
        }
        return dto;
    }
}
