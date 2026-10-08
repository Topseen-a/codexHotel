import { Link } from "react-router-dom";
import { SITE } from "../../config/site";
import { EXPERIENCES } from "../../content/experiences";
import { IMAGES } from "../../content/images";
import Icon from "../ui/Icon";

export default function ExperienceSection() {
  return (
    <section className="experience" id="experiences">
      <div className="experience-spotlight" style={{ backgroundImage: `url(${IMAGES.beachResort})` }}>
        <div className="experience-spotlight-content">
          <span className="eyebrow">Top destination</span>
          <h2>Experience Paradise at Our Beach Resort</h2>
          <p>Wake up to ocean views, enjoy a private beach and indulge in unforgettable moments.</p>
          <span className="experience-location">
            <Icon name="mapPin" size={16} /> {SITE.location}
          </span>
          <Link to="/gallery" className="btn btn-light">
            Explore Resort <Icon name="arrowRight" size={16} />
          </Link>
        </div>
      </div>

      <div className="experience-list">
        <div className="section-heading">
          <h2>Hotel Experiences</h2>
          <p>More than a stay — a journey of unforgettable moments.</p>
          <Link to="/experiences" className="link experience-all">
            All experiences <Icon name="arrowRight" size={14} />
          </Link>
        </div>
        <div className="experience-grid">
          {EXPERIENCES.map((experience) => (
            <Link key={experience.slug} to={`/experiences#${experience.slug}`} className="experience-card">
              <div className="experience-card-image">
                <img src={experience.image} alt="" loading="lazy" />
              </div>
              <h3>{experience.title}</h3>
              <p>{experience.description}</p>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}
