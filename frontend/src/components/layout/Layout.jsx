import { Link, Outlet } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import Alert from "../ui/Alert";
import Footer from "./Footer";
import Navbar from "./Navbar";
import ScrollManager from "./ScrollManager";
import ScrollReveal from "./ScrollReveal";

export default function Layout({ showFooter = true }) {
  const { sessionExpired, isAuthenticated } = useAuth();

  return (
    <div className="app-shell">
      <ScrollManager />
      <ScrollReveal />
      <a href="#main" className="skip-link">
        Skip to content
      </a>
      <Navbar />
      {sessionExpired && !isAuthenticated && (
        <div className="session-banner">
          <Alert tone="warning">
            Your session has expired. Please <Link to="/login">log in</Link> again to continue.
          </Alert>
        </div>
      )}
      <main id="main" className="app-main">
        <Outlet />
      </main>
      {showFooter && <Footer />}
    </div>
  );
}
