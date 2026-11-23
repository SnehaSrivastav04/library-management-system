import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api" });

// Attach the JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// If the token is missing/expired (401), clear it and go back to login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401 && !err.config.url.includes("/auth/login")) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

// Turns a backend error into a readable message for the screen
export function errorMessage(err, fallback = "Something went wrong") {
  if (!err.response) return "Cannot reach the server. Is the backend running?";
  if (err.response.status === 403) return "You do not have permission to do this.";
  const data = err.response.data;
  if (data?.fieldErrors) return Object.values(data.fieldErrors).join(", ");
  if (data?.message) return data.message;
  return fallback;
}

export default api;