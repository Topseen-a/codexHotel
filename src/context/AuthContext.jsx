import { createContext, useContext, useEffect, useMemo, useState } from "react";
import * as authApi from "../api/auth";

const AuthContext = createContext(null);

const TOKEN_KEY = "codexhotel_token";

const STAFF_ROLES = ["ADMIN", "MANAGER", "RECEPTIONIST"];

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      setLoading(false);
      return;
    }

    authApi
      .me()
      .then(setUser)
      .catch(() => localStorage.removeItem(TOKEN_KEY))
      .finally(() => setLoading(false));
  }, []);

  const login = async (email, password) => {
    const result = await authApi.login({ email, password });
    localStorage.setItem(TOKEN_KEY, result.token);
    setUser(result.user);
    return result.user;
  };

  const register = async (payload) => {
    const result = await authApi.register(payload);
    localStorage.setItem(TOKEN_KEY, result.token);
    setUser(result.user);
    return result.user;
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    setUser(null);
  };

  const value = useMemo(
    () => ({
      user,
      loading,
      login,
      register,
      logout,
      isAuthenticated: Boolean(user),
      isStaff: Boolean(user) && STAFF_ROLES.includes(user.role),
      isAdmin: user?.role === "ADMIN",
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
