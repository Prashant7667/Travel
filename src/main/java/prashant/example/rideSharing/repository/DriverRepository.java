package prashant.example.rideSharing.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prashant.example.rideSharing.model.Driver;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long>{
    Optional<Driver>findByEmail(String email);
    @Query(
            value = """
        SELECT *
        FROM driver d
        WHERE d.availability_status = 'AVAILABLE'
          AND d.latitude BETWEEN :minLat AND :maxLat
          AND d.longitude BETWEEN :minLon AND :maxLon
        ORDER BY (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(d.latitude))
                * cos(radians(d.longitude) - radians(:lon))
                + sin(radians(:lat)) * sin(radians(d.latitude))
            )
        )
        LIMIT 1
        """,
            nativeQuery = true
    )
    Driver findNearestDriver(
            @Param("lat")double lat,
            @Param("lon")double lon,
            @Param("minLat")double minLat,
            @Param("maxLat")double maxLat,
            @Param("minLon")double minLon,
            @Param("maxLon")double maxLon
    );
}
