package prashant.example.rideSharing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            driverEntity.setAvailabilityStatus(Driver.AvailabilityStatus.valueOf(String.valueOf(driver.getAvailabilityStatus())));
        }
        Driver savedDriver=driverService.createDriver(driverEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);

    }
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver>savedDrivers= driverService.getAllDrivers();
        return ResponseEntity.ok(savedDrivers);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        Driver driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @Valid @RequestBody Driver driver) {
        Driver updatedEntity = new Driver();
        updatedEntity.setName(driver.getName());
        updatedEntity.setEmail(driver.getEmail());
        updatedEntity.setPassword(driver.getPassword());
        updatedEntity.setPhoneNumber(driver.getPhoneNumber());
        updatedEntity.setVehicleDetails(driver.getVehicleDetails());
        updatedEntity.setLatitude(driver.getLatitude());
        updatedEntity.setLongitude(driver.getLongitude());
        if (driver.getAvailabilityStatus() != null) {
            updatedEntity.setAvailabilityStatus(driver.getAvailabilityStatus());
        }

        Driver updatedDriver=  driverService.updateDriver(id, updatedEntity);
        return ResponseEntity.ok(updatedDriver);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Driver> updateDriverAvailability(@PathVariable Long id,
                                              @RequestParam String status) {
        Driver.AvailabilityStatus availabilityStatus = Driver.AvailabilityStatus.valueOf(status);
        Driver updatedDriver= driverService.updateDriverAvailability(id, availabilityStatus);
        return ResponseEntity.ok(updatedDriver);
    }
}
