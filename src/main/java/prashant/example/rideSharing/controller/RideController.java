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
                req.getStartLocation(),
                req.getEndLocation(),
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


//    @PostMapping
//    public Ride createRide(@Valid @RequestBody Ride ride) {
//        Ride savedRide = rideService.createRide(
//                ride.getStartLocation(),
//                ride.getEndLocation(),
//                ride.getFare()
//        );
//        return savedRide;
//    }

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
        updatedEntity.setStartLocation(ride.getStartLocation());
        updatedEntity.setEndLocation(ride.getEndLocation());
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
    @PatchMapping("/{id}/status")
    public Ride updateRideStatus(@PathVariable Long id, @RequestParam String status) {
        Ride.RideStatus newStatus = Ride.RideStatus.valueOf(status.toUpperCase());
        Ride updatedRide = rideService.updateRideStatus(id, newStatus);
        return updatedRide;
    }

}
