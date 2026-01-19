package prashant.example.rideSharing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {
    @Autowired
    private DriverService driverService;
    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) {
        Driver driverEntity = new Driver();
        driverEntity.setName(driver.getName());
        driverEntity.setEmail(driver.getEmail());
        driverEntity.setPassword(driver.getPassword());
        driverEntity.setPhoneNumber(driver.getPhoneNumber());
        driverEntity.setVehicleDetails(driver.getVehicleDetails());
        driverEntity.setLongitude(driver.getLongitude());
        driverEntity.setLatitude(driver.getLatitude());
        if (driver.getAvailabilityStatus() != null) {
            driverEntity.setAvailabilityStatus(driver.getAvailabilityStatus());
        }
        Driver savedDriver=driverService.createDriver(driverEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allDrivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver>savedDrivers= driverService.getAllDrivers();
        return ResponseEntity.ok(savedDrivers);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/me")
    public ResponseEntity<Driver> updateDriver(@RequestBody Driver driver) {
        Driver updatedDriver=  driverService.updateDriver(driver);
        return ResponseEntity.ok(updatedDriver);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteDriver() {
        driverService.deleteDriver();
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/me")
    public ResponseEntity<Driver> getCurrentDriver() {
        Driver currentDriver=driverService.getCurrentDriver();
        return ResponseEntity.ok(currentDriver);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PatchMapping("/me/availability")
    public ResponseEntity<Driver> updateDriverAvailability(@RequestParam String status) {
        Driver.AvailabilityStatus availabilityStatus = Driver.AvailabilityStatus.valueOf(status.toUpperCase());
        Driver updatedDriver= driverService.updateDriverAvailability(availabilityStatus);
        return ResponseEntity.ok(updatedDriver);
    }
}
