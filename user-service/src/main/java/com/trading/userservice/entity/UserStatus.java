package com.trading.userservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


public enum UserStatus {
    ACTIVE,
    SUSPENDED,
    BLOCKED
}