package com.binluis.parkingsystem.controller;

import com.binluis.parkingsystem.domain.ParkingBoy;
import com.binluis.parkingsystem.domain.ParkingBoyRepository;
import com.binluis.parkingsystem.exception.ResourceNotFoundException;
import com.binluis.parkingsystem.models.ParkingBoyResponse;
import com.binluis.parkingsystem.models.User;
import com.binluis.parkingsystem.payload.*;
import com.binluis.parkingsystem.domain.UserRepository;
import com.binluis.parkingsystem.security.UserPrincipal;
import com.binluis.parkingsystem.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParkingBoyRepository parkingBoyRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('PARKINGCLERK')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping(path = "/user/{id}/parkingclerks")
    public ResponseEntity<ParkingBoy> getAllParkingBoys(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        ParkingBoy parkingBoy=parkingBoyRepository.findOneByName(user.get().getName());
        return ResponseEntity.ok(parkingBoy);
    }
//    @GetMapping("/users/{username}")
//    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//
//        long pollCount = pollRepository.countByCreatedBy(user.getId());
//        long voteCount = voteRepository.countByUserId(user.getId());
//
//        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount);
//
//        return userProfile;
//    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

}