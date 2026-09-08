import { apiClient } from "./client";

export const listRooms = () => apiClient.get("/api/rooms");
export const getRoomById = (id) => apiClient.get(`/api/rooms/${id}`);
export const listRoomsByStatus = (status) => apiClient.get("/api/rooms/status", { params: { status } });
export const createRoom = (payload) => apiClient.post("/api/rooms", payload);
export const updateRoomStatus = (payload) => apiClient.put("/api/rooms/status", payload);
export const deleteRoom = (id) => apiClient.delete(`/api/rooms/${id}`);
