import { useState } from "react";
import Alert from "./Alert";
import Modal from "./Modal";

/** Confirmation modal whose confirm action may be async; shows its error inline. */
export default function ConfirmDialog({
  open,
  title,
  children,
  confirmLabel = "Confirm",
  tone = "danger",
  onConfirm,
  onClose,
}) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  const close = () => {
    if (busy) return;
    setError("");
    onClose();
  };

  const confirm = async () => {
    setBusy(true);
    setError("");
    try {
      await onConfirm();
      onClose();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal
      open={open}
      title={title}
      onClose={close}
      size="sm"
      footer={
        <>
          <button type="button" className="btn btn-outline" onClick={close} disabled={busy}>
            Go back
          </button>
          <button
            type="button"
            className={`btn ${tone === "danger" ? "btn-danger" : "btn-primary"}`}
            onClick={confirm}
            disabled={busy}
          >
            {busy ? "Working…" : confirmLabel}
          </button>
        </>
      }
    >
      <div className="stack">
        <div className="muted">{children}</div>
        <Alert tone="danger">{error}</Alert>
      </div>
    </Modal>
  );
}
