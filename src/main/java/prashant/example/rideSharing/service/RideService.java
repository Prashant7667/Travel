package prashant.example.rideSharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Driver.AvailabilityStatus;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.model.Ride.RideStatus;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.repository.RideRepository;

import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    public Ride createRide(Long passengerId, String startLocation, String endLocation, Double fare) {

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));


        Driver driver = driverRepository.findFirstByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        if (driver == null) {
            throw new RuntimeException("No available drivers at the moment.");
        }

        driver.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
        driverRepository.save(driver);

        Ride ride = new Ride();
        ride.setPassenger(passenger);
        ride.setDriver(driver);
        ride.setStartLocation(startLocation);
        ride.setEndLocation(endLocation);
        ride.setFare(fare);
        ride.setStatus(RideStatus.REQUESTED);

        return rideRepository.save(ride);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + id));
    }
    public Ride updateRide(Long id, Ride updatedData) {
        Ride existingRide = getRideById(id);

        existingRide.setStartLocation(updatedData.getStartLocation());
        existingRide.setEndLocation(updatedData.getEndLocation());

        if (updatedData.getStatus() != null) {
            existingRide.setStatus(updatedData.getStatus());
        }

        existingRide.setFare(updatedData.getFare());

        return rideRepository.save(existingRide);
    }

    public Ride updateRideStatus(Long rideId, RideStatus newStatus) {
        Ride ride = getRideById(rideId);
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (ride.getStatus() != newStatus) {
            ride.setStatus(newStatus);
            if (newStatus == RideStatus.COMPLETED) {
                Driver driver = ride.getDriver();
                if (driver != null) {
                    driver.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
                    driverRepository.save(driver);
                }
            }

            return rideRepository.save(ride);
        }

        return ride;
    }
    public void deleteRide(Long id) {
        Ride ride = getRideById(id);
        Driver driver = ride.getDriver();
        if (driver != null && driver.getAvailabilityStatus() == AvailabilityStatus.UNAVAILABLE) {
            driver.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            driverRepository.save(driver);
        }

        rideRepository.delete(ride);
    }

    public List<Ride> getPassengerRideHistory(Long passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    public List<Ride> getDriverRideHistory(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }
}
