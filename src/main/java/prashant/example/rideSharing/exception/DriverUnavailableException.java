package prashant.example.rideSharing.exception;

public class DriverUnavailableException extends RuntimeException {
    public DriverUnavailableException(String message) {
        super(message);
    }
}
