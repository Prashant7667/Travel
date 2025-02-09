package prashant.example.rideSharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;  // <-- Import this
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Driver createDriver(Driver driver) {
        if (driver.getPassword() != null && !driver.getPassword().isBlank()) {
            driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        }
        return driverRepository.save(driver);
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver updateDriver(Long id, Driver updatedData) {
        Driver existingDriver = getDriverById(id);

        existingDriver.setName(updatedData.getName());
        existingDriver.setEmail(updatedData.getEmail());

        if (updatedData.getPassword() != null && !updatedData.getPassword().isBlank()) {
            existingDriver.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        existingDriver.setPhoneNumber(updatedData.getPhoneNumber());
        existingDriver.setVehicleDetails(updatedData.getVehicleDetails());
        existingDriver.setAvailabilityStatus(updatedData.getAvailabilityStatus());

        return driverRepository.save(existingDriver);
    }

    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }

    public Driver updateDriverAvailability(Long id, Driver.AvailabilityStatus status) {
        Driver driver = getDriverById(id);
        driver.setAvailabilityStatus(status);
        return driverRepository.save(driver);
    }
}
