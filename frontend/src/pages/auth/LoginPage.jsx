import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import Alert from "../../components/ui/Alert";
import PasswordInput from "../../components/ui/PasswordInput";
import { useAuth } from "../../context/useAuth";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { isStaffRole } from "../../utils/roles";
import AuthLayout from "./AuthLayout";

export default function LoginPage() {
  useDocumentTitle("Log in");
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const from = location.state?.from;

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const user = await login(form.email.trim(), form.password);
      const fallback = isStaffRole(user.role) ? "/admin" : "/";
      navigate(from ? `${from.pathname}${from.search || ""}` : fallback, { replace: true });
    } catch (err) {
      setError(err.message);
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout
      title="Welcome back"
      subtitle="Log in to book a room or manage your stay."
      footer={
        <>
          New here?{" "}
          <Link to="/register" state={location.state}>
            Create an account
          </Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={submit}>
        <Alert tone="success">{location.state?.notice}</Alert>
        <Alert tone="danger">{error}</Alert>
        <div className="field">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            required
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
        </div>
        <div className="field">
          <div className="field-label-row">
            <label htmlFor="password">Password</label>
            <Link to="/forgot-password" className="link field-label-link">
              Forgot password?
            </Link>
          </div>
          <PasswordInput
            id="password"
            autoComplete="current-password"
            required
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </div>
        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Logging in…" : "Log in"}
        </button>
      </form>
    </AuthLayout>
  );
}
