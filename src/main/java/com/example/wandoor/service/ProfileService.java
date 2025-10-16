package com.example.wandoor.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.model.entity.Profile;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository){
        this.repository = repository;
    }

    public Optional<Profile> getProfileById(String id){
        return repository.findById(id);
    }
}
