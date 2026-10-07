package com.codexhotel.enums;

public enum Role {

    ADMIN,
    MANAGER,
    RECEPTIONIST,
    GUEST;

    public boolean isStaff() {
        return this != GUEST;
    }
}
