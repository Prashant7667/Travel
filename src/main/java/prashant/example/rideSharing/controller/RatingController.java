package prashant.example.rideSharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prashant.example.rideSharing.model.Rating;
import prashant.example.rideSharing.service.RatingService;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;
    @PostMapping("/driver/{rideId}")
    public ResponseEntity<Rating> submitRating (@PathVariable Long rideId, @RequestParam int star, @RequestParam(required = false) String comment){
        Rating rating= ratingService.submitRating(rideId,star, comment);
        return ResponseEntity.ok(rating);
    }

}
