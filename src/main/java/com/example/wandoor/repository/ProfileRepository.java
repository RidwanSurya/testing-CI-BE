package com.example.wandoor.repository;

import com.example.wandoor.model.entity.Profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> getProfileById(String id);
}
