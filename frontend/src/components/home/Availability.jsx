import SearchBar from "./SearchBar";

/** Availability search, directly under the hero. */
export default function Availability() {
  return (
    <section className="availability" id="availability" aria-labelledby="availability-title">
      <div className="container availability-inner">
        <div className="availability-heading">
          <span className="eyebrow">Plan your stay</span>
          <h2 id="availability-title">Check availability</h2>
        </div>
        <SearchBar />
      </div>
    </section>
  );
}
