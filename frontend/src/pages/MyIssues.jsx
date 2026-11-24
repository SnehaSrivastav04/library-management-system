import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";
import Navbar from "../components/Navbar";

// How many days past the due date (0 if returned or not late)
function daysOverdue(issue) {
  if (issue.status !== "ISSUED") return 0;
  const [y, m, d] = issue.dueDate.split("-").map(Number);
  const due = new Date(y, m - 1, d);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.max(0, Math.round((today - due) / 86400000));
}

function StatusBadge({ issue }) {
  if (issue.status === "RETURNED") {
    return <span className="badge returned">Returned</span>;
  }
  const late = daysOverdue(issue);
  if (late > 0) {
    return <span className="badge overdue">Overdue ({late} day{late > 1 ? "s" : ""})</span>;
  }
  return <span className="badge issued">Issued</span>;
}

export default function MyIssues() {
  const [issues, setIssues] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await api.get("/issues/my");
        setIssues([...data].sort((a, b) => b.id - a.id));
      } catch (err) {
        setError(errorMessage(err, "Could not load your issues."));
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <div className="page">
      <Navbar />
      <h2 className="page-title">My Issued Books</h2>

      {error && <p className="error">{error}</p>}
      {loading && <p>Loading...</p>}
      {!loading && !error && issues.length === 0 && <p>You have not issued any books yet.</p>}

      {!loading && issues.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Book</th>
              <th>Issued on</th>
              <th>Due date</th>
              <th>Returned on</th>
              <th>Fine</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {issues.map((i) => (
              <tr key={i.id}>
                <td>{i.bookTitle}</td>
                <td>{i.issueDate}</td>
                <td>{i.dueDate}</td>
                <td>{i.returnDate ?? "—"}</td>
                <td>{i.status === "RETURNED" ? `₹${Number(i.fineAmount).toFixed(2)}` : "—"}</td>
                <td>
                  <StatusBadge issue={i} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}