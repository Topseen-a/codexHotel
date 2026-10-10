import { srcSetFor } from "../../content/images";

/**
 * Responsive photo: offers the browser several widths (srcset) and how wide the
 * image will be shown (sizes), so phones download small files. `priority` is for
 * the main above-the-fold image; everything else lazy-loads.
 */
export default function Photo({ src, sizes, alt = "", priority = false, className, ...rest }) {
  return (
    <img
      src={src}
      srcSet={srcSetFor(src)}
      sizes={sizes}
      alt={alt}
      loading={priority ? "eager" : "lazy"}
      decoding="async"
      fetchPriority={priority ? "high" : undefined}
      className={className}
      {...rest}
    />
  );
}
