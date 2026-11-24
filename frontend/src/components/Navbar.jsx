import { NavLink, useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "null");
  const role = user?.role;
  const isStaff = role === "ADMIN" || role === "LIBRARIAN";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <header className="topbar">
      <div className="nav-left">
        <h1>Library</h1>
        <nav className="nav-links">
          <NavLink to="/books">Books</NavLink>
          {role === "MEMBER" && <NavLink to="/my-issues">My Issues</NavLink>}
          {isStaff && <NavLink to="/manage-issues">Manage Issues</NavLink>}
        </nav>
      </div>
      <div className="user-box">
        <span>
          {user?.name} ({role})
        </span>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </header>
  );
}