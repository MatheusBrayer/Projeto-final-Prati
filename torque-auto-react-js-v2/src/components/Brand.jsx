import { Link } from "react-router-dom";

export function Brand() {
  return (
    <Link to="/" hash="inicio" className="brand" aria-label="Torque Auto — início">
      <span className="brand-symbol">T</span>
      <span>
        Torque<span className="text-primary">Auto</span>
      </span>
    </Link>
  );
}
