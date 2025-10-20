package com.example.wandoor.model.entity;

import jakarta.persistence.Column;

public class SplitBill {
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    
}
