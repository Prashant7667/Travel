package prashant.example.rideSharing.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

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
    private Double latitude;
    private Double longitude;
    private Double AvgRating=0.0;
    private Long TotalRating=0L;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNAVAILABLE;

    public Driver() {}
    public enum AvailabilityStatus{
        AVAILABLE,
        UNAVAILABLE
    }
}

