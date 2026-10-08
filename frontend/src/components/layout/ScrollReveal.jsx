import { useEffect } from "react";
import { useLocation } from "react-router-dom";

// Elements that fade up into view as they scroll in. Kept in one list so pages
// don't need to opt in individually. The staff dashboard is excluded.
const REVEAL_SELECTORS = [
  ".availability-inner > *",
  ".highlight",
  ".section-heading",
  ".room-card",
  ".experience-spotlight-content",
  ".experience-card",
  ".advantage-item",
  ".advantage-photo",
  ".testimonial",
  ".festive-copy",
  ".festive-countdown",
  ".page-hero .container",
  ".rooms-filter",
  ".room-row",
  ".rates",
  ".room-gallery",
  ".room-facts li",
  ".room-detail-info section",
  ".gallery-item",
  ".about-story > *",
  ".info-tile",
  ".about-experience",
  ".experience-feature-photo",
  ".experience-feature-copy",
  ".contact-help",
  ".booking-card",
  ".footer-top > *",
].join(",");

const STAGGER_MS = 90;
const MAX_STAGGER_STEPS = 6;

/** Adds scroll-triggered reveal animations to matching elements on every page. */
export default function ScrollReveal() {
  const { pathname } = useLocation();

  useEffect(() => {
    const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (reducedMotion || !("IntersectionObserver" in window)) return undefined;

    // Content is only hidden once this runs, so nothing disappears without JS.
    document.documentElement.classList.add("reveal-ready");
    const seen = new WeakSet();

    const observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (!entry.isIntersecting) continue;
          const el = entry.target;
          observer.unobserve(el);
          el.classList.add("is-revealed");
          // Once settled, drop the reveal styles so hover transforms work normally.
          // (transitionend bubbles from children, so check the target.)
          const settle = (event) => {
            if (event.target !== el || event.propertyName !== "transform") return;
            el.removeEventListener("transitionend", settle);
            el.removeAttribute("data-reveal");
            el.classList.remove("is-revealed");
            el.style.removeProperty("--reveal-delay");
          };
          el.addEventListener("transitionend", settle);
        }
      },
      { rootMargin: "0px 0px -8% 0px", threshold: 0.12 }
    );

    const tag = (root) => {
      root.querySelectorAll(REVEAL_SELECTORS).forEach((el) => {
        if (seen.has(el) || el.closest(".admin")) return;
        seen.add(el);
        const siblings = [...el.parentElement.children].filter((child) => child.matches(REVEAL_SELECTORS));
        const step = Math.min(Math.max(siblings.indexOf(el), 0), MAX_STAGGER_STEPS);
        el.style.setProperty("--reveal-delay", `${step * STAGGER_MS}ms`);
        el.setAttribute("data-reveal", "");
        observer.observe(el);
      });
    };

    tag(document);

    // Pick up content that renders later (data loading, tab switches).
    const mutations = new MutationObserver((records) => {
      for (const record of records) {
        record.addedNodes.forEach((node) => node.nodeType === 1 && tag(node.parentElement || node));
      }
    });
    mutations.observe(document.body, { childList: true, subtree: true });

    return () => {
      observer.disconnect();
      mutations.disconnect();
    };
  }, [pathname]);

  return null;
}
