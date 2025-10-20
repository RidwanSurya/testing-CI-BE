package com.example.wandoor.model.response;
import java.security.Timestamp;

public record ProfileResponse (
    boolean status,
    String message,
    Data data 
){
    public record Data(
        String id,
        String cif,
        String username,
        String first_name,
        String middle_name,
        String lastname,
        Timestamp dob,
        Number phone_number,
        String email_address
    ){}
}