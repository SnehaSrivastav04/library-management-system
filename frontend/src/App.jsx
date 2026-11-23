import { Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Books from "./pages/Books";

// Only lets logged-in users (with a token) see the page
function ProtectedRoute({ children }) {
  return localStorage.getItem("token") ? children : <Navigate to="/login" replace />;
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
      <Route path="*" element={<Navigate to="/books" replace />} />
    </Routes>
  );
}