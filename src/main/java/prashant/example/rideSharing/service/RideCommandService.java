package prashant.example.rideSharing.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.repository.RideRepository;
@Service
public class RideCommandService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final DriverService driverService;
    private final RideQueryService rideQueryService;

    RideCommandService(RideRepository rideRepository, DriverRepository driverRepository, PassengerRepository passengerRepository,DriverService driverService,RideQueryService rideQueryService){
        this.rideRepository=rideRepository;
        this.driverRepository=driverRepository;
        this.passengerRepository=passengerRepository;
        this.driverService=driverService;
        this.rideQueryService=rideQueryService;
    }

    public Ride requestRide(double  startLongitude, double startLatitude, double  endLongitude, double endLatitude, Double fare){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String passengerEmail=authentication.getName();
        Passenger passenger=passengerRepository.findByEmail(passengerEmail)
                .orElseThrow(()->new ResourceNotFoundException("Passenger not found with email: "+passengerEmail));
        Driver driver=driverService.findNearestDriver(startLongitude,startLatitude);
        if(driver==null){
            throw new ResourceNotFoundException("No available drivers");
        }
        Ride ride=new Ride();
        ride.setPassenger(passenger);
        ride.setDriver(driver);
        ride.setStartLongitude(startLongitude);
        ride.setStartLatitude(startLatitude);
        ride.setEndLongitude(endLongitude);
        ride.setEndLatitude(endLatitude);
        ride.setFare(fare);
        ride.setStatus(Ride.RideStatus.REQUESTED);
        return rideRepository.save(ride);
    }
    public Ride updateRideStatus(Ride ride, Ride.RideStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (ride.getStatus() != newStatus) {
            ride.setStatus(newStatus);
            if (newStatus == Ride.RideStatus.COMPLETED||newStatus== Ride.RideStatus.CANCELLED) {
                Driver driver = ride.getDriver();
                if (driver != null) {
                    driver.setAvailabilityStatus(Driver.AvailabilityStatus.AVAILABLE);
                    driverRepository.save(driver);
                }
            }
            return rideRepository.save(ride);
        }
        return ride;
    }
    public Ride cancelRideByPassenger(Long rideId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        Ride ride=rideQueryService.getRideById(rideId);
        if(!ride.getPassenger().getEmail().equals(auth.getName())){
            throw new IllegalStateException("You are not authorised to process this request");
        }
        if(ride.getStatus()== Ride.RideStatus.COMPLETED){
            throw new IllegalStateException("Ride Is Already Completed");
        }
        return updateRideStatus(ride, Ride.RideStatus.CANCELLED);
    }
    public Ride updateRide(Long id, Ride updatedData) {
        Ride existingRide = rideQueryService.getRideById(id);
        existingRide.setStartLongitude(updatedData.getStartLongitude());
        existingRide.setStartLatitude(updatedData.getStartLatitude());
        existingRide.setEndLatitude(updatedData.getEndLatitude());
        existingRide.setEndLongitude(updatedData.getEndLongitude());
        existingRide.setFare(updatedData.getFare());
        return rideRepository.save(existingRide);
    }
    public void deleteRide(Long id) {
        Ride ride = rideQueryService.getRideById(id);
        Driver driver = ride.getDriver();
        if (driver != null && driver.getAvailabilityStatus() == Driver.AvailabilityStatus.UNAVAILABLE) {
            driver.setAvailabilityStatus(Driver.AvailabilityStatus.AVAILABLE);
            driverRepository.save(driver);
        }
        rideRepository.delete(ride);
    }


}
