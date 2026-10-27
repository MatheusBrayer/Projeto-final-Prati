import { Routes, Route } from "react-router-dom";
import { useScrollToHash } from "./hooks/useScrollToHash.js";
import Home from "./pages/Home.jsx";
import Planos from "./pages/Planos.jsx";
import NotFound from "./pages/NotFound.jsx";

export default function App() {
  useScrollToHash();

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/planos" element={<Planos />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
