import { useState } from "react";
import api, { errorMessage } from "../api";
import Navbar from "../components/Navbar";

export default function ManageIssues() {
  const [memberId, setMemberId] = useState("");
  const [issues, setIssues] = useState([]);
  const [loadedFor, setLoadedFor] = useState(null);
  const [loading, setLoading] = useState(false);

  const [bookIdToIssue, setBookIdToIssue] = useState("");
  const [memberIdToIssue, setMemberIdToIssue] = useState("");

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const clearMessages = () => {
    setError("");
    setSuccess("");
  };

  const loadIssues = async (id) => {
    setLoading(true);
    try {
      const { data } = await api.get(`/issues/user/${id}`);
      setIssues([...data].sort((a, b) => b.id - a.id));
      setLoadedFor(id);
    } catch (err) {
      setError(errorMessage(err, "Could not load issues."));
    } finally {
      setLoading(false);
    }
  };

  const handleFind = (e) => {
    e.preventDefault();
    clearMessages();
    if (!memberId.trim()) {
      setError("Enter a member ID.");
      return;
    }
    loadIssues(memberId.trim());
  };

  const handleReturn = async (issue) => {
    clearMessages();
    try {
      const { data } = await api.put(`/issues/${issue.id}/return`);
      setSuccess(`Returned "${data.bookTitle}". Fine: ₹${Number(data.fineAmount).toFixed(2)}`);
      loadIssues(loadedFor);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const handleIssue = async (e) => {
    e.preventDefault();
    clearMessages();
    if (!bookIdToIssue.trim() || !memberIdToIssue.trim()) {
      setError("Enter both book ID and member ID.");
      return;
    }
    try {
      const { data } = await api.post("/issues", {
        bookId: Number(bookIdToIssue),
        userId: Number(memberIdToIssue),
      });
      setSuccess(`Issued "${data.bookTitle}" to ${data.userName}. Due date: ${data.dueDate}.`);
      if (String(loadedFor) === memberIdToIssue.trim()) {
        loadIssues(loadedFor);
      }
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="page">
      <Navbar />
      <h2 className="page-title">Manage Issues</h2>

      {error && <p className="error">{error}</p>}
      {success && <p className="ok">{success}</p>}

      <div className="panel">
        <h2>Issue a book to a member</h2>
        <form className="inline-form" onSubmit={handleIssue}>
          <div>
            <label>Book ID</label>
            <input
              type="number"
              min="1"
              value={bookIdToIssue}
              onChange={(e) => setBookIdToIssue(e.target.value)}
            />
          </div>
          <div>
            <label>Member ID</label>
            <input
              type="number"
              min="1"
              value={memberIdToIssue}
              onChange={(e) => setMemberIdToIssue(e.target.value)}
            />
          </div>
          <button type="submit">Issue</button>
        </form>
      </div>

      <div className="panel">
        <h2>Find a member's issues</h2>
        <form className="inline-form" onSubmit={handleFind}>
          <div>
            <label>Member ID</label>
            <input
              type="number"
              min="1"
              value={memberId}
              onChange={(e) => setMemberId(e.target.value)}
            />
          </div>
          <button type="submit">Find</button>
        </form>
      </div>

      {loading && <p>Loading...</p>}

      {!loading && loadedFor !== null && issues.length === 0 && (
        <p>No issues found for member {loadedFor}.</p>
      )}

      {!loading && issues.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Issue ID</th>
              <th>Book</th>
              <th>Member</th>
              <th>Due date</th>
              <th>Returned on</th>
              <th>Fine</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {issues.map((i) => (
              <tr key={i.id}>
                <td>{i.id}</td>
                <td>{i.bookTitle}</td>
                <td>{i.userName}</td>
                <td>{i.dueDate}</td>
                <td>{i.returnDate ?? "—"}</td>
                <td>{i.status === "RETURNED" ? `₹${Number(i.fineAmount).toFixed(2)}` : "—"}</td>
                <td>
                  <span className={`badge ${i.status === "RETURNED" ? "returned" : "issued"}`}>
                    {i.status === "RETURNED" ? "Returned" : "Issued"}
                  </span>
                </td>
                <td>
                  {i.status === "ISSUED" && (
                    <button onClick={() => handleReturn(i)}>Return</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}