package prashant.example.rideSharing.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="passengers")

public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true,nullable = false)
    private String email;
    @JsonIgnore
    private String password;
    private String phoneNumber;
    private Double avgRating=0.0;
    private Long totalRating=0L;

}
