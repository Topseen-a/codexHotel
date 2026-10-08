// Mirrors the backend's @PreAuthorize rules so the UI only offers actions
// the API will actually allow. The backend remains the source of truth.

export const ROLES = {
  ADMIN: "ADMIN",
  MANAGER: "MANAGER",
  RECEPTIONIST: "RECEPTIONIST",
  GUEST: "GUEST",
};

export const ALL_ROLES = [ROLES.GUEST, ROLES.RECEPTIONIST, ROLES.MANAGER, ROLES.ADMIN];

const STAFF = [ROLES.ADMIN, ROLES.MANAGER, ROLES.RECEPTIONIST];
const MANAGEMENT = [ROLES.ADMIN, ROLES.MANAGER];

export const PERMISSIONS = {
  viewDashboard: STAFF,
  viewReports: MANAGEMENT,
  manageRooms: MANAGEMENT,
  updateRoomStatus: STAFF,
  manageBookings: STAFF,
  managePayments: STAFF,
  deletePayments: [ROLES.ADMIN],
  lookupGuests: STAFF,
  manageUsers: [ROLES.ADMIN],
};

export const can = (user, permission) => Boolean(user) && PERMISSIONS[permission].includes(user.role);

export const isStaffRole = (role) => STAFF.includes(role);

export const ROLE_LABELS = {
  ADMIN: "Administrator",
  MANAGER: "Manager",
  RECEPTIONIST: "Receptionist",
  GUEST: "Guest",
};
