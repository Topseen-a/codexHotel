import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <div className="center-page stack" style={{ textAlign: "center", gap: 16 }}>
      <h1>Page not found</h1>
      <p className="muted">That page doesn't exist — or you don't have access to it.</p>
      <Link to="/rooms" className="btn btn-primary">
        Back to rooms
      </Link>
    </div>
  );
}
