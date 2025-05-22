package com.example.sepdrivebackend.controller;

import com.example.sepdrivebackend.dto.UserProfileDto;
import com.example.sepdrivebackend.model.User;
import com.example.sepdrivebackend.repository.UserRepository;
import com.example.sepdrivebackend.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private UserRepository userRepository;

    public UserController(UserService userService) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/search")
    public List<UserProfileDto> searchUser(@RequestParam("q") String query) {
        return userService.searchUsers(query);
    }


    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile(Authentication authentication) {
        String username = authentication.getName(); // holt den eingeloggten Benutzer
        UserProfileDto profile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String username) {
        UserProfileDto profile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{id}/upload-profileImage")
    public ResponseEntity<String> uploadProfileImage(@PathVariable long id, @RequestParam("profileImage") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!supportedImages(contentType)) {
            return ResponseEntity.badRequest().body("Es werden nur Bilder mit dem Dateiformat .jpeg, .png oder .gif akzeptiert");
        }
        userService.saveProfileImage(id, file);
        System.out.println("Dateiname: " + file.getOriginalFilename());
        return ResponseEntity.ok("Profilbild wurde erfolgreich hochgeladen");
    }

    private boolean supportedImages(String type) {
        return type != null && (
                type.equalsIgnoreCase("image/jpeg") ||
                        type.equalsIgnoreCase("image/png") ||
                        type.equalsIgnoreCase("image/gif")
        );
    }

    @GetMapping("/{id}/profileImage")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Der Benutzer konnte nicht gefunden werden"));
        byte[] image = user.getProfilePicture();
        String contentType = user.getProfileImageContentType();
        if (image == null || contentType == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/profileImage")
    public ResponseEntity<String> deleteProfileImage(@PathVariable Long id) {
        userService.deleteProfileImage(id);
        return ResponseEntity.ok("Profilbild zurückgesetzt");
    }

}
