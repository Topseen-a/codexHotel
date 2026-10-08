import { useCallback, useEffect, useState } from "react";

/**
 * Runs an async loader whenever `deps` change and tracks its state.
 * Pass `null` as the loader to skip loading (e.g. until an input is ready).
 */
export function useAsync(loader, deps = []) {
  const [state, setState] = useState({ data: null, error: null, loading: Boolean(loader) });
  const [version, setVersion] = useState(0);

  useEffect(() => {
    if (!loader) {
      setState({ data: null, error: null, loading: false });
      return undefined;
    }

    let active = true;
    setState((prev) => ({ ...prev, loading: true, error: null }));

    loader().then(
      (data) => active && setState({ data, error: null, loading: false }),
      (error) => active && setState({ data: null, error, loading: false })
    );

    return () => {
      active = false;
    };
    // The caller controls re-runs through `deps`; `version` forces a reload.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, version]);

  const reload = useCallback(() => setVersion((v) => v + 1), []);
  const setData = useCallback(
    (updater) => setState((prev) => ({ ...prev, data: typeof updater === "function" ? updater(prev.data) : updater })),
    []
  );

  return { ...state, reload, setData };
}
