import { useCallback, useEffect, useId, useLayoutEffect, useRef, useState } from "react";
import { createPortal } from "react-dom";
import Icon from "./Icon";
import "./Dropdown.css";

const GAP = 8;
const MAX_WIDTH = 360;

/**
 * Accessible custom select (ARIA listbox) styled to the site theme.
 *
 * options: [{ value, label, hint? }]
 * variant:
 *   - "inline"  label above the value, no box (search bar)
 *   - "field"   looks like a form input; pair with <label htmlFor={id}>
 *   - "compact" smaller field for tables and toolbars
 * The menu renders in a portal so scrolling containers (modals) can't clip it.
 */
export default function Dropdown({
  id,
  label,
  value,
  options,
  onChange,
  variant = "inline",
  placeholder = "Select…",
  className = "",
}) {
  const generatedId = useId();
  const baseId = id || generatedId;
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);
  const [position, setPosition] = useState(null);
  const triggerRef = useRef(null);
  const listRef = useRef(null);

  const selectedIndex = options.findIndex((option) => option.value === value);
  const selected = options[selectedIndex];

  const openList = () => {
    setActive(Math.max(selectedIndex, 0));
    setOpen(true);
  };

  const close = useCallback((refocus = true) => {
    setOpen(false);
    setPosition(null);
    if (refocus) triggerRef.current?.focus();
  }, []);

  const choose = (index) => {
    onChange(options[index].value);
    close();
  };

  // In the search bar, line the menu up with the whole field (icon included).
  const place = useCallback(() => {
    const trigger = triggerRef.current;
    if (!trigger) return;
    const anchor = (variant === "inline" && trigger.closest(".search-field")) || trigger;
    const rect = anchor.getBoundingClientRect();
    const height = listRef.current?.offsetHeight || 240;
    const width = Math.min(Math.max(rect.width, 220), MAX_WIDTH, window.innerWidth - 16);
    const below = rect.bottom + GAP + height <= window.innerHeight || rect.top < height + GAP;
    const left = Math.min(Math.max(8, rect.left), window.innerWidth - width - 8);
    setPosition({ top: below ? rect.bottom + GAP : rect.top - height - GAP, left, minWidth: width });
  }, [variant]);

  useLayoutEffect(() => {
    if (!open) return undefined;
    place();
    window.addEventListener("resize", place);
    window.addEventListener("scroll", place, true);
    return () => {
      window.removeEventListener("resize", place);
      window.removeEventListener("scroll", place, true);
    };
  }, [open, place]);

  const placed = position !== null;
  useEffect(() => {
    if (open && placed) listRef.current?.focus();
  }, [open, placed]);

  useEffect(() => {
    if (!open) return undefined;
    const onPointer = (event) => {
      if (!triggerRef.current?.contains(event.target) && !listRef.current?.contains(event.target)) close(false);
    };
    document.addEventListener("mousedown", onPointer);
    return () => document.removeEventListener("mousedown", onPointer);
  }, [open, close]);

  // Keep the highlighted option in view while moving with the keyboard.
  useEffect(() => {
    if (open) listRef.current?.children[active]?.scrollIntoView({ block: "nearest" });
  }, [open, active]);

  const onTriggerKey = (event) => {
    if (["ArrowDown", "ArrowUp", "Enter", " "].includes(event.key)) {
      event.preventDefault();
      openList();
    }
  };

  const onListKey = (event) => {
    const last = options.length - 1;
    const moves = {
      ArrowDown: () => setActive((i) => Math.min(i + 1, last)),
      ArrowUp: () => setActive((i) => Math.max(i - 1, 0)),
      Home: () => setActive(0),
      End: () => setActive(last),
      Enter: () => choose(active),
      " ": () => choose(active),
      Escape: () => close(),
      Tab: () => close(false),
    };
    if (!moves[event.key]) return;
    if (event.key !== "Tab") event.preventDefault();
    // Don't let an enclosing modal react to Escape as well.
    if (event.key === "Escape") event.stopPropagation();
    moves[event.key]();
  };

  const valueText = selected ? selected.label : placeholder;

  return (
    <div className={`dropdown dropdown--${variant} ${open ? "is-open" : ""} ${className}`}>
      <button
        ref={triggerRef}
        id={baseId}
        type="button"
        className={`dropdown-trigger ${selected ? "" : "is-empty"}`}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={open ? `${baseId}-list` : undefined}
        aria-label={label ? `${label}: ${valueText}` : undefined}
        onClick={() => (open ? close() : openList())}
        onKeyDown={onTriggerKey}
      >
        {variant === "inline" && <span className="dropdown-label">{label}</span>}
        <span className="dropdown-value">{valueText}</span>
        <Icon name="chevronDown" size={16} className="dropdown-chevron" />
      </button>

      {open &&
        createPortal(
          <ul
            ref={listRef}
            id={`${baseId}-list`}
            className="dropdown-list"
            role="listbox"
            tabIndex={-1}
            aria-label={label}
            aria-activedescendant={`${baseId}-opt-${active}`}
            style={position ? { top: position.top, left: position.left, minWidth: position.minWidth } : { visibility: "hidden" }}
            onKeyDown={onListKey}
          >
            {options.map((option, index) => {
              const isSelected = index === selectedIndex;
              return (
                <li
                  key={String(option.value)}
                  id={`${baseId}-opt-${index}`}
                  role="option"
                  aria-selected={isSelected}
                  className={`dropdown-option ${index === active ? "is-active" : ""} ${isSelected ? "is-selected" : ""}`}
                  onMouseEnter={() => setActive(index)}
                  onMouseDown={(event) => event.preventDefault()}
                  onClick={() => choose(index)}
                >
                  <span className="dropdown-option-text">
                    <span>{option.label}</span>
                    {option.hint && <small>{option.hint}</small>}
                  </span>
                  {isSelected && <Icon name="check" size={16} className="dropdown-check" />}
                </li>
              );
            })}
          </ul>,
          document.body
        )}
    </div>
  );
}
