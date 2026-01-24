package prashant.example.rideSharing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ApiErrorResponse {
    private  String Code;
    private String Message;
}
