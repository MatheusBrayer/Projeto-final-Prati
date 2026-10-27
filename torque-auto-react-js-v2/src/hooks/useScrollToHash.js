import { useEffect } from "react";
import { useLocation } from "react-router-dom";

// TanStack Router scrolla sozinho até a âncora; com React Router isso é manual.
export function useScrollToHash() {
  const { pathname, hash } = useLocation();

  useEffect(() => {
    if (hash) {
      const target = document.querySelector(hash);
      if (target) target.scrollIntoView({ behavior: "smooth", block: "start" });
    } else {
      window.scrollTo(0, 0);
    }
  }, [pathname, hash]);
}
