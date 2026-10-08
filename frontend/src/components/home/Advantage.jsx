import { SITE } from "../../config/site";
import { ADVANTAGES } from "../../content/experiences";
import { IMAGES } from "../../content/images";
import Icon from "../ui/Icon";
import Testimonials from "./Testimonials";

export default function Advantage() {
  return (
    <section className="section advantage">
      <div className="container advantage-layout">
        <div className="advantage-intro">
          <div className="section-heading">
            <span className="eyebrow">Why choose us</span>
            <h2>The {SITE.name} Advantage</h2>
            <p>From exceptional service to breathtaking locations, we make sure your stay is nothing short of extraordinary.</p>
          </div>
          <div className="advantage-grid">
            {ADVANTAGES.map((item) => (
              <div key={item.title} className="advantage-item">
                <span className="advantage-icon">
                  <Icon name={item.icon} size={20} />
                </span>
                <div>
                  <h3>{item.title}</h3>
                  <p>{item.text}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="advantage-photo">
          <img src={IMAGES.advantage} alt="Infinity pool deck overlooking the sea" loading="lazy" />
          <p className="script" aria-hidden="true">
            Where comfort
            <br />
            meets adventure
          </p>
        </div>

        <Testimonials />
      </div>
    </section>
  );
}
