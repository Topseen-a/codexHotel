import { apiClient } from "./client";

export const getPriceList = () => apiClient.get("/api/pricing");
