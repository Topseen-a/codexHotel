import { useMemo } from "react";
import { Link } from "react-router-dom";
import { backgroundFor, IMAGES } from "../../content/images";
import { useCountdown } from "../../hooks/useCountdown";

// December nights are priced at the backend's FESTIVE rate. Count down to the
// start of the season (or, during December, to its end).
function festiveWindow(now = new Date()) {
  const inSeason = now.getMonth() === 11;
  return {
    inSeason,
    target: inSeason ? new Date(now.getFullYear() + 1, 0, 1) : new Date(now.getFullYear(), 11, 1),
  };
}

export default function FestiveBanner() {
  const { inSeason, target } = useMemo(() => festiveWindow(), []);
  const time = useCountdown(target);

  const units = [
    { label: "Days", value: time.days },
    { label: "Hours", value: time.hours },
    { label: "Minutes", value: time.minutes },
    { label: "Seconds", value: time.seconds },
  ];

  return (
    <section className="festive" style={{ backgroundImage: `url(${backgroundFor(IMAGES.promo)})` }}>
      <div className="container festive-inner">
        <div className="festive-copy">
          <span className="eyebrow">{inSeason ? "Festive season" : "Plan ahead"}</span>
          <h2>{inSeason ? "The Festive Season Is Here" : "Book Early for the Festive Season"}</h2>
          <p>
            December stays are our most sought-after. Your nightly rate is fixed the moment you book, so secure your
            dates while rooms are still open.
          </p>
          <Link to="/rooms" className="btn btn-light">
            Book Now
          </Link>
        </div>

        <div className="festive-countdown" role="timer" aria-label={inSeason ? "Festive season ends in" : "Festive season starts in"}>
          <span className="festive-countdown-label">{inSeason ? "Season ends in" : "Season starts in"}</span>
          <div className="festive-units">
            {units.map((unit) => (
              <div key={unit.label} className="festive-unit">
                <strong>{String(unit.value).padStart(2, "0")}</strong>
                <span>{unit.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
