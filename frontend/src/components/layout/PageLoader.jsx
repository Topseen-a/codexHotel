import Spinner from "../ui/Spinner";

export default function PageLoader() {
  return (
    <div style={{ display: "grid", placeItems: "center", minHeight: "60vh", color: "var(--accent-text)" }}>
      <Spinner label="Loading page" />
    </div>
  );
}
