// Display metadata for backend enums.

export const BOOKING_STATUS = {
  CONFIRMED: { label: "Confirmed", tone: "success" },
  CANCELLED: { label: "Cancelled", tone: "danger" },
  COMPLETED: { label: "Completed", tone: "neutral" },
};

export const ROOM_STATUS = {
  AVAILABLE: { label: "Available", tone: "success" },
  OCCUPIED: { label: "Occupied", tone: "info" },
  MAINTENANCE: { label: "Maintenance", tone: "warning" },
};

export const PAYMENT_METHODS = [
  { value: "CARD", label: "Card" },
  { value: "TRANSFER", label: "Bank transfer" },
  { value: "CASH", label: "Cash" },
];

export const SEASONS = {
  WEEKDAY: { label: "Weekday", hint: "Mon – Fri" },
  WEEKEND: { label: "Weekend", hint: "Sat & Sun" },
  FESTIVE: { label: "Festive", hint: "All of December" },
};
