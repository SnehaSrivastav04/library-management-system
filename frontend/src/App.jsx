import { Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Books from "./pages/Books";
import MyIssues from "./pages/MyIssues";
import ManageIssues from "./pages/ManageIssues";

// Needs a token. If `roles` is given, the user's role must be one of them.
function ProtectedRoute({ roles, children }) {
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user") || "null");

  if (!token) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user?.role)) return <Navigate to="/books" replace />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/books"
        element={
          <ProtectedRoute>
            <Books />
          </ProtectedRoute>
        }
      />
      <Route
        path="/my-issues"
        element={
          <ProtectedRoute roles={["MEMBER"]}>
            <MyIssues />
          </ProtectedRoute>
        }
      />
      <Route
        path="/manage-issues"
        element={
          <ProtectedRoute roles={["ADMIN", "LIBRARIAN"]}>
            <ManageIssues />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/books" replace />} />
    </Routes>
  );
}