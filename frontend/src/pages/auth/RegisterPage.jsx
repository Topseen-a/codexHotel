import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import Alert from "../../components/ui/Alert";
import PasswordInput from "../../components/ui/PasswordInput";
import { useAuth } from "../../context/useAuth";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import AuthLayout from "./AuthLayout";

const PHONE_PATTERN = "^(\\+234|0)[789][01]\\d{8}$";

export default function RegisterPage() {
  useDocumentTitle("Create account");
  const { register } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ name: "", email: "", phoneNumber: "", password: "" });
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const from = location.state?.from;

  const update = (field) => (e) => {
    setForm({ ...form, [field]: e.target.value });
    setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setFieldErrors({});
    setSubmitting(true);
    try {
      await register({ ...form, name: form.name.trim(), email: form.email.trim(), phoneNumber: form.phoneNumber.trim() });
      navigate(from ? `${from.pathname}${from.search || ""}` : "/rooms", { replace: true });
    } catch (err) {
      setFieldErrors(err.fieldErrors || {});
      setError(Object.keys(err.fieldErrors || {}).length ? "Please fix the highlighted fields." : err.message);
      setSubmitting(false);
    }
  };

  const fieldProps = (field) => ({
    "aria-invalid": Boolean(fieldErrors[field]),
    "aria-describedby": fieldErrors[field] ? `${field}-error` : undefined,
  });

  const fieldError = (field) =>
    fieldErrors[field] ? (
      <span id={`${field}-error`} className="field-error">
        {fieldErrors[field]}
      </span>
    ) : null;

  return (
    <AuthLayout
      title="Create your account"
      subtitle="Book rooms, pay online and manage your stays in one place."
      footer={
        <>
          Already have an account?{" "}
          <Link to="/login" state={location.state}>
            Log in
          </Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={submit}>
        <Alert tone="danger">{error}</Alert>
        <div className="field">
          <label htmlFor="name">Full name</label>
          <input id="name" autoComplete="name" required value={form.name} onChange={update("name")} {...fieldProps("name")} />
          {fieldError("name")}
        </div>
        <div className="field">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            required
            value={form.email}
            onChange={update("email")}
            {...fieldProps("email")}
          />
          {fieldError("email")}
        </div>
        <div className="field">
          <label htmlFor="phoneNumber">Phone number</label>
          <input
            id="phoneNumber"
            type="tel"
            autoComplete="tel"
            required
            pattern={PHONE_PATTERN}
            title="A Nigerian number, e.g. 08012345678 or +2348012345678"
            placeholder="08012345678"
            value={form.phoneNumber}
            onChange={update("phoneNumber")}
            {...fieldProps("phoneNumber")}
          />
          {fieldError("phoneNumber")}
        </div>
        <div className="field">
          <label htmlFor="password">Password</label>
          <PasswordInput
            id="password"
            autoComplete="new-password"
            required
            minLength={8}
            value={form.password}
            onChange={update("password")}
            {...fieldProps("password")}
          />
          {fieldErrors.password ? fieldError("password") : <span className="field-hint">At least 8 characters</span>}
        </div>
        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Creating account…" : "Create account"}
        </button>
      </form>
    </AuthLayout>
  );
}
