import { useEffect, useState } from "react";
import { calculatePrice } from "../api/pricing";
import { stayNights } from "../utils/format";

const MAX_QUOTED_NIGHTS = 31;

/**
 * Prices each night of a stay with the backend's seasonal pricing
 * (/api/pricing/calculate), so the estimate matches what booking will charge.
 */
export function useStayQuote({ roomType, basePrice, checkIn, checkOut }) {
  const nights = stayNights(checkIn, checkOut);
  const quotable = Boolean(roomType) && basePrice != null && nights.length > 0 && nights.length <= MAX_QUOTED_NIGHTS;
  const key = quotable ? `${roomType}|${basePrice}|${checkIn}|${checkOut}` : null;
  const [result, setResult] = useState({ key: null });

  useEffect(() => {
    if (!key) return undefined;
    let active = true;

    // Debounce so picking dates doesn't fire a burst of requests.
    const timer = setTimeout(() => {
      Promise.all(nights.map((date) => calculatePrice({ roomType, basePrice, date })))
        .then((prices) => {
          if (!active) return;
          const priced = nights.map((date, i) => ({ date, price: Number(prices[i]) }));
          setResult({ key, status: "ready", nights: priced, total: priced.reduce((sum, n) => sum + n.price, 0) });
        })
        .catch((error) => active && setResult({ key, status: "error", nights: [], total: 0, error }));
    }, 250);

    return () => {
      active = false;
      clearTimeout(timer);
    };
    // `key` captures every input that affects the quote.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key]);

  if (nights.length === 0 || basePrice == null) return { status: "idle", nights: [], total: 0 };
  if (nights.length > MAX_QUOTED_NIGHTS) {
    return { status: "too-long", nights: [], total: Number(basePrice) * nights.length };
  }
  return result.key === key ? result : { status: "loading", nights: [], total: 0 };
}

/** Groups nights that share a price: [{ price, count }], cheapest first. */
export function groupNightsByPrice(nights) {
  const groups = new Map();
  for (const night of nights) {
    groups.set(night.price, (groups.get(night.price) || 0) + 1);
  }
  return [...groups].map(([price, count]) => ({ price, count })).sort((a, b) => a.price - b.price);
}
