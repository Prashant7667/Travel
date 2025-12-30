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

import java.time.LocalDateTime;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final RideRepository rideRepository;
    public RatingService(RatingRepository ratingRepository,DriverRepository driverRepository,PassengerRepository passengerRepository,RideRepository rideRepository){
        this.ratingRepository=ratingRepository;
        this.driverRepository=driverRepository;
        this.passengerRepository=passengerRepository;
        this.rideRepository=rideRepository;
    }
    @Transactional
    public Rating submitRating(Long rideId, int stars, String comment){
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not found"));
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user=(UserDetailsImpl) authentication.getPrincipal();
        if(!ride.getStatus().equals(Ride.RideStatus.COMPLETED)){
            throw new IllegalStateException("At this stage ride can't be rated");
        }
        Rating rideRating=new Rating();
        Rating.RatedBy ratedBy;
        rideRating.setRide(ride);
        rideRating.setStars(stars);
        rideRating.setComment(comment);
        rideRating.setRatedAt(LocalDateTime.now());
        if(user.getRole().equals("DRIVER")){
            ratedBy= Rating.RatedBy.DRIVER;
            if(ratingRepository.existsByRideIdAndRatedBy(rideId,ratedBy)){
                throw new IllegalStateException("Ride is already rated by this user");
            }
            Driver driver=driverRepository.findByEmail(user.getUsername()).orElseThrow(()->new IllegalArgumentException("driver not found"));
            if(!driver.getId().equals(ride.getDriver().getId())){
                throw new IllegalStateException("Not Your Ride");
            }
            Passenger passenger=ride.getPassenger();
            updatePassengerRating(passenger.getId(),stars);
            rideRating.setRatedBy(ratedBy);
            rideRating.setPassenger(passenger);
            rideRating.setDriver(driver);
        }else{
            ratedBy= Rating.RatedBy.PASSENGER;
            if(ratingRepository.existsByRideIdAndRatedBy(rideId,ratedBy)){
                throw new IllegalStateException("Ride is already rated by this user");
            }
            Passenger passenger=passengerRepository.findByEmail(user.getUsername()).orElseThrow(()->new IllegalArgumentException("passenger not found"));
            if(!passenger.getId().equals(ride.getPassenger().getId())){
                throw new IllegalStateException("Not Your Ride");
            }
            Driver driver=ride.getDriver();
            updateDriverRating(driver.getId(),stars);

            rideRating.setRatedBy(ratedBy);
            rideRating.setPassenger(passenger);
            rideRating.setDriver(driver);
        }
        return ratingRepository.save(rideRating);
    }
    private void updateDriverRating(Long driverId,int stars){
        Driver driver=driverRepository.findById(driverId).orElseThrow(()->new RuntimeException("Driver Not Found"));
        double total=driver.getAvgRating()* driver.getTotalRating();
        total+=stars;
        driver.setTotalRating(driver.getTotalRating()+1);
        driver.setAvgRating(total/(driver.getTotalRating()));
        driverRepository.save(driver);
    }
    private void updatePassengerRating(Long passengerId,int stars){
        Passenger passenger=passengerRepository.findById(passengerId).orElseThrow(()->new RuntimeException("Passenger Not Found"));
        double total=passenger.getAvgRating()* passenger.getTotalRating();
        total+=stars;
        passenger.setTotalRating(passenger.getTotalRating()+1);
        passenger.setAvgRating(total/(passenger.getTotalRating()));
        passengerRepository.save(passenger);
    }
}
