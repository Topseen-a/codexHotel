import { apiClient } from "./client";

export const createBooking = (payload) => apiClient.post("/api/bookings", payload);
export const cancelBooking = (bookingId) => apiClient.put("/api/bookings/cancel", { bookingId });
export const getBookingById = (id) => apiClient.get(`/api/bookings/${id}`);
export const getBookingsByUser = (userId) => apiClient.get(`/api/bookings/user/${userId}`);
export const getBookingsByStatus = (status) => apiClient.get("/api/bookings/status", { params: { status } });
