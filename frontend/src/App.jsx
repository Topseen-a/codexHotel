import { BrowserRouter, Route, Routes } from "react-router-dom";
import Layout from "./components/layout/Layout";
import { GuestOnly, RequireAuth, RequirePermission } from "./components/layout/RouteGuards";
import { AuthProvider } from "./context/AuthContext";
import AboutPage from "./pages/AboutPage";
import AccountPage from "./pages/account/AccountPage";
import BookingDetailPage from "./pages/account/BookingDetailPage";
import MyBookingsPage from "./pages/account/MyBookingsPage";
import AdminIndex from "./pages/admin/AdminIndex";
import AdminLayout from "./pages/admin/AdminLayout";
import BookingsAdminPage from "./pages/admin/BookingsAdminPage";
import GuestsPage from "./pages/admin/GuestsPage";
import OverviewPage from "./pages/admin/OverviewPage";
import PaymentsAdminPage from "./pages/admin/PaymentsAdminPage";
import PricingPage from "./pages/admin/PricingPage";
import RoomsAdminPage from "./pages/admin/RoomsAdminPage";
import UsersPage from "./pages/admin/UsersPage";
import ForgotPasswordPage from "./pages/auth/ForgotPasswordPage";
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import ResetPasswordPage from "./pages/auth/ResetPasswordPage";
import ContactPage from "./pages/ContactPage";
import ExperiencesPage from "./pages/ExperiencesPage";
import GalleryPage from "./pages/GalleryPage";
import HomePage from "./pages/HomePage";
import NotFoundPage from "./pages/NotFoundPage";
import RoomDetailPage from "./pages/RoomDetailPage";
import RoomsPage from "./pages/RoomsPage";

const guard = (permission, element) => (
  <RequirePermission permission={permission} fallback="/admin">
    {element}
  </RequirePermission>
);

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public site */}
          <Route element={<Layout />}>
            <Route index element={<HomePage />} />
            <Route path="rooms" element={<RoomsPage />} />
            <Route path="rooms/:roomType" element={<RoomDetailPage />} />
            <Route path="experiences" element={<ExperiencesPage />} />
            <Route path="gallery" element={<GalleryPage />} />
            <Route path="about" element={<AboutPage />} />
            <Route path="contact" element={<ContactPage />} />

            {/* Signed-in guests (and staff viewing a guest's booking) */}
            <Route path="bookings" element={<RequireAuth><MyBookingsPage /></RequireAuth>} />
            <Route path="bookings/:bookingId" element={<RequireAuth><BookingDetailPage /></RequireAuth>} />
            <Route path="account" element={<RequireAuth><AccountPage /></RequireAuth>} />

            <Route path="*" element={<NotFoundPage />} />
          </Route>

          {/* Staff dashboard — sections gated by role, mirroring the API's @PreAuthorize rules */}
          <Route element={<Layout showFooter={false} />}>
            <Route
              path="admin"
              element={
                <RequirePermission permission="viewDashboard">
                  <AdminLayout />
                </RequirePermission>
              }
            >
              <Route index element={<AdminIndex />} />
              <Route path="overview" element={guard("viewReports", <OverviewPage />)} />
              <Route path="bookings" element={guard("manageBookings", <BookingsAdminPage />)} />
              <Route path="rooms" element={guard("updateRoomStatus", <RoomsAdminPage />)} />
              <Route path="payments" element={guard("managePayments", <PaymentsAdminPage />)} />
              <Route path="guests" element={guard("lookupGuests", <GuestsPage />)} />
              <Route path="users" element={guard("manageUsers", <UsersPage />)} />
              <Route path="pricing" element={guard("viewDashboard", <PricingPage />)} />
            </Route>
          </Route>

          {/* Full-screen auth pages */}
          <Route path="login" element={<GuestOnly><LoginPage /></GuestOnly>} />
          <Route path="register" element={<GuestOnly><RegisterPage /></GuestOnly>} />
          <Route path="forgot-password" element={<GuestOnly><ForgotPasswordPage /></GuestOnly>} />
          {/* Not GuestOnly: a signed-in user may still follow a reset link from email. */}
          <Route path="reset-password" element={<ResetPasswordPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
