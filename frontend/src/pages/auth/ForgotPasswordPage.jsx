import { useState } from "react";
import { Link } from "react-router-dom";
import { forgotPassword } from "../../api/auth";
import Alert from "../../components/ui/Alert";
import Icon from "../../components/ui/Icon";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import AuthLayout from "./AuthLayout";

export default function ForgotPasswordPage() {
  useDocumentTitle("Forgot password");
  const [email, setEmail] = useState("");
  const [sentTo, setSentTo] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await forgotPassword(email.trim());
      setSentTo(email.trim());
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  if (sentTo) {
    return (
      <AuthLayout
        title="Check your email"
        subtitle={`If an account exists for ${sentTo}, we've sent a link to reset your password.`}
        footer={
          <>
            Remembered it? <Link to="/login">Back to sign in</Link>
          </>
        }
      >
        <div className="auth-form">
          <div className="auth-notice">
            <span className="auth-notice-icon">
              <Icon name="mail" size={22} />
            </span>
            <p className="muted">
              The link expires in 30 minutes and can only be used once. Don&apos;t see it? Check your spam folder, or
              request a new link.
            </p>
          </div>
          <button type="button" className="btn btn-outline btn-block" onClick={() => setSentTo("")}>
            Send another link
          </button>
        </div>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout
      title="Forgot your password?"
      subtitle="Enter the email you signed up with and we'll send you a link to choose a new one."
      footer={
        <>
          Remembered it? <Link to="/login">Back to sign in</Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={submit}>
        <Alert tone="danger">{error}</Alert>
        <div className="field">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Sending…" : "Send reset link"}
        </button>
      </form>
    </AuthLayout>
  );
}
