import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

export default function Books() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  const [books, setBooks] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadBooks = async (kw = "") => {
    setLoading(true);
    setError("");
    try {
      const search = kw.trim();
      const { data } = search
        ? await api.get("/books/search", { params: { keyword: search } })
        : await api.get("/books");
      setBooks(data);
    } catch {
      setError("Could not load books.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBooks();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    loadBooks(keyword);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <div className="page">
      <header className="topbar">
        <h1>Library Books</h1>
        <div className="user-box">
          <span>
            {user?.name} ({user?.role})
          </span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </header>

      <form className="search" onSubmit={handleSearch}>
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="Search by title or author"
        />
        <button type="submit">Search</button>
        <button
          type="button"
          onClick={() => {
            setKeyword("");
            loadBooks("");
          }}
        >
          Clear
        </button>
      </form>

      {error && <p className="error">{error}</p>}
      {loading && <p>Loading...</p>}

      {!loading && !error && books.length === 0 && <p>No books found.</p>}

      {!loading && books.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Author</th>
              <th>ISBN</th>
              <th>Category</th>
              <th>Available</th>
            </tr>
          </thead>
          <tbody>
            {books.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.title}</td>
                <td>{b.author}</td>
                <td>{b.isbn}</td>
                <td>{b.category}</td>
                <td>
                  {b.availableCopies} / {b.totalCopies}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}