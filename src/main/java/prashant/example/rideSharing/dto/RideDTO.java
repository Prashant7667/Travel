package prashant.example.rideSharing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDTO {
    private Long id;

    @NotBlank(message = "Start Location Cannot Be Empty")
    private String startLocation;

    @NotBlank(message = "End Location Cannot Be Empty")
    private String endLocation;

    private String status;

    private Double fare;

    @NotNull(message = "Passenger Id Cannot Be Null")
    private Long passengerId;
    private Long driverId;
}
