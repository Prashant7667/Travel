package prashant.example.rideSharing.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prashant.example.rideSharing.model.Ride;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerEmail(String email);

    List<Ride> findByDriverEmail(String email);

}
