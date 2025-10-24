package com.example.wandoor.model.response;
import java.security.Timestamp;
import java.time.LocalDateTime;

public record ProfileResponse (
    boolean status,
    String message,
    Data data 
){
    public record Data(
        String id,
        String cif,
        String username,
        String firstName,
        String middleName,
        String lastName,
        LocalDateTime dob,
        Number phoneNumber,
        String emailAddress
    ){}
}