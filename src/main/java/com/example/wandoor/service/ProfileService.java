package com.example.wandoor.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.model.entity.Profile;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository){
        this.repository = repository;
    }

    public Object getProfile(String id){
        return repository.getProfileById(id);
    }

    // 404 Not Found Exception
    public static class ProfileNotFoundException extends RuntimeException{
        public ProfileNotFoundException(String message){
            super(message);
        }
    }

    // 401 Unauthorize Exception
    public static class UnauthorizeException extends RuntimeException{
        public UnauthorizeException(String message){
            super(message);
        }
    }
    
    public ResponseEntity<Map<String, Object>> getProfileResponse(String id){
        Map<String, Object> response = new LinkedHashMap<>();

        Optional<Profile> profileOpt = repository.findById(id);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
