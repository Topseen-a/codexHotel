import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../../api/auth";
import Alert from "../../components/ui/Alert";
import PasswordInput from "../../components/ui/PasswordInput";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import AuthLayout from "./AuthLayout";

export default function ResetPasswordPage() {
  useDocumentTitle("Reset password");
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const token = params.get("token") || "";
  const [form, setForm] = useState({ password: "", confirm: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    if (form.password !== form.confirm) {
      setError("Passwords don't match.");
      return;
    }
    setSubmitting(true);
    try {
      await resetPassword(token, form.password);
      navigate("/login", {
        replace: true,
        state: { notice: "Your password has been reset. Sign in with your new password." },
      });
    } catch (err) {
      setError(err.message);
      setSubmitting(false);
    }
  };

  const footer = (
    <>
      Need a new link? <Link to="/forgot-password">Request another</Link>
    </>
  );

  if (!token) {
    return (
      <AuthLayout title="Reset link missing" subtitle="This page needs the link from your reset email." footer={footer}>
        <div className="auth-form">
          <Alert tone="warning">Open the link from your email again, or request a new one.</Alert>
        </div>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout title="Choose a new password" subtitle="Pick something at least 8 characters long." footer={footer}>
      <form className="auth-form" onSubmit={submit}>
        <Alert tone="danger">{error}</Alert>
        <div className="field">
          <label htmlFor="new-password">New password</label>
          <PasswordInput
            id="new-password"
            autoComplete="new-password"
            required
            minLength={8}
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </div>
        <div className="field">
          <label htmlFor="confirm-password">Confirm new password</label>
          <PasswordInput
            id="confirm-password"
            autoComplete="new-password"
            required
            value={form.confirm}
            onChange={(e) => setForm({ ...form, confirm: e.target.value })}
          />
        </div>
        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Saving…" : "Reset password"}
        </button>
      </form>
    </AuthLayout>
  );
}
