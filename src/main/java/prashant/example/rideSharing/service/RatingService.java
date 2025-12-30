package prashant.example.rideSharing.service;

import jakarta.transaction.Transactional;
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
    public Rating submitDriverRating(Long rideId, Long driverId, int stars, String comment){
        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not found"));
        Driver driver=driverRepository.findById(driverId).orElseThrow(()-> new RuntimeException("Driver Not found"));
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        Rating.RatedBy ratedBy;
        UserDetailsImpl user=(UserDetailsImpl) authentication.getPrincipal();
        if(user.getRole().equals( Rating.RatedBy.DRIVER)){
            ratedBy= Rating.RatedBy.DRIVER;
        }
        else ratedBy= Rating.RatedBy.PASSENGER;
        if(!ride.getStatus().equals(Ride.RideStatus.COMPLETED)){
            throw new IllegalStateException("At this stage ride can't be rated");
        }
        if(ratingRepository.existsByRideIdAndRatedBy(rideId,ratedBy)){
            throw new IllegalStateException("Already rated By This User");
        }
        Passenger passenger=passengerRepository.findByEmail(user.getUsername()).orElseThrow(()->new RuntimeException("User Not found"));
        if(!ride.getDriver().getId().equals(driverId)){
            throw new IllegalStateException("This Driver is not the correct one");
        }
        if(!ride.getPassenger().getId().equals(passenger.getId())){
            throw new IllegalStateException("This Passenger is not authorised to perform this request");
        }

        Rating rideRating=new Rating();
        rideRating.setPassenger(passenger);
        rideRating.setRatedBy(ratedBy);
        rideRating.setRide(ride);
        rideRating.setDriver(driver);
        rideRating.setStars(stars);
        rideRating.setComment(comment);
        rideRating.setRatedBy(ratedBy);
        updateDriverRating(driverId,stars);
        return ratingRepository.save(rideRating);
    }
    @Transactional
    private void updateDriverRating(Long driverId,int stars){
        Driver driver=driverRepository.findById(driverId).orElseThrow(()->new RuntimeException("Driver Not Found"));
        double total=driver.getAvgRating()* driver.getTotalRating();
        total+=stars;
        driver.setTotalRating(driver.getTotalRating()+1);
        driver.setAvgRating(total/(driver.getTotalRating()));
        driverRepository.save(driver);
    }
}
