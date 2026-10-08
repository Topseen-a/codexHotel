import { useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import { BRAND_WORDMARK, SITE } from "../../config/site";
import { IMAGES } from "../../content/images";

/** Split hero: a dark editorial panel on the left, a tall photograph on the right. */
export default function Hero() {
  const photoRef = useRef(null);

  // Gentle parallax: the photo drifts slower than the page while the hero is visible.
  useEffect(() => {
    const photo = photoRef.current;
    const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (!photo || reducedMotion) return undefined;

    let frame = 0;
    const update = () => {
      frame = 0;
      const y = Math.min(window.scrollY, window.innerHeight);
      photo.style.setProperty("--hero-shift", `${y * 0.12}px`);
    };
    const onScroll = () => {
      if (!frame) frame = requestAnimationFrame(update);
    };
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => {
      window.removeEventListener("scroll", onScroll);
      cancelAnimationFrame(frame);
    };
  }, []);

  return (
    <section className="hero">
      <div className="hero-panel">
        <div className="hero-thumbs" aria-hidden="true">
          {IMAGES.heroThumbs.map((src) => (
            <img key={src} src={src} alt="" />
          ))}
        </div>

        <h1>
          {BRAND_WORDMARK}
          <br />
          {SITE.city}
        </h1>
        <p className="hero-lead">Your sanctuary on the {SITE.location.split(",")[0]}</p>

        <Link to="/rooms" className="btn btn-primary hero-cta">
          Book your stay
        </Link>

        <a href="#availability" className="hero-scroll">
          Scroll down
        </a>
      </div>

      <div className="hero-photo" ref={photoRef}>
        <div className="hero-photo-inner">
          <img src={IMAGES.heroTall} alt="Warm wood-panelled suite opening onto a tropical garden" fetchPriority="high" />
        </div>
      </div>
    </section>
  );
}
