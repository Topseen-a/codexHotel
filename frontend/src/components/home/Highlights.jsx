import { HIGHLIGHTS } from "../../content/experiences";
import Icon from "../ui/Icon";

export default function Highlights() {
  return (
    <section className="highlights" aria-label="Why stay with us">
      <div className="container highlights-grid">
        {HIGHLIGHTS.map((item) => (
          <div key={item.title} className="highlight">
            <span className="highlight-icon">
              <Icon name={item.icon} size={24} strokeWidth={1.5} />
            </span>
            <div>
              <h3>{item.title}</h3>
              <p>{item.text}</p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
