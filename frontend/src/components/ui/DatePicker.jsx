import { useCallback, useEffect, useId, useLayoutEffect, useRef, useState } from "react";
import { createPortal } from "react-dom";
import { addDaysISO, parseISODate, toISODate, todayISO } from "../../utils/format";
import Icon from "./Icon";
import "./DatePicker.css";

const WEEKDAYS = ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"];
const monthFormatter = new Intl.DateTimeFormat("en-GB", { month: "long", year: "numeric" });
const valueFormatter = new Intl.DateTimeFormat("en-GB", { weekday: "short", day: "numeric", month: "short", year: "numeric" });
const dayLabelFormatter = new Intl.DateTimeFormat("en-GB", { weekday: "long", day: "numeric", month: "long", year: "numeric" });

const POPOVER_WIDTH = 304;
const GAP = 10;

const firstOfMonth = (iso) => `${iso.slice(0, 7)}-01`;

function addMonthsISO(iso, months) {
  const date = parseISODate(iso);
  const day = date.getDate();
  date.setDate(1);
  date.setMonth(date.getMonth() + months);
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  date.setDate(Math.min(day, lastDay));
  return toISODate(date);
}

/** 6 weeks (Mon–Sun) covering the month that starts on `monthStart`. */
function monthGrid(monthStart) {
  const first = parseISODate(monthStart);
  const offset = (first.getDay() + 6) % 7; // Monday = 0
  const start = addDaysISO(monthStart, -offset);
  return Array.from({ length: 42 }, (_, i) => addDaysISO(start, i));
}

/**
 * Themed date picker that replaces <input type="date">. Values are ISO
 * yyyy-mm-dd strings. `variant="field"` looks like a form input (pair it with
 * <label htmlFor={id}>); `variant="inline"` shows its own label (search bar).
 * `rangeStart` highlights the nights between it and the selected date.
 */
export default function DatePicker({
  id,
  label,
  value,
  onChange,
  min,
  max,
  rangeStart,
  variant = "field",
  placeholder = "Select a date",
  clearable = false,
}) {
  const generatedId = useId();
  const triggerId = id || generatedId;
  const [open, setOpen] = useState(false);
  const [viewMonth, setViewMonth] = useState(() => firstOfMonth(value || todayISO()));
  const [focused, setFocused] = useState(value || todayISO());
  const [position, setPosition] = useState(null);
  const triggerRef = useRef(null);
  const popoverRef = useRef(null);
  const today = todayISO();

  const isDisabled = (iso) => (min && iso < min) || (max && iso > max);

  const openCalendar = () => {
    const start = value || (min && min > today ? min : today);
    setFocused(start);
    setViewMonth(firstOfMonth(start));
    setOpen(true);
  };

  const close = useCallback((refocus = true) => {
    setOpen(false);
    setPosition(null);
    if (refocus) triggerRef.current?.focus();
  }, []);

  const select = (iso) => {
    if (isDisabled(iso)) return;
    onChange(iso);
    close();
  };

  // Position the floating calendar next to the trigger (rendered in a portal so
  // scrolling containers such as modals can't clip it).
  const place = useCallback(() => {
    const rect = triggerRef.current?.getBoundingClientRect();
    if (!rect) return;
    const height = popoverRef.current?.offsetHeight || 360;
    const width = Math.min(POPOVER_WIDTH, window.innerWidth - 16);
    const fitsBelow = rect.bottom + GAP + height <= window.innerHeight || rect.top < height + GAP;
    const left = Math.min(Math.max(8, rect.left), window.innerWidth - width - 8);
    setPosition({ top: fitsBelow ? rect.bottom + GAP : rect.top - height - GAP, left, width });
  }, []);

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

  useEffect(() => {
    if (!open) return undefined;
    const onPointer = (event) => {
      if (!triggerRef.current?.contains(event.target) && !popoverRef.current?.contains(event.target)) close(false);
    };
    document.addEventListener("mousedown", onPointer);
    return () => document.removeEventListener("mousedown", onPointer);
  }, [open, close]);

  // Move keyboard focus to the focused day whenever it changes — once the
  // popover has been positioned (it's invisible, so unfocusable, until then).
  const placed = position !== null;
  useEffect(() => {
    if (open && placed) popoverRef.current?.querySelector(`[data-date="${focused}"]`)?.focus();
  }, [open, placed, focused, viewMonth]);

  const moveFocus = (iso) => {
    setFocused(iso);
    if (firstOfMonth(iso) !== viewMonth) setViewMonth(firstOfMonth(iso));
  };

  const onGridKey = (event) => {
    const weekday = (parseISODate(focused).getDay() + 6) % 7;
    const keys = {
      ArrowLeft: () => moveFocus(addDaysISO(focused, -1)),
      ArrowRight: () => moveFocus(addDaysISO(focused, 1)),
      ArrowUp: () => moveFocus(addDaysISO(focused, -7)),
      ArrowDown: () => moveFocus(addDaysISO(focused, 7)),
      Home: () => moveFocus(addDaysISO(focused, -weekday)),
      End: () => moveFocus(addDaysISO(focused, 6 - weekday)),
      PageUp: () => moveFocus(addMonthsISO(focused, event.shiftKey ? -12 : -1)),
      PageDown: () => moveFocus(addMonthsISO(focused, event.shiftKey ? 12 : 1)),
      Enter: () => select(focused),
      " ": () => select(focused),
    };
    if (keys[event.key]) {
      event.preventDefault();
      keys[event.key]();
    }
  };

  const onPopoverKey = (event) => {
    if (event.key === "Escape") {
      // Don't let an enclosing modal close as well.
      event.stopPropagation();
      event.nativeEvent.stopImmediatePropagation?.();
      close();
    }
  };

  const days = monthGrid(viewMonth);
  const canGoBack = !min || firstOfMonth(min) < viewMonth;
  const canGoForward = !max || firstOfMonth(max) > viewMonth;
  const display = value ? valueFormatter.format(parseISODate(value)) : placeholder;
  const dialogId = `${triggerId}-calendar`;

  return (
    <>
      <button
        ref={triggerRef}
        id={triggerId}
        type="button"
        className={`datepicker-trigger datepicker-trigger--${variant} ${value ? "" : "is-empty"} ${open ? "is-open" : ""}`}
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-controls={open ? dialogId : undefined}
        onClick={() => (open ? close() : openCalendar())}
      >
        {variant === "inline" && <span className="datepicker-label">{label}</span>}
        <span className="datepicker-value">{display}</span>
        <Icon name="calendar" size={16} className="datepicker-icon" />
      </button>

      {open &&
        createPortal(
          <div
            ref={popoverRef}
            id={dialogId}
            className="datepicker-popover"
            role="dialog"
            aria-modal="false"
            aria-label={label ? `Choose ${label.toLowerCase()} date` : "Choose a date"}
            style={position ? { top: position.top, left: position.left, width: position.width } : { visibility: "hidden" }}
            onKeyDown={onPopoverKey}
          >
            <div className="datepicker-header">
              <button
                type="button"
                className="datepicker-nav"
                onClick={() => setViewMonth(addMonthsISO(viewMonth, -1))}
                disabled={!canGoBack}
                aria-label="Previous month"
              >
                <Icon name="chevronLeft" size={16} />
              </button>
              <span className="datepicker-month" aria-live="polite">
                {monthFormatter.format(parseISODate(viewMonth))}
              </span>
              <button
                type="button"
                className="datepicker-nav"
                onClick={() => setViewMonth(addMonthsISO(viewMonth, 1))}
                disabled={!canGoForward}
                aria-label="Next month"
              >
                <Icon name="chevronRight" size={16} />
              </button>
            </div>

            <div className="datepicker-grid" role="grid" onKeyDown={onGridKey}>
              <div className="datepicker-row" role="row">
                {WEEKDAYS.map((day) => (
                  <span key={day} className="datepicker-weekday" role="columnheader" aria-label={day}>
                    {day}
                  </span>
                ))}
              </div>
              {Array.from({ length: 6 }, (_, week) => (
                <div key={week} className="datepicker-row" role="row">
                  {days.slice(week * 7, week * 7 + 7).map((iso) => {
                    const outside = firstOfMonth(iso) !== viewMonth;
                    const disabled = isDisabled(iso);
                    const selected = iso === value;
                    const inRange = rangeStart && value && iso > rangeStart && iso < value;
                    const isRangeStart = rangeStart && value && iso === rangeStart && rangeStart < value;
                    const classes = [
                      "datepicker-day",
                      outside && "is-outside",
                      disabled && "is-disabled",
                      selected && "is-selected",
                      inRange && "is-in-range",
                      isRangeStart && "is-range-start",
                      iso === today && "is-today",
                    ]
                      .filter(Boolean)
                      .join(" ");
                    return (
                      <span key={iso} role="gridcell" aria-selected={selected}>
                        <button
                          type="button"
                          className={classes}
                          data-date={iso}
                          tabIndex={iso === focused ? 0 : -1}
                          disabled={disabled}
                          aria-label={dayLabelFormatter.format(parseISODate(iso))}
                          aria-current={iso === today ? "date" : undefined}
                          onClick={() => select(iso)}
                          onFocus={() => setFocused(iso)}
                        >
                          {Number(iso.slice(8))}
                        </button>
                      </span>
                    );
                  })}
                </div>
              ))}
            </div>

            <div className="datepicker-footer">
              <button
                type="button"
                className="datepicker-link"
                onClick={() => moveFocus(isDisabled(today) && min ? min : today)}
              >
                {isDisabled(today) ? "Earliest date" : "Today"}
              </button>
              {clearable && value && (
                <button
                  type="button"
                  className="datepicker-link"
                  onClick={() => {
                    onChange("");
                    close();
                  }}
                >
                  Clear
                </button>
              )}
            </div>
          </div>,
          document.body
        )}
    </>
  );
}
