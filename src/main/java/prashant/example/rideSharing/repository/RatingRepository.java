package prashant.example.rideSharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prashant.example.rideSharing.model.Rating;
@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {
    boolean existsByRideIdAndRatedBy(Long rideId, Rating.RatedBy ratedBy);

}
