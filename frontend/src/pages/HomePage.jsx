import Advantage from "../components/home/Advantage";
import Availability from "../components/home/Availability";
import ExperienceSection from "../components/home/ExperienceSection";
import FeaturedRooms from "../components/home/FeaturedRooms";
import FestiveBanner from "../components/home/FestiveBanner";
import Hero from "../components/home/Hero";
import Highlights from "../components/home/Highlights";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "./HomePage.css";

export default function HomePage() {
  useDocumentTitle();

  return (
    <>
      <Hero />
      <Availability />
      <Highlights />
      <FeaturedRooms />
      <ExperienceSection />
      <Advantage />
      <FestiveBanner />
    </>
  );
}
