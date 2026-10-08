import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_URL || "https://codexhotel-wg6d.onrender.com";

export const TOKEN_KEY = "codexhotel_token";

/** Fired when an authenticated request is rejected, so the app can sign the user out. */
export const SESSION_EXPIRED_EVENT = "codexhotel:session-expired";

const FALLBACK_MESSAGE = "Something went wrong. Please try again.";
const OFFLINE_MESSAGE = "Can't reach the server right now — please check your connection and try again in a moment.";
const TIMEOUT_MESSAGE = "That took too long to respond. Please try again in a moment.";

/** Error carrying the backend's message, HTTP status and any per-field validation errors. */
export class ApiError extends Error {
  constructor(message, status = 0, fieldErrors = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

export const getToken = () => {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
};

export const setToken = (token) => {
  try {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    else localStorage.removeItem(TOKEN_KEY);
  } catch {
    // Storage unavailable (private mode etc.) — the session just won't persist.
  }
};

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 45000,
});

apiClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// The backend wraps every response as { success, message, data } — unwrap to `data`.
apiClient.interceptors.response.use(
  (response) => response.data?.data,
  (error) => {
    if (error.code === "ECONNABORTED") {
      return Promise.reject(new ApiError(TIMEOUT_MESSAGE));
    }
    if (!error.response) {
      return Promise.reject(new ApiError(OFFLINE_MESSAGE));
    }

    const { status, data: body } = error.response;

    if (status === 401 && error.config?.headers?.Authorization) {
      setToken(null);
      window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
    }

    let message = FALLBACK_MESSAGE;
    let fieldErrors = {};

    if (body && typeof body === "object" && typeof body.message === "string" && body.message.trim()) {
      message = body.message;

      // Validation failures come back as { message: "Validation failed", data: { field: "reason" } }
      if (body.data && typeof body.data === "object" && !Array.isArray(body.data)) {
        fieldErrors = body.data;
        const reasons = Object.values(body.data).filter((v) => typeof v === "string" && v.trim());
        if (reasons.length) message = reasons.join(" ");
      }
    }

    return Promise.reject(new ApiError(message, status, fieldErrors));
  }
);
