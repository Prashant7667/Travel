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

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // distance in km
    }

    public Driver findNearestDriver(double startLongitude, double startLatitude){
        List<Driver>allDrivers=getAllDrivers();
        Driver nearest=null;
        double nearLoc=Double.MAX_VALUE;
        for(Driver driver:allDrivers){
            double dist=haversine(startLatitude,startLongitude,driver.getLatitude(),driver.getLongitude());
            if(dist<nearLoc){
                nearLoc=dist;
                nearest=driver;
            }
        }
        return nearest;
    }
}
