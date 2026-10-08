import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as authApi from "../api/auth";
import { SESSION_EXPIRED_EVENT, getToken, setToken } from "../api/client";
import { can, isStaffRole } from "../utils/roles";
import { AuthContext } from "./authContextObject";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(() => Boolean(getToken()));
  const [sessionExpired, setSessionExpired] = useState(false);
  const userRef = useRef(null);

  useEffect(() => {
    userRef.current = user;
  }, [user]);

  useEffect(() => {
    if (!getToken()) return;
    authApi
      .me()
      .then(setUser)
      .catch(() => setToken(null))
      .finally(() => setLoading(false));
  }, []);

  // The API client fires this when an authenticated request comes back 401.
  useEffect(() => {
    const onExpired = () => {
      if (userRef.current) setSessionExpired(true);
      setUser(null);
    };
    window.addEventListener(SESSION_EXPIRED_EVENT, onExpired);
    return () => window.removeEventListener(SESSION_EXPIRED_EVENT, onExpired);
  }, []);

  const startSession = useCallback((result) => {
    setToken(result.token);
    setSessionExpired(false);
    setUser(result.user);
    return result.user;
  }, []);

  const login = useCallback(
    async (email, password) => startSession(await authApi.login({ email, password })),
    [startSession]
  );

  const register = useCallback(async (payload) => startSession(await authApi.register(payload)), [startSession]);

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({
      user,
      loading,
      sessionExpired,
      login,
      register,
      logout,
      setUser,
      isAuthenticated: Boolean(user),
      isStaff: Boolean(user) && isStaffRole(user.role),
      can: (permission) => can(user, permission),
    }),
    [user, loading, sessionExpired, login, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
