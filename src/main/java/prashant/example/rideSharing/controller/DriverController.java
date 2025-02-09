package prashant.example.rideSharing.controller;

import prashant.example.rideSharing.dto.DriverDTO;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping
    public DriverDTO createDriver(@Valid @RequestBody DriverDTO driverDTO) {
        Driver driverEntity = new Driver();
        driverEntity.setName(driverDTO.getName());
        driverEntity.setEmail(driverDTO.getEmail());
        driverEntity.setPassword(driverDTO.getPassword());
        driverEntity.setPhoneNumber(driverDTO.getPhoneNumber());
        driverEntity.setVehicleDetails(driverDTO.getVehicleDetails());
        if (driverDTO.getAvailabilityStatus() != null) {
            driverEntity.setAvailabilityStatus(Driver.AvailabilityStatus.valueOf(driverDTO.getAvailabilityStatus()));
        }
        Driver savedDriver = driverService.createDriver(driverEntity);
        return convertEntityToDTO(savedDriver);
    }
    @GetMapping
    public List<DriverDTO> getAllDrivers() {
        return driverService.getAllDrivers()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public DriverDTO getDriverById(@PathVariable Long id) {
        Driver driver = driverService.getDriverById(id);
        return convertEntityToDTO(driver);
    }
    @PutMapping("/{id}")
    public DriverDTO updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDTO driverDTO) {
        Driver updatedEntity = new Driver();
        updatedEntity.setName(driverDTO.getName());
        updatedEntity.setEmail(driverDTO.getEmail());
        updatedEntity.setPassword(driverDTO.getPassword());
        updatedEntity.setPhoneNumber(driverDTO.getPhoneNumber());
        updatedEntity.setVehicleDetails(driverDTO.getVehicleDetails());
        if (driverDTO.getAvailabilityStatus() != null) {
            updatedEntity.setAvailabilityStatus(Driver.AvailabilityStatus.valueOf(driverDTO.getAvailabilityStatus()));
        }

        Driver savedDriver = driverService.updateDriver(id, updatedEntity);
        return convertEntityToDTO(savedDriver);
    }
    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @PatchMapping("/{id}/availability")
    public DriverDTO updateDriverAvailability(@PathVariable Long id,
                                              @RequestParam String status) {
        Driver.AvailabilityStatus availabilityStatus = Driver.AvailabilityStatus.valueOf(status);
        Driver updatedDriver = driverService.updateDriverAvailability(id, availabilityStatus);
        return convertEntityToDTO(updatedDriver);
    }
    private DriverDTO convertEntityToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setName(driver.getName());
        dto.setEmail(driver.getEmail());
        dto.setPassword(driver.getPassword());
        dto.setPhoneNumber(driver.getPhoneNumber());
        dto.setVehicleDetails(driver.getVehicleDetails());
        if (driver.getAvailabilityStatus() != null) {
            dto.setAvailabilityStatus(driver.getAvailabilityStatus().name());
        }
        return dto;
    }
}
