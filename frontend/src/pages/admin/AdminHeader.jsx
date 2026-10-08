export default function AdminHeader({ title, description, actions }) {
  return (
    <header className="admin-header">
      <div>
        <h1>{title}</h1>
        {description && <p className="muted">{description}</p>}
      </div>
      {actions && <div className="cluster">{actions}</div>}
    </header>
  );
}
