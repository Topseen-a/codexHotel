import { Link } from "react-router-dom";
import EmptyState from "../components/ui/EmptyState";
import { useDocumentTitle } from "../hooks/useDocumentTitle";

export default function NotFoundPage() {
  useDocumentTitle("Page not found");

  return (
    <div className="app-page container">
      <EmptyState
        icon="mapPin"
        title="Page not found"
        action={
          <Link to="/" className="btn btn-primary">
            Back to home
          </Link>
        }
      >
        That page doesn&apos;t exist, or you don&apos;t have access to it.
      </EmptyState>
    </div>
  );
}
