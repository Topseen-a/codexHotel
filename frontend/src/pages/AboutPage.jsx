import { Link } from "react-router-dom";
import Icon from "../components/ui/Icon";
import { SITE } from "../config/site";
import { ADVANTAGES, EXPERIENCES } from "../content/experiences";
import { IMAGES } from "../content/images";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "./InfoPages.css";

export default function AboutPage() {
  useDocumentTitle("About");

  return (
    <>
      <section className="page-hero" style={{ backgroundImage: `url(${IMAGES.about})` }}>
        <div className="container">
          <span className="eyebrow">About us</span>
          <h1>Hospitality, by the Sea</h1>
          <p>
            {SITE.name} is a beachfront retreat on the {SITE.location} — built for slow mornings and long sunsets.
          </p>
        </div>
      </section>

      <section className="container section about-story">
        <div className="about-story-photo">
          <img src={IMAGES.aboutStory} alt="The resort's pool and white facade" loading="lazy" />
        </div>
        <div className="stack" style={{ "--stack-gap": "18px" }}>
          <span className="eyebrow">Our story</span>
          <h2>Small enough to know your name</h2>
          <p className="muted">
            We set out to build the kind of hotel we wanted to stay in ourselves: honest prices, rooms that feel calm
            the moment you walk in, and a team that remembers how you take your coffee.
          </p>
          <p className="muted">
            Every room is booked directly with us — no middlemen — so the price you see is the price you pay, and
            you can manage, pay for or cancel your stay online at any time.
          </p>
          <Link to="/rooms" className="btn btn-primary" style={{ justifySelf: "start" }}>
            Explore rooms <Icon name="arrowRight" size={16} />
          </Link>
        </div>
      </section>

      <section className="section about-values">
        <div className="container">
          <div className="section-heading" style={{ marginBottom: 32 }}>
            <span className="eyebrow">What we promise</span>
            <h2>The {SITE.name} Advantage</h2>
          </div>
          <div className="info-grid">
            {ADVANTAGES.map((item) => (
              <div key={item.title} className="info-tile card">
                <span className="info-tile-icon">
                  <Icon name={item.icon} size={22} />
                </span>
                <h3>{item.title}</h3>
                <p className="muted">{item.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="container section">
        <div className="section-heading" style={{ marginBottom: 32 }}>
          <span className="eyebrow">During your stay</span>
          <h2>Experiences</h2>
        </div>
        <div className="about-experiences">
          {EXPERIENCES.map((experience) => (
            <article key={experience.title} className="about-experience card">
              <img src={experience.image} alt="" loading="lazy" />
              <div>
                <h3>{experience.title}</h3>
                <p className="muted">{experience.long}</p>
              </div>
            </article>
          ))}
        </div>
      </section>
    </>
  );
}
