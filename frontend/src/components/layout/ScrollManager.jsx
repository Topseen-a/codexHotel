import { useEffect } from "react";
import { useLocation } from "react-router-dom";

/** Scrolls to the top on navigation, or to the #anchor when the URL has one. */
export default function ScrollManager() {
  const { pathname, hash } = useLocation();

  useEffect(() => {
    if (hash) {
      // Wait a frame so the target section has rendered.
      const id = requestAnimationFrame(() => {
        document.getElementById(hash.slice(1))?.scrollIntoView({ behavior: "smooth", block: "start" });
      });
      return () => cancelAnimationFrame(id);
    }
    window.scrollTo({ top: 0, behavior: "instant" });
    return undefined;
  }, [pathname, hash]);

  return null;
}
