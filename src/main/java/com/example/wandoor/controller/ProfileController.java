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
        return service.getProfileResponse(id);
    }
}