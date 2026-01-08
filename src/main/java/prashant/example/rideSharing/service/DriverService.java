package prashant.example.rideSharing.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;  // <-- Import this
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    public DriverService(DriverRepository driverRepository,PasswordEncoder passwordEncoder){
        this.driverRepository=driverRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public Driver createDriver(Driver driver) {
        if (driver.getPassword() != null && !driver.getPassword().isBlank()) {
            driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        }
        return driverRepository.save(driver);
    }

    public Driver getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with email: " + email));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver updateDriver(Driver updatedData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email= auth.getName();
        Driver existingDriver = getDriverByEmail(email);

        if (updatedData.getName() != null)
            existingDriver.setName(updatedData.getName());

        if (updatedData.getPassword() != null && !updatedData.getPassword().isBlank())
            existingDriver.setPassword(passwordEncoder.encode(updatedData.getPassword()));

        if (updatedData.getPhoneNumber() != null)
            existingDriver.setPhoneNumber(updatedData.getPhoneNumber());

        if (updatedData.getVehicleDetails() != null)
            existingDriver.setVehicleDetails(updatedData.getVehicleDetails());

        if (updatedData.getAvailabilityStatus() != null)
            existingDriver.setAvailabilityStatus(updatedData.getAvailabilityStatus());

        if (updatedData.getLatitude() != null)
            existingDriver.setLatitude(updatedData.getLatitude());

        if (updatedData.getLongitude() != null)
            existingDriver.setLongitude(updatedData.getLongitude());

        return driverRepository.save(existingDriver);
    }

    public void deleteDriver() {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String email=auth.getName();
        Driver driver = getDriverByEmail(email);
        driverRepository.delete(driver);
    }

    public Driver updateDriverAvailability(Driver.AvailabilityStatus status) {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String email=auth.getName();
        Driver driver = getDriverByEmail(email);
        driver.setAvailabilityStatus(status);
        return driverRepository.save(driver);
    }

    public Driver findNearestDriver(double startLongitude, double startLatitude){
        double radiusKm = 5.0; // configurable later

        double latDelta = radiusKm / 111.0;
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(startLatitude)));

        double minLat = startLatitude - latDelta;
        double maxLat = startLatitude + latDelta;
        double minLon = startLongitude - lonDelta;
        double maxLon = startLongitude + lonDelta;

        Driver driver = driverRepository.findNearestDriver(startLatitude,startLongitude,minLat,maxLat, minLon,maxLon);
        if(driver==null){
            throw new ResourceNotFoundException("No Available Drivers");
        }
        return driver;
    }
}
