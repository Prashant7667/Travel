package prashant.example.rideSharing.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prashant.example.rideSharing.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long>{
    Driver findFirstByAvailabilityStatus(Driver.AvailabilityStatus availabilityStatus);
}
