import { apiClient } from "./client";

export const makePayment = (payload) => apiClient.post("/api/payments", payload);
export const getPaymentById = (id) => apiClient.get(`/api/payments/${id}`);
export const getPaymentsByBooking = (bookingId) => apiClient.get(`/api/payments/booking/${bookingId}`);
export const getPaymentsByStatus = (successful) => apiClient.get("/api/payments/status", { params: { successful } });
export const markPaymentSuccessful = (id) => apiClient.put(`/api/payments/${id}/success`);
export const deletePayment = (id) => apiClient.delete(`/api/payments/${id}`);
