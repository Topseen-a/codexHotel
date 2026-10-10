import { Link } from "react-router-dom";
import Icon from "../components/ui/Icon";
import Photo from "../components/ui/Photo";
import { SITE } from "../config/site";
import { EXPERIENCES } from "../content/experiences";
import { backgroundFor, IMAGES } from "../content/images";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "./InfoPages.css";

export default function ExperiencesPage() {
  useDocumentTitle("Experiences");

  return (
    <>
      <section className="page-hero" style={{ backgroundImage: `url(${backgroundFor(IMAGES.experiences)})` }}>
        <div className="container">
          <span className="eyebrow">Experiences</span>
          <h1>More Than a Stay</h1>
          <p>Wellness, food, adventure and culture — everything that makes a stay at {SITE.name} unforgettable.</p>
        </div>
      </section>

      <nav className="container experience-index" aria-label="Experiences on this page">
        {EXPERIENCES.map((experience) => (
          <a key={experience.slug} href={`#${experience.slug}`} className="chip">
            {experience.title}
          </a>
        ))}
      </nav>

      <div className="container experience-features">
        {EXPERIENCES.map((experience, index) => (
          <article key={experience.slug} id={experience.slug} className={`experience-feature ${index % 2 ? "reverse" : ""}`}>
            <div className="experience-feature-photo">
              <Photo src={experience.imageLarge} alt={experience.title} sizes="(max-width: 860px) 100vw, 55vw" />
            </div>
            <div className="experience-feature-copy">
              <span className="eyebrow">
                {String(index + 1).padStart(2, "0")} · {experience.description}
              </span>
              <h2>{experience.title}</h2>
              <p className="muted">{experience.intro}</p>
              <ul>
                {experience.highlights.map((item) => (
                  <li key={item}>
                    <Icon name="check" size={16} /> {item}
                  </li>
                ))}
              </ul>
              <Link to="/contact" className="link">
                Ask our concierge <Icon name="arrowRight" size={14} />
              </Link>
            </div>
          </article>
        ))}
      </div>

      <section className="container section">
        <div className="contact-help card card-pad">
          <div>
            <h2>Ready to experience it yourself?</h2>
            <p className="muted">Book your stay and our team will help plan the rest.</p>
          </div>
          <Link to="/rooms" className="btn btn-primary">
            Book your stay <Icon name="arrowRight" size={16} />
          </Link>
        </div>
      </section>
    </>
  );
}
