package prashant.example.rideSharing.controller;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.model.RideAction;
import prashant.example.rideSharing.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private RideService rideService;
    @PostMapping("/request")
    public Ride RequestRide(@Valid @RequestBody Ride req){
        return rideService.requestRide(
                req.getStartLongitude(),
                req.getStartLatitude(),
                req.getEndLongitude(),
                req.getEndLatitude(),
                req.getFare()
        );
    }

    @PostMapping("/{rideId}/accept")
    public Ride AcceptRide(@PathVariable Long rideId,@RequestParam Long driverId){
        return rideService.handleRideAction(rideId,driverId, RideAction.DRIVER_ACCEPT);
    }
    @PostMapping("/{rideId}/reject")
    public Ride RejectRide(@PathVariable Long rideId,@RequestParam Long driverId){
        return rideService.handleRideAction(rideId,driverId,RideAction.DRIVER_REJECT);
    }
    @PostMapping("/{rideId}/start")
    public Ride StartRide(@PathVariable Long rideId,@RequestParam Long driverId){
        return rideService.handleRideAction(rideId,driverId, RideAction.START_RIDE);
    }
    @PostMapping("/{rideId}/complete")
    public Ride CompleteRide(@PathVariable Long rideId,@RequestParam Long driverId){
        return rideService.handleRideAction(rideId,driverId,RideAction.COMPLETE_RIDE);
    }
    @PostMapping("/{rideId}/cancel")
    public Ride CancelRide(@PathVariable Long rideId,@RequestParam Long driverId){
        return rideService.handleRideAction(rideId,driverId,RideAction.CANCEL_RIDE);
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides()
                .stream()
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public Ride getRideById(@PathVariable Long id) {
        Ride ride = rideService.getRideById(id);
        return ride;
    }
    @PutMapping("/{id}")
    public Ride updateRide(@PathVariable Long id, @Valid @RequestBody Ride ride) {
        Ride updatedEntity = new Ride();
        updatedEntity.setStartLongitude(ride.getStartLongitude());
        updatedEntity.setStartLatitude(ride.getStartLatitude());
        updatedEntity.setEndLatitude(ride.getEndLatitude());
        updatedEntity.setEndLongitude(ride.getEndLongitude());
        if (ride.getStatus() != null) {
            updatedEntity.setStatus(Ride.RideStatus.valueOf(String.valueOf(ride.getStatus())));
        }

        updatedEntity.setFare(ride.getFare());

        Ride savedRide = rideService.updateRide(id, updatedEntity);
        return savedRide;
    }
    @DeleteMapping("/{id}")
    public void deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
    }
}
