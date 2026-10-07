import axios from "axios";

// Points at your deployed backend. Set VITE_API_URL in .env for local dev
// against a different backend, or leave the fallback for the Render deploy.
const BASE_URL = import.meta.env.VITE_API_URL || "https://codexhotel-wg6d.onrender.com";

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
  // Generous: the Render free-tier backend cold-starts from sleep and can
  // take 30-50s to respond to the first request after idling.
  timeout: 45000,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("codexhotel_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const FALLBACK_MESSAGE = "Something went wrong. Please try again.";
const OFFLINE_MESSAGE = "Can't reach the server right now — please check your connection and try again in a moment.";
const TIMEOUT_MESSAGE = "That took too long to respond. Please try again in a moment.";

// The backend wraps every response as { success, message, data }.
// Unwrap that here so the rest of the app just deals with `data`.
apiClient.interceptors.response.use(
  (response) => response.data?.data,
  (error) => {
    // Never surface a raw payload to the UI — only ever a clean, human
    // sentence. Covers cold-start 502s / proxy timeouts (HTML bodies),
    // dropped connections, and any unrecognized response shape.
    if (error.code === "ECONNABORTED") {
      return Promise.reject(new Error(TIMEOUT_MESSAGE));
    }
    if (!error.response) {
      return Promise.reject(new Error(OFFLINE_MESSAGE));
    }

    const body = error.response.data;
    let message = FALLBACK_MESSAGE;

    if (body && typeof body === "object" && typeof body.message === "string" && body.message.trim()) {
      message = body.message;

      // Validation failures come back as { message: "Validation failed", data: { field: "reason", ... } }
      if (body.data && typeof body.data === "object" && !Array.isArray(body.data)) {
        const fieldErrors = Object.values(body.data).filter((v) => typeof v === "string" && v.trim());
        if (fieldErrors.length) message = fieldErrors.join(" ");
      }
    } else if (typeof error.message === "string" && error.message.trim()) {
      message = error.message;
    }

    return Promise.reject(new Error(message));
  }
);
