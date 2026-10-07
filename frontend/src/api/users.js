import { apiClient } from "./client";

export const getAllUsers = () => apiClient.get("/api/users");
export const createStaffUser = (payload) => apiClient.post("/api/users", payload);
export const updateUser = (id, payload) => apiClient.put(`/api/users/${id}`, payload);
export const deleteUser = (id) => apiClient.delete(`/api/users/${id}`);
