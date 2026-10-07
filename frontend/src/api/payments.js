import { apiClient } from "./client";

export const makePayment = (payload) => apiClient.post("/api/payments", payload);
export const getPaymentsByBooking = (bookingId) => apiClient.get(`/api/payments/booking/${bookingId}`);
