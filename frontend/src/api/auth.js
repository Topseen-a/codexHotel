import { apiClient } from "./client";

export const register = (payload) => apiClient.post("/api/auth/register", payload);
export const login = (payload) => apiClient.post("/api/auth/login", payload);
export const me = () => apiClient.get("/api/auth/me");
export const forgotPassword = (email) => apiClient.post("/api/auth/forgot-password", { email });
export const resetPassword = (token, newPassword) => apiClient.post("/api/auth/reset-password", { token, newPassword });
