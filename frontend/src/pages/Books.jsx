import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";
import BookForm from "../components/BookForm";
import Navbar from "../components/Navbar";

export default function Books() {
  const user = JSON.parse(localStorage.getItem("user") || "null");
  const role = user?.role;
  const isStaff = role === "ADMIN" || role === "LIBRARIAN";
  const isAdmin = role === "ADMIN";
  const isMember = role === "MEMBER";

  const [books, setBooks] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);

  const loadBooks = async (kw = "") => {
    setLoading(true);
    try {
      const search = kw.trim();
      const { data } = search
        ? await api.get("/books/search", { params: { keyword: search } })
        : await api.get("/books");
      setBooks(data);
    } catch (err) {
      setError(errorMessage(err, "Could not load books."));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBooks();
  }, []);

  const clearMessages = () => {
    setError("");
    setSuccess("");
  };

  const handleSearch = (e) => {
    e.preventDefault();
    clearMessages();
    loadBooks(keyword);
  };

  // MEMBER: borrow a book for themselves
  const handleIssue = async (book) => {
    clearMessages();
    try {
      const { data } = await api.post("/issues", { bookId: book.id });
      const due = data?.dueDate ? ` Due date: ${data.dueDate}.` : "";
      setSuccess(`Issued "${book.title}".${due}`);
      loadBooks(keyword);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  // LIBRARIAN / ADMIN: add or edit a book
  const handleSave = async (values) => {
    clearMessages();
    setSaving(true);
    try {
      if (editing) {
        await api.put(`/books/${editing.id}`, values);
        setSuccess("Book updated.");
      } else {
        await api.post("/books", values);
        setSuccess("Book added.");
      }
      closeForm();
      loadBooks(keyword);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  // ADMIN only: delete a book
  const handleDelete = async (book) => {
    if (!window.confirm(`Delete "${book.title}"?`)) return;
    clearMessages();
    try {
      await api.delete(`/books/${book.id}`);
      setSuccess("Book deleted.");
      loadBooks(keyword);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const openAdd = () => {
    clearMessages();
    setEditing(null);
    setShowForm(true);
  };

  const openEdit = (book) => {
    clearMessages();
    setEditing(book);
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditing(null);
  };

  return (
    <div className="page">
      <Navbar />
      <h2 className="page-title">Books</h2>

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
            clearMessages();
            loadBooks("");
          }}
        >
          Clear
        </button>
        {isStaff && (
          <button type="button" className="success" onClick={openAdd}>
            + Add Book
          </button>
        )}
      </form>

      {showForm && (
        <BookForm
          key={editing?.id ?? "new"}
          initial={
            editing
              ? {
                  title: editing.title,
                  author: editing.author,
                  isbn: editing.isbn,
                  category: editing.category,
                  totalCopies: editing.totalCopies,
                }
              : undefined
          }
          saving={saving}
          onSubmit={handleSave}
          onCancel={closeForm}
        />
      )}

      {error && <p className="error">{error}</p>}
      {success && <p className="ok">{success}</p>}
      {loading && <p>Loading...</p>}

      {!loading && books.length === 0 && <p>No books found.</p>}

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
              <th>Actions</th>
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
                <td>
                  <div className="actions">
                    {isMember && (
                      <button
                        disabled={b.availableCopies <= 0}
                        onClick={() => handleIssue(b)}
                      >
                        Issue
                      </button>
                    )}
                    {isStaff && (
                      <button className="secondary" onClick={() => openEdit(b)}>
                        Edit
                      </button>
                    )}
                    {isAdmin && (
                      <button className="danger" onClick={() => handleDelete(b)}>
                        Delete
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}