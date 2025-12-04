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
    @ManyToOne
    @JoinColumn(name="ride_id")
    private Ride ride;
    @ManyToOne
    @JoinColumn(name="driver_id")
    private Driver driver;
    @ManyToOne
    @JoinColumn(name="passenger_id")
    private Passenger passenger;
    private int stars;
    private String comment;
    private LocalDateTime ratedAt=LocalDateTime.now();
}
