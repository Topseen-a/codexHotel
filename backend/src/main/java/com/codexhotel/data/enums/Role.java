package com.codexhotel.data.enums;

/**
 * Roles supported by the hotel management system.
 *
 * ADMIN        - full system access: manage staff accounts, rooms, bookings, payments, reports.
 * MANAGER      - manage rooms, pricing, view reports; cannot manage other staff accounts.
 * RECEPTIONIST - front-desk operations: manage bookings/payments for any guest, update room status.
 * GUEST        - self-service only: own profile, own bookings, own payments.
 */
public enum Role {

    ADMIN,
    MANAGER,
    RECEPTIONIST,
    GUEST
}
