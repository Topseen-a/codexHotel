import Icon from "./Icon";

export default function Stars({ rating = 5 }) {
  return (
    <span className="stars" aria-label={`${rating} out of 5 stars`}>
      {Array.from({ length: 5 }, (_, i) => (
        <Icon key={i} name="star" size={14} filled={i < rating} strokeWidth={1.2} />
      ))}
    </span>
  );
}
