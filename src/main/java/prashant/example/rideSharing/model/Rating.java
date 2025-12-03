package prashant.example.rideSharing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ratings")

public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long rideId;
    private Long driverId;
    private Long passengerId;
    private int stars;
    private String comment;
    private LocalDateTime ratedAt=LocalDateTime.now();
}
