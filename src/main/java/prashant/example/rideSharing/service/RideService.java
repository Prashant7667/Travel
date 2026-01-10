package prashant.example.rideSharing.service;
import jakarta.transaction.Transactional;
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
import java.util.List;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final DriverService driverService;
    RideService(RideRepository rideRepository, DriverRepository driverRepository, PassengerRepository passengerRepository,DriverService driverService){
        this.rideRepository=rideRepository;
        this.driverRepository=driverRepository;
        this.passengerRepository=passengerRepository;
        this.driverService=driverService;
    }

    public Ride requestRide(double  startLongitude, double startLatitude,double  endLongitude, double endLatitude, Double fare){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
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
        ride.setStatus(RideStatus.REQUESTED);
        return rideRepository.save(ride);
    }
    @Transactional
    public Ride handleRideAction(long rideId, RideAction rideAction){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String email=auth.getName();
        Driver driver =driverRepository.findByEmail(auth.getName()).orElseThrow(()->new RuntimeException("No Driver is found"));
        Ride ride=getRideById(rideId);
        if(ride.getDriver()==null){
            throw new IllegalStateException("Ride has No driver assigned ");
        }
        if(!ride.getDriver().getId().equals(driver.getId())){
            throw new IllegalStateException("This driver is not assigned to this ride");
        }
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
                if(ride.getStatus()!= RideStatus.REQUESTED){
                    throw new IllegalStateException("Ride can only be rejected when status is requested");
                }
                ride.setStatus(RideStatus.REQUESTED);
                ride.setDriver(null);
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
                break;
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
    public Ride cancelRideByPassenger(Long rideId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        Ride ride=getRideById(rideId);
        if(!ride.getPassenger().getEmail().equals(auth.getName())){
            throw new IllegalStateException("You are not authorised to process this request");
        }
        if(ride.getStatus()==RideStatus.COMPLETED){
            throw new IllegalStateException("Ride Is Already Completed");
        }
        return updateRideStatus(ride,RideStatus.CANCELLED);
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
        existingRide.setFare(updatedData.getFare());
        return rideRepository.save(existingRide);
    }
    public Ride updateRideStatus(Ride ride, RideStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (ride.getStatus() != newStatus) {
            ride.setStatus(newStatus);
            if (newStatus == RideStatus.COMPLETED||newStatus==RideStatus.CANCELLED) {
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
    public List<Ride> getPassengerRideHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return rideRepository.findByPassengerEmail(email);
    }
    public List<Ride> getDriverRideHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return rideRepository.findByDriverEmail(email);
    }
}