import { Link } from "react-router-dom";
import Brand from "../../components/layout/Brand";
import Icon from "../../components/ui/Icon";
import { IMAGES } from "../../content/images";
import "./AuthPages.css";

export default function AuthLayout({ title, subtitle, children, footer }) {
  return (
    <div className="auth">
      <aside className="auth-visual" style={{ backgroundImage: `url(${IMAGES.auth})` }}>
        <Brand className="brand--light" />
        <div className="auth-visual-copy">
          <p className="script">More than a stay,</p>
          <h2>it&apos;s an experience worth coming back to.</h2>
        </div>
      </aside>

      <section className="auth-panel">
        <div className="auth-panel-top">
          <Brand className="auth-mobile-brand" />
          <Link to="/" className="back-link">
            <Icon name="arrowLeft" size={16} /> Back to site
          </Link>
        </div>
        <div className="auth-form-wrap">
          <h1>{title}</h1>
          <p className="muted">{subtitle}</p>
          {children}
          {footer && <p className="auth-footer">{footer}</p>}
        </div>
      </section>
    </div>
  );
}
