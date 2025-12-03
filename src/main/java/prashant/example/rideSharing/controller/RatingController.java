package prashant.example.rideSharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import prashant.example.rideSharing.model.Rating;
import prashant.example.rideSharing.service.RatingService;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;
    @PostMapping("/submit/{rideId}")
    public Rating submitRating (@PathVariable Long rideId, @RequestParam Long driverId,@RequestParam int star, @RequestParam String comment){
        return ratingService.submitRating(rideId,driverId,star, comment);
    }
}
