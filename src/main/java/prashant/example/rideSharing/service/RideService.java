package prashant.example.rideSharing.service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Driver.AvailabilityStatus;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.model.Ride.RideStatus;
import prashant.example.rideSharing.model.RideAction;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.repository.RideRepository;

import java.net.Authenticator;
import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverService driverService;

    public Ride requestRide(double  startLongitude, double startLatitude,double  endLongitude, double endLatitude, Double fare){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String email=user.getUsername();
        String role=user.getRole();
        System.out.println(authentication);
        if(!"PASSENGER".equalsIgnoreCase(role)){
            throw new IllegalStateException("Only Passenger can request a ride");
        }

        Passenger passenger=passengerRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Passenger not found with email: "+email));
        Driver driver=driverService.findNearestDriver(startLongitude,startLatitude);
        if(driver==null){
            throw new RuntimeException("No available drivers at the moment.");
        }
        Ride ride=new Ride();
        ride.setPassenger(passenger);
        ride.setDriver(driver);
        ride.setStartLongitude(startLongitude);
        ride.setStartLatitude(startLatitude);
        ride.setEndLongitude(endLongitude);
        ride.setEndLatitude(endLatitude);
        ride.setFare(fare);
        ride.setStatus(RideStatus.REQUESTED);
        return rideRepository.save(ride);

    }
    @Transactional
    public Ride handleRideAction(long rideId, long driverId, RideAction rideAction){
        Ride ride=getRideById(rideId);
        if(ride.getDriver()==null){
            throw new IllegalStateException("Ride has No driver assigned ");
        }
        if(!ride.getDriver().getId().equals(driverId)){
            throw new IllegalStateException("This driver is not assigned to this ride");
        }
        Driver driver =ride.getDriver();
        switch (rideAction){
            case DRIVER_ACCEPT :
                if(ride.getStatus()!= Ride.RideStatus.REQUESTED){
                    throw new IllegalStateException("Ride can only be accepted when status is requested");
                }
                ride.setStatus(RideStatus.ACCEPTED);
                driver.setAvailabilityStatus(Driver.AvailabilityStatus.UNAVAILABLE);
                driverRepository.save(driver);
                break;
            case DRIVER_REJECT:
                if(ride.getStatus()!= Ride.RideStatus.REQUESTED){
                    throw new IllegalStateException("Ride can only be rejected when status is requested");
                }
                ride.setStatus(RideStatus.CANCELLED);
                driver.setAvailabilityStatus(Driver.AvailabilityStatus.AVAILABLE);
                driverRepository.save(driver);
                break;
            case START_RIDE:
                if(ride.getStatus()!= Ride.RideStatus.ACCEPTED){
                    throw new IllegalStateException("Ride cannot be started if it is not accepted by driver");
                }
                ride.setStatus(RideStatus.IN_PROGRESS);
                driver.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
                driverRepository.save(driver);
                break;
            case COMPLETE_RIDE:
                if(ride.getStatus()!=RideStatus.IN_PROGRESS){
                    throw new IllegalStateException("Ride can't be completed when status is not in progress");
                }
                ride.setStatus(RideStatus.COMPLETED);
                driver.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
                driverRepository.save(driver);
            case CANCEL_RIDE:
                if(ride.getStatus()==RideStatus.COMPLETED||ride.getStatus()==RideStatus.CANCELLED){
                    throw new IllegalStateException("Ride is already finished");
                }
                ride.setStatus(RideStatus.CANCELLED);
                driver.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
                driverRepository.save(driver);
        }
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
        existingRide.setStartLongitude(updatedData.getStartLongitude());
        existingRide.setStartLatitude(updatedData.getStartLatitude());
        existingRide.setEndLatitude(updatedData.getEndLatitude());
        existingRide.setEndLongitude(updatedData.getEndLongitude());
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

    public List<Ride> getRideHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        String email = user.getUsername();
        String role = user.getRole();

        if ("PASSENGER".equals(role)) {
            return rideRepository.findByPassengerEmail(email);
        }

        if ("DRIVER".equals(role)) {
            return rideRepository.findByDriverEmail(email);
        }

        return List.of();
    }


    public List<Ride> getDriverRideHistory(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }
}
