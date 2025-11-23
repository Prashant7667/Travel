package prashant.example.rideSharing.controller;

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
    public  Driver createDriver(@Valid @RequestBody Driver driver) {
        Driver driverEntity = new Driver();
        driverEntity.setName(driver.getName());
        driverEntity.setEmail(driver.getEmail());
        driverEntity.setPassword(driver.getPassword());
        driverEntity.setPhoneNumber(driver.getPhoneNumber());
        driverEntity.setVehicleDetails(driver.getVehicleDetails());
        if (driver.getAvailabilityStatus() != null) {
            driverEntity.setAvailabilityStatus(Driver.AvailabilityStatus.valueOf(String.valueOf(driver.getAvailabilityStatus())));
        }
        return driverService.createDriver(driverEntity);
        //return convertEntityToDTO(savedDriver);
    }
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers()
                .stream()
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
        //return convertEntityToDTO(driver);
    }
    @PutMapping("/{id}")
    public Driver updateDriver(@PathVariable Long id, @Valid @RequestBody Driver driver) {
        Driver updatedEntity = new Driver();
        updatedEntity.setName(driver.getName());
        updatedEntity.setEmail(driver.getEmail());
        updatedEntity.setPassword(driver.getPassword());
        updatedEntity.setPhoneNumber(driver.getPhoneNumber());
        updatedEntity.setVehicleDetails(driver.getVehicleDetails());
        if (driver.getAvailabilityStatus() != null) {
            updatedEntity.setAvailabilityStatus(Driver.AvailabilityStatus.valueOf(String.valueOf(driver.getAvailabilityStatus())));
        }

        return  driverService.updateDriver(id, updatedEntity);
    }
    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @PatchMapping("/{id}/availability")
    public Driver updateDriverAvailability(@PathVariable Long id,
                                              @RequestParam String status) {
        Driver.AvailabilityStatus availabilityStatus = Driver.AvailabilityStatus.valueOf(status);
        return driverService.updateDriverAvailability(id, availabilityStatus);

    }


}
