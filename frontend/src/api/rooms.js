import { apiClient } from "./client";

export const listRooms = () => apiClient.get("/api/rooms");
export const getRoomById = (id) => apiClient.get(`/api/rooms/${id}`);
export const getRoomByNumber = (roomNumber) => apiClient.get(`/api/rooms/number/${roomNumber}`);
export const listRoomsByStatus = (status) => apiClient.get("/api/rooms/status", { params: { status } });
export const createRoom = (payload) => apiClient.post("/api/rooms", payload);
export const updateRoomStatus = (roomId, roomStatus) => apiClient.put("/api/rooms/status", { roomId, roomStatus });
export const deleteRoom = (id) => apiClient.delete(`/api/rooms/${id}`);
