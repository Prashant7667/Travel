package prashant.example.rideSharing.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.model.RideAction;
import prashant.example.rideSharing.service.DriverAssignmentService;
import prashant.example.rideSharing.service.RideCommandService;
import prashant.example.rideSharing.service.RideQueryService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rides")
public class RideController {
    private final DriverAssignmentService driverAssignmentService;
    private final RideQueryService rideQueryService;
    private final RideCommandService rideCommandService;
    RideController(DriverAssignmentService driverAssignmentService, RideQueryService rideQueryService, RideCommandService rideCommandService){
        this.driverAssignmentService=driverAssignmentService;
        this.rideQueryService=rideQueryService;
        this.rideCommandService=rideCommandService;
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @PostMapping("/request")
    public ResponseEntity<Ride> requestRide(@Valid @RequestBody Ride req){
        Ride requestedRide= rideCommandService.requestRide(
                req.getStartLongitude(),
                req.getStartLatitude(),
                req.getEndLongitude(),
                req.getEndLatitude(),
                req.getFare()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(requestedRide);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{rideId}/accept")
    public ResponseEntity<Ride> acceptRide(@PathVariable Long rideId){
        Ride acceptedRide= driverAssignmentService.acceptRide(rideId);
        return ResponseEntity.ok(acceptedRide);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{rideId}/reject")
    public ResponseEntity<Ride> rejectRide(@PathVariable Long rideId){
        Ride rejectedRide= driverAssignmentService.handleRideAction(rideId,RideAction.DRIVER_REJECT);
        return ResponseEntity.ok(rejectedRide);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{rideId}/start")
    public ResponseEntity<Ride> startRide(@PathVariable Long rideId){
        Ride startRideData=driverAssignmentService.handleRideAction(rideId,RideAction.START_RIDE);
        return ResponseEntity.ok(startRideData);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{rideId}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable Long rideId){
        Ride completeRide= driverAssignmentService.handleRideAction(rideId,RideAction.COMPLETE_RIDE);
        return ResponseEntity.ok(completeRide);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable Long rideId){
         Ride cancelRide=driverAssignmentService.handleRideAction(rideId,RideAction.CANCEL_RIDE);
         return ResponseEntity.ok(cancelRide);
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @PostMapping("/me/{rideId}/cancel")
    public ResponseEntity<Ride> cancelRideByPassenger(@PathVariable Long rideId){
        Ride cancelRide=rideCommandService.cancelRideByPassenger(rideId);
        return ResponseEntity.ok(cancelRide);
    }
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides() {
        List<Ride>allRides= rideQueryService.getAllRides();
        return ResponseEntity.ok(allRides);

    }
   // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        Ride ride = rideQueryService.getRideById(id);
        return ResponseEntity.ok(ride);
    }
    @PreAuthorize("hasRole('ADMIN')")
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

        Ride savedRide = rideCommandService.updateRide(id, updatedEntity);
        return ResponseEntity.ok(savedRide);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/me/driver/rideHistory")
    public ResponseEntity<List<Ride>>rideDriverHistory(){
        List<Ride>rides= rideQueryService.getDriverRideHistory();
        return ResponseEntity.ok(rides);
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/me/passenger/rideHistory")
    public ResponseEntity<List<Ride>>ridePassengerHistory(){
        List<Ride>rides= rideQueryService.getPassengerRideHistory();
        return ResponseEntity.ok(rides);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideCommandService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}
