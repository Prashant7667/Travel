package prashant.example.rideSharing.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Ride> RequestRide(@Valid @RequestBody Ride req){
        Ride requestedRide= rideService.requestRide(
                req.getStartLongitude(),
                req.getStartLatitude(),
                req.getEndLongitude(),
                req.getEndLatitude(),
                req.getFare()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(requestedRide);
    }

    @PostMapping("/{rideId}/accept")
    public ResponseEntity<Ride> AcceptRide(@PathVariable Long rideId,@RequestParam Long driverId){
        Ride acceptedRide= rideService.handleRideAction(rideId,driverId, RideAction.DRIVER_ACCEPT);
        return ResponseEntity.ok(acceptedRide);
    }
    @PostMapping("/{rideId}/reject")
    public ResponseEntity<Ride> RejectRide(@PathVariable Long rideId,@RequestParam Long driverId){
        Ride rejectedRide= rideService.handleRideAction(rideId,driverId,RideAction.DRIVER_REJECT);
        return ResponseEntity.ok(rejectedRide);
    }
    @PostMapping("/{rideId}/start")
    public ResponseEntity<Ride> StartRide(@PathVariable Long rideId,@RequestParam Long driverId){
        Ride startRideData=rideService.handleRideAction(rideId,driverId, RideAction.START_RIDE);
        return ResponseEntity.ok(startRideData);
    }
    @PostMapping("/{rideId}/complete")
    public ResponseEntity<Ride> CompleteRide(@PathVariable Long rideId,@RequestParam Long driverId){
        Ride completeRide= rideService.handleRideAction(rideId,driverId,RideAction.COMPLETE_RIDE);
        return ResponseEntity.ok(completeRide);
    }
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<Ride> CancelRide(@PathVariable Long rideId,@RequestParam Long driverId){
         Ride cancelRide=rideService.handleRideAction(rideId,driverId,RideAction.CANCEL_RIDE);
         return ResponseEntity.ok(cancelRide);
    }

    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides() {
        List<Ride>allRides= rideService.getAllRides();
        return ResponseEntity.ok(allRides);

    }
    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        Ride ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Ride> updateRide(@PathVariable Long id, @Valid @RequestBody Ride ride) {
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
        return ResponseEntity.ok(savedRide);
    }
    @GetMapping("/rideHistory")
    public ResponseEntity<List<Ride>>rideHistory(){
        List<Ride>rides= rideService.getRideHistory();
        return ResponseEntity.ok(rides);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {

        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}
