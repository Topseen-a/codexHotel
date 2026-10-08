import { Link } from "react-router-dom";
import { BRAND_WORDMARK, SITE } from "../../config/site";

/** The emblem: a doorway arch framing the sun rising over water. */
export function BrandMark({ className = "" }) {
  return (
    <svg
      className={`brand-mark ${className}`}
      viewBox="0 0 48 48"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M10 42V21a14 14 0 0 1 28 0v21" />
      <path d="M16.5 32a7.5 7.5 0 0 1 15 0" />
      <path d="M13 32h22" />
      <path d="M17 36h14M20 39.5h8" />
      <path d="M5 42h38" />
    </svg>
  );
}

/**
 * `variant="mark"` renders just the emblem (navbar); the default adds the
 * wordmark (footer, auth pages).
 */
export default function Brand({ className = "", variant = "full" }) {
  if (variant === "mark") {
    return (
      <Link to="/" className={`brand brand--mark ${className}`} aria-label={`${SITE.name} home`}>
        <BrandMark />
      </Link>
    );
  }

  return (
    <Link to="/" className={`brand ${className}`} aria-label={`${SITE.name} home`}>
      <BrandMark />
      <span className="brand-text">
        <span className="brand-name">{BRAND_WORDMARK}</span>
        <span className="brand-sub">{SITE.subtitle}</span>
      </span>
    </Link>
  );
}
