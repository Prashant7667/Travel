package prashant.example.rideSharing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private Long id;
    @NotBlank(message = "Name Cannot Be Empty")
    private String name;
    @Email
    @NotBlank(message ="Please Provide a Valid Email" )
    private String email;
    @Size(min=6,message="Password Must be AtLeast 6 Character")
    private String password;
    private String phoneNumber;
    private String vehicleDetails;
    private String availabilityStatus;
}
