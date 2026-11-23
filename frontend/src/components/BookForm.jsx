import { useState } from "react";

const emptyForm = { title: "", author: "", isbn: "", category: "", totalCopies: 1 };

// Used for both "Add book" and "Edit book"
export default function BookForm({ initial, onSubmit, onCancel, saving }) {
  const [form, setForm] = useState(initial ?? emptyForm);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ ...form, totalCopies: Number(form.totalCopies) });
  };

  return (
    <form className="book-form" onSubmit={handleSubmit}>
      <h2>{initial ? "Edit book" : "Add a new book"}</h2>

      <div className="form-grid">
        <div>
          <label>Title</label>
          <input name="title" value={form.title} onChange={handleChange} />
        </div>
        <div>
          <label>Author</label>
          <input name="author" value={form.author} onChange={handleChange} />
        </div>
        <div>
          <label>ISBN</label>
          <input name="isbn" value={form.isbn} onChange={handleChange} />
        </div>
        <div>
          <label>Category</label>
          <input name="category" value={form.category} onChange={handleChange} />
        </div>
        <div>
          <label>Total copies</label>
          <input
            name="totalCopies"
            type="number"
            min="1"
            value={form.totalCopies}
            onChange={handleChange}
          />
        </div>
      </div>

      <div className="actions">
        <button type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save"}
        </button>
        <button type="button" className="secondary" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
}