package com.example.wandoor.controller;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wandoor.service.ProfileService;
import com.example.wandoor.model.entity.Profile;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController{
    private final ProfileService service;

    public ProfileController(ProfileService service){
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable String id){
        Optional<Profile> profileOpt = service.getProfileById(id);

        Map<String, Object> response = new LinkedHashMap<>();

        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();
            Map<String, Object> filteredProfile = new HashMap<>();
            filteredProfile.put("id", profile.getId());
            filteredProfile.put("cif", profile.getCif());
            filteredProfile.put("username", profile.getUsername());
            filteredProfile.put("first_name", profile.getFirstName());
            filteredProfile.put("middle_name", profile.getMiddleName());
            filteredProfile.put("last_name", profile.getLastName());
            filteredProfile.put("dob", profile.getDob());
            filteredProfile.put("phone_number", profile.getPhoneNumber());
            filteredProfile.put("email_address", profile.getEmailAddress());
            Map<String, Object> data = new HashMap<>();
            data.put("profile", filteredProfile);

            response.put("status", true);
            response.put("message", "Profile retrieved successfully");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } else {
            response.put("status", false);
            response.put("message", "Profile not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}