package prashant.example.rideSharing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import prashant.example.rideSharing.model.Ride;

@Service
public class RideBroadcastService {

    private final DriverSessionRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RideBroadcastService(DriverSessionRegistry registry) {
        this.registry = registry;
    }

    public void broadcastRide(Ride ride) {
        registry.getAll().forEach((email, session) -> {
            try {
                if (session.isOpen()) {
                    String payload = objectMapper.writeValueAsString(ride);
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (Exception ignored) {}
        });
    }
}
