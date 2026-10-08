import { apiClient } from "./client";

export const getPriceList = () => apiClient.get("/api/pricing");

/** Nightly price for one date; `date` is an ISO yyyy-mm-dd string. */
export const calculatePrice = ({ roomType, basePrice, date }) =>
  apiClient.get("/api/pricing/calculate", { params: { roomType, basePrice, date } });
