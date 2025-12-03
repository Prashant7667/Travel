package prashant.example.rideSharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Passenger;
import prashant.example.rideSharing.model.Rating;
import prashant.example.rideSharing.model.Ride;
import prashant.example.rideSharing.repository.DriverRepository;
import prashant.example.rideSharing.repository.PassengerRepository;
import prashant.example.rideSharing.repository.RatingRepository;
import prashant.example.rideSharing.repository.RideRepository;
@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private RideRepository rideRepository;
    public Rating submitRating(Long rideId, Long driverId,int stars, String comment){
        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not found"));
        if(!ride.getStatus().equals("Completed")){
            throw new IllegalStateException("At this stage ride can't be rated");
        }
//        if(ratingRepository.existByRideId(rideId)){
//            throw new IllegalStateException("Already rated");
//        }
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user=(UserDetailsImpl) authentication.getPrincipal();
        Passenger passenger=passengerRepository.findByEmail(user.getUsername()).orElseThrow(()->new RuntimeException("User Not found"));
        if(!ride.getPassenger().equals(passenger.getId())){
            throw new IllegalStateException("This Passenger is not authorised to perform this request");
        }


        Rating rideRating=new Rating();
        rideRating.setPassengerId(passenger.getId());
        rideRating.setRideId(rideId);
        rideRating.setDriverId(driverId);
        rideRating.setStars(stars);
        rideRating.setComment(comment);
        updateDriverRating(driverId,stars);
        return ratingRepository.save(rideRating);
    }
    private void updateDriverRating(Long driverId,int stars){
        Driver driver=driverRepository.findById(driverId).orElseThrow(()->new RuntimeException("Driver Not Found"));
        double total=driver.getAvgRating()* driver.getTotalRating();
        total+=stars;
        driver.setTotalRating(driver.getTotalRating()+stars);
        driver.setAvgRating(total/(driver.getTotalRating()));
        driverRepository.save(driver);
    }
}
