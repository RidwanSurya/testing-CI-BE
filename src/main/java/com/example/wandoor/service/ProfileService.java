package com.example.wandoor.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.model.response.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.model.entity.Profile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }
    
    public ProfileResponse getProfile(){
        var userId = RequestContext.get().getUserId();
        var cif = RequestContext.get().getCif();

        var userExists =  repository.findByIdAndCif(userId, cif)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var data =  new ProfileResponse.Data(
                userExists.getId(),
                userExists.getCif(),
                userExists.getUsername(),
                userExists.getFirstName(),
                userExists.getMiddleName(),
                userExists.getLastName(),
                userExists.getDob(),
                userExists.getPhoneNumber(),
                userExists.getEmailAddress()
        );

        return new ProfileResponse(
                true,
                "Profile retrieved successfully",
                data
        );
    }
}
