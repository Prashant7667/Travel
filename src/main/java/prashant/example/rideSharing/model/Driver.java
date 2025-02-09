package prashant.example.rideSharing.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.availability.AvailabilityState;

@Entity
@Data
@AllArgsConstructor
@Table(name="drivers")

public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String phoneNumber;
    private String vehicleDetails;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNAVAILABLE;

    public Driver() {}
    public enum AvailabilityStatus{
        AVAILABLE,
        UNAVAILABLE
    }
}

