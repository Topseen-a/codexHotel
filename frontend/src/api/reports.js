import { apiClient } from "./client";

export const getReport = () => apiClient.get("/api/reports");
