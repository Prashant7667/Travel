package prashant.example.rideSharing.service;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.exception.ResourceNotFoundException;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.model.RideAction;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.repository.RideRepository;
@Service
public class DriverAssignmentService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final DriverService driverService;
    private final RideQueryService rideQueryService;
    DriverAssignmentService(RideRepository rideRepository, DriverRepository driverRepository, PassengerRepository passengerRepository,DriverService driverService,RideQueryService rideQueryService){
        this.rideRepository=rideRepository;
        this.driverRepository=driverRepository;
        this.passengerRepository=passengerRepository;
        this.driverService=driverService;
        this.rideQueryService=rideQueryService;
    }
    @Transactional
    public Ride acceptRide(long rideId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        Driver driver=driverService.getDriverByEmail(auth.getName());
        int updated= rideRepository.acceptRide(rideId,driver);
        if(updated==0){
            throw new IllegalStateException("Ride Already Updated");
        }
        driver.setAvailabilityStatus(Driver.AvailabilityStatus.UNAVAILABLE);
        driverRepository.save(driver);
        return rideQueryService.getRideById(rideId);
    }
    @Transactional
    public Ride handleRideAction(long rideId, RideAction rideAction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Driver driver = driverRepository.findByEmail(auth.getName()).orElseThrow(() -> new ResourceNotFoundException("No Driver is found"));
        Ride ride = rideQueryService.getRideById(rideId);
        if (ride.getDriver() == null) {
            throw new IllegalStateException("Ride has No driver assigned ");
        }
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new IllegalStateException("This driver is not assigned to this ride");
        }
        switch (rideAction) {
            case DRIVER_REJECT:
                if (ride.getStatus() != Ride.RideStatus.REQUESTED) {
                    throw new IllegalStateException("Ride can only be rejected when status is requested");
                }
                ride.setStatus(Ride.RideStatus.REQUESTED);
                ride.setDriver(null);
                driver.setAvailabilityStatus(Driver.AvailabilityStatus.AVAILABLE);
                driverRepository.save(driver);
                break;
            case START_RIDE:
                if (ride.getStatus() != Ride.RideStatus.ACCEPTED) {
                    throw new IllegalStateException("Ride cannot be started if it is not accepted by driver");
                }
                ride.setStatus(Ride.RideStatus.IN_PROGRESS);
                driver.setAvailabilityStatus(Driver.AvailabilityStatus.UNAVAILABLE);
                driverRepository.save(driver);
                break;
            case COMPLETE_RIDE:
                if (ride.getStatus() != Ride.RideStatus.IN_PROGRESS) {
                    throw new IllegalStateException("Ride can't be completed when status is not in progress");
                }
                ride.setStatus(Ride.RideStatus.COMPLETED);
                driver.setAvailabilityStatus(Driver.AvailabilityStatus.AVAILABLE);
                driverRepository.save(driver);
                break;
            case CANCEL_RIDE:
                if (ride.getStatus() == Ride.RideStatus.COMPLETED || ride.getStatus() == Ride.RideStatus.CANCELLED) {
                    throw new IllegalStateException("Ride is already finished");
                }
                ride.setStatus(Ride.RideStatus.CANCELLED);
                driver.setAvailabilityStatus(Driver.AvailabilityStatus.AVAILABLE);
                driverRepository.save(driver);
        }
        return rideRepository.save(ride);

    }
}
