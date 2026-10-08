import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { deleteUser, updateUser } from "../../api/users";
import Alert from "../../components/ui/Alert";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import Icon from "../../components/ui/Icon";
import PasswordInput from "../../components/ui/PasswordInput";
import { useAuth } from "../../context/useAuth";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { initials } from "../../utils/format";
import { ROLE_LABELS } from "../../utils/roles";
import "./Account.css";

const PHONE_PATTERN = "^(\\+234|0)[789][01]\\d{8}$";

export default function AccountPage() {
  useDocumentTitle("Account settings");
  const { user, setUser, login, logout, isStaff } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: user.name,
    email: user.email,
    phoneNumber: user.phoneNumber,
    password: "",
    confirmPassword: "",
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [message, setMessage] = useState("");
  const [confirmDelete, setConfirmDelete] = useState(false);

  const update = (field) => (e) => {
    setForm({ ...form, [field]: e.target.value });
    setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");
    setFieldErrors({});

    if (form.password && form.password !== form.confirmPassword) {
      setFieldErrors({ confirmPassword: "Passwords don't match" });
      return;
    }

    const payload = {
      name: form.name.trim(),
      email: form.email.trim(),
      phoneNumber: form.phoneNumber.trim(),
      ...(form.password ? { password: form.password } : {}),
    };

    setSaving(true);
    try {
      const updated = await updateUser(user.id, payload);
      if (updated.email !== user.email) {
        // The session token is tied to the old email, so sign in again with the new one.
        logout();
        navigate("/login", { replace: true });
        return;
      }
      if (payload.password) {
        // Changing the password signs out every existing session, this one
        // included — sign straight back in with the new password.
        await login(updated.email, payload.password);
      } else {
        setUser(updated);
      }
      setForm((prev) => ({ ...prev, password: "", confirmPassword: "" }));
      setMessage(
        payload.password
          ? "Your details have been saved. Your password was changed and other devices have been signed out."
          : "Your details have been saved."
      );
    } catch (err) {
      setFieldErrors(err.fieldErrors || {});
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const removeAccount = async () => {
    await deleteUser(user.id);
    logout();
    navigate("/", { replace: true });
  };

  const err = (field) =>
    fieldErrors[field] ? (
      <span className="field-error" id={`${field}-error`}>
        {fieldErrors[field]}
      </span>
    ) : null;

  return (
    <div className="app-page container account">
      <header className="app-page-header">
        <div>
          <span className="eyebrow">Account</span>
          <h1>Account settings</h1>
          <p>Update your contact details and password.</p>
        </div>
      </header>

      <div className="account-layout">
        <aside className="card card-pad account-profile">
          <span className="avatar avatar-lg">{initials(user.name)}</span>
          <h2>{user.name}</h2>
          <span className="badge badge-info">{ROLE_LABELS[user.role]}</span>
          <nav className="account-links">
            <Link to="/bookings">
              <Icon name="calendar" size={18} /> My bookings
            </Link>
            {isStaff && (
              <Link to="/admin">
                <Icon name="grid" size={18} /> Staff dashboard
              </Link>
            )}
          </nav>
        </aside>

        <div className="stack" style={{ "--stack-gap": "24px" }}>
          <form className="card card-pad stack" onSubmit={submit}>
            <h2 className="card-title">Personal details</h2>
            <Alert tone="success">{message}</Alert>
            <Alert tone="danger">{error}</Alert>
            <div className="form-grid">
              <div className="field">
                <label htmlFor="acc-name">Full name</label>
                <input id="acc-name" required value={form.name} onChange={update("name")} aria-invalid={Boolean(fieldErrors.name)} />
                {err("name")}
              </div>
              <div className="field">
                <label htmlFor="acc-phone">Phone number</label>
                <input
                  id="acc-phone"
                  type="tel"
                  required
                  pattern={PHONE_PATTERN}
                  title="A Nigerian number, e.g. 08012345678"
                  value={form.phoneNumber}
                  onChange={update("phoneNumber")}
                  aria-invalid={Boolean(fieldErrors.phoneNumber)}
                />
                {err("phoneNumber")}
              </div>
            </div>
            <div className="field">
              <label htmlFor="acc-email">Email</label>
              <input
                id="acc-email"
                type="email"
                required
                value={form.email}
                onChange={update("email")}
                aria-invalid={Boolean(fieldErrors.email)}
              />
              {err("email") || (
                <span className="field-hint">Changing your email will sign you out so you can log in with the new one.</span>
              )}
            </div>

            <h3 className="account-subtitle">Change password</h3>
            <div className="form-grid">
              <div className="field">
                <label htmlFor="acc-password">New password</label>
                <PasswordInput
                  id="acc-password"
                  autoComplete="new-password"
                  minLength={8}
                  value={form.password}
                  onChange={update("password")}
                  aria-invalid={Boolean(fieldErrors.password)}
                />
                {err("password") || <span className="field-hint">Leave blank to keep your current password</span>}
              </div>
              <div className="field">
                <label htmlFor="acc-confirm">Confirm new password</label>
                <PasswordInput
                  id="acc-confirm"
                  autoComplete="new-password"
                  value={form.confirmPassword}
                  onChange={update("confirmPassword")}
                  aria-invalid={Boolean(fieldErrors.confirmPassword)}
                />
                {err("confirmPassword")}
              </div>
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? "Saving…" : "Save changes"}
              </button>
            </div>
          </form>

          <section className="card card-pad account-danger">
            <div>
              <h2 className="card-title">Delete account</h2>
              <p className="muted">Permanently remove your account and sign out. This can&apos;t be undone.</p>
            </div>
            <button type="button" className="btn btn-danger-outline" onClick={() => setConfirmDelete(true)}>
              <Icon name="trash" size={16} /> Delete account
            </button>
          </section>
        </div>
      </div>

      <ConfirmDialog
        open={confirmDelete}
        title="Delete your account?"
        confirmLabel="Delete my account"
        onConfirm={removeAccount}
        onClose={() => setConfirmDelete(false)}
      >
        Your profile will be removed and you&apos;ll be signed out immediately.
      </ConfirmDialog>
    </div>
  );
}
