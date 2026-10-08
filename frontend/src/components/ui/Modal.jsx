import { useEffect, useId, useRef } from "react";
import { createPortal } from "react-dom";
import Icon from "./Icon";
import "./Modal.css";

export default function Modal({ open, title, onClose, children, footer, size = "md" }) {
  const titleId = useId();
  const panelRef = useRef(null);
  // Parents usually pass a fresh onClose each render; read it through a ref so
  // the open/close effect (focus handling, scroll lock) only runs on toggle.
  const onCloseRef = useRef(onClose);
  useEffect(() => {
    onCloseRef.current = onClose;
  });

  useEffect(() => {
    if (!open) return undefined;
    const previouslyFocused = document.activeElement;
    const onKey = (event) => event.key === "Escape" && onCloseRef.current();

    document.addEventListener("keydown", onKey);
    document.body.classList.add("no-scroll");
    panelRef.current?.querySelector("input, select, textarea, button:not(.modal-close)")?.focus();

    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.classList.remove("no-scroll");
      previouslyFocused?.focus?.();
    };
  }, [open]);

  if (!open) return null;

  return createPortal(
    <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <div ref={panelRef} className={`modal modal-${size}`} role="dialog" aria-modal="true" aria-labelledby={titleId}>
        <header className="modal-header">
          <h2 id={titleId}>{title}</h2>
          <button type="button" className="btn btn-ghost btn-icon btn-sm modal-close" onClick={onClose} aria-label="Close">
            <Icon name="close" size={18} />
          </button>
        </header>
        <div className="modal-body">{children}</div>
        {footer && <footer className="modal-footer">{footer}</footer>}
      </div>
    </div>,
    document.body
  );
}
