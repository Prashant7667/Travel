package prashant.example.rideSharing.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prashant.example.rideSharing.model.Driver;
import prashant.example.rideSharing.model.Ride;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerEmail(String email);

    List<Ride> findByDriverEmail(String email);
    @Modifying
    @Query("""
            UPDATE Ride r
            SET r.status='ACCEPTED',r.driver=:driver
            WHERE r.id=:rideId AND r.status='REQUESTED'
            """
    )
    int acceptRide(@Param("rideId") Long rideId,
                   @Param("driver") Driver driver);

}
