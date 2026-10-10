import { useState } from "react";
import { backgroundFor, IMAGES } from "../content/images";
import { GALLERY, GALLERY_CATEGORIES } from "../content/gallery";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import Modal from "../components/ui/Modal";
import Photo from "../components/ui/Photo";
import "./InfoPages.css";

export default function GalleryPage() {
  useDocumentTitle("Gallery");
  const [category, setCategory] = useState("All");
  const [open, setOpen] = useState(null);

  const photos = category === "All" ? GALLERY : GALLERY.filter((photo) => photo.category === category);

  return (
    <>
      <section className="page-hero" style={{ backgroundImage: `url(${backgroundFor(IMAGES.gallery)})` }}>
        <div className="container">
          <span className="eyebrow">Gallery</span>
          <h1>A Look Around the Resort</h1>
          <p>Pools, suites, dining and the coastline on our doorstep.</p>
        </div>
      </section>

      <div className="container section gallery">
        <div className="chip-group" role="tablist" aria-label="Filter photos">
          {GALLERY_CATEGORIES.map((name) => (
            <button
              key={name}
              type="button"
              role="tab"
              aria-selected={category === name}
              className={`chip ${category === name ? "active" : ""}`}
              onClick={() => setCategory(name)}
            >
              {name}
            </button>
          ))}
        </div>

        <div className="gallery-grid">
          {photos.map((photo) => (
            <button
              key={photo.id}
              type="button"
              className={`gallery-item ${photo.wide ? "wide" : ""} ${photo.tall ? "tall" : ""}`}
              onClick={() => setOpen(photo)}
              aria-label={`Open photo: ${photo.alt}`}
            >
              <Photo
                src={photo.src}
                alt={photo.alt}
                sizes={photo.wide ? "(max-width: 900px) 100vw, 50vw" : "(max-width: 600px) 50vw, (max-width: 900px) 33vw, 25vw"}
              />
              <span>{photo.category}</span>
            </button>
          ))}
        </div>
      </div>

      <Modal open={Boolean(open)} title={open?.alt || ""} onClose={() => setOpen(null)} size="lg">
        {open && <img className="gallery-full" src={open.src.replace(/w=\d+/, "w=1800")} alt={open.alt} />}
      </Modal>
    </>
  );
}
