package prashant.example.rideSharing.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prashant.example.rideSharing.model.Passenger;
public interface PassengerRepository extends JpaRepository<Passenger,Long> {
}
