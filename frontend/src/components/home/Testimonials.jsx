import { useState } from "react";
import { TESTIMONIALS } from "../../content/testimonials";
import Icon from "../ui/Icon";
import Stars from "../ui/Stars";

export default function Testimonials() {
  const [index, setIndex] = useState(0);
  const review = TESTIMONIALS[index];
  const step = (delta) => setIndex((i) => (i + delta + TESTIMONIALS.length) % TESTIMONIALS.length);

  return (
    <figure className="testimonial card">
      <span className="testimonial-mark" aria-hidden="true">
        &ldquo;
      </span>
      <blockquote key={index}>{review.quote}</blockquote>
      <figcaption className="testimonial-author">
        <img src={review.avatar} alt="" loading="lazy" />
        <div>
          <strong>{review.name}</strong>
          <span>{review.origin}</span>
          <Stars rating={review.rating} />
        </div>
      </figcaption>
      <div className="testimonial-controls">
        <div className="cluster" style={{ "--cluster-gap": "6px" }}>
          <button type="button" className="btn btn-ghost btn-icon btn-sm" onClick={() => step(-1)} aria-label="Previous review">
            <Icon name="arrowLeft" size={16} />
          </button>
          <button type="button" className="btn btn-ghost btn-icon btn-sm" onClick={() => step(1)} aria-label="Next review">
            <Icon name="arrowRight" size={16} />
          </button>
        </div>
        <span className="faint" aria-live="polite">
          {index + 1} / {TESTIMONIALS.length}
        </span>
      </div>
    </figure>
  );
}
