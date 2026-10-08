import { useState } from "react";
import { Link } from "react-router-dom";
import { createStaffUser, deleteUser, getAllUsers, updateUser } from "../../api/users";
import Alert from "../../components/ui/Alert";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import Dropdown from "../../components/ui/Dropdown";
import EmptyState from "../../components/ui/EmptyState";
import Icon from "../../components/ui/Icon";
import Modal from "../../components/ui/Modal";
import PasswordInput from "../../components/ui/PasswordInput";
import { useAuth } from "../../context/useAuth";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { initials } from "../../utils/format";
import { ALL_ROLES, ROLE_LABELS } from "../../utils/roles";
import AdminHeader from "./AdminHeader";

const PHONE_PATTERN = "^(\\+234|0)[789][01]\\d{8}$";
const ROLE_TONE = { ADMIN: "danger", MANAGER: "warning", RECEPTIONIST: "info", GUEST: "neutral" };

export default function UsersPage() {
  useDocumentTitle("Staff & Users");
  const { user: me } = useAuth();
  const users = useAsync(getAllUsers, []);

  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [editing, setEditing] = useState(null); // null | "new" | user
  const [toDelete, setToDelete] = useState(null);
  const [message, setMessage] = useState("");

  const term = search.trim().toLowerCase();
  const list = (users.data || [])
    .filter((u) => roleFilter === "ALL" || u.role === roleFilter)
    .filter((u) => !term || [u.name, u.email, u.phoneNumber].some((v) => v?.toLowerCase().includes(term)))
    .sort((a, b) => ALL_ROLES.indexOf(b.role) - ALL_ROLES.indexOf(a.role) || a.name.localeCompare(b.name));

  const counts = Object.fromEntries(ALL_ROLES.map((role) => [role, (users.data || []).filter((u) => u.role === role).length]));

  const onSaved = (saved, isNew) => {
    users.setData((items) => (isNew ? [...items, saved] : items.map((u) => (u.id === saved.id ? saved : u))));
    setMessage(isNew ? `${saved.name} was added as ${ROLE_LABELS[saved.role].toLowerCase()}.` : `${saved.name}'s account was updated.`);
  };

  const remove = async () => {
    await deleteUser(toDelete.id);
    users.setData((items) => items.filter((u) => u.id !== toDelete.id));
    setMessage(`${toDelete.name}'s account was deleted.`);
  };

  return (
    <>
      <AdminHeader
        title="Staff & Users"
        description="Create staff accounts, change roles and manage every account."
        actions={
          <button type="button" className="btn btn-primary btn-sm" onClick={() => setEditing("new")}>
            <Icon name="plus" size={16} /> Add account
          </button>
        }
      />

      <div className="admin-toolbar">
        <div className="chip-group" role="tablist" aria-label="Filter by role">
          {["ALL", ...[...ALL_ROLES].reverse()].map((role) => (
            <button
              key={role}
              type="button"
              role="tab"
              aria-selected={roleFilter === role}
              className={`chip ${roleFilter === role ? "active" : ""}`}
              onClick={() => setRoleFilter(role)}
            >
              {role === "ALL" ? "Everyone" : ROLE_LABELS[role]}
              {users.data && <span className="chip-count">{role === "ALL" ? users.data.length : counts[role]}</span>}
            </button>
          ))}
        </div>
        <label className="admin-search">
          <Icon name="search" size={16} />
          <input placeholder="Search name, email or phone" aria-label="Search users" value={search} onChange={(e) => setSearch(e.target.value)} />
        </label>
      </div>

      <Alert tone="success">{message}</Alert>
      <Alert tone="danger">{users.error?.message}</Alert>

      {users.loading && <div className="skeleton" style={{ height: 280 }} />}

      {users.data && (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th aria-label="Actions" />
              </tr>
            </thead>
            <tbody>
              {list.map((u) => (
                <tr key={u.id}>
                  <td data-label="Name">
                    <span className="user-cell">
                      <span className="avatar avatar-sm">{initials(u.name)}</span>
                      <span>
                        {u.name}
                        {u.id === me.id && <span className="faint"> (you)</span>}
                      </span>
                    </span>
                  </td>
                  <td data-label="Email">{u.email}</td>
                  <td data-label="Phone">{u.phoneNumber}</td>
                  <td data-label="Role">
                    <span className={`badge badge-${ROLE_TONE[u.role]}`}>{ROLE_LABELS[u.role]}</span>
                  </td>
                  <td data-label="">
                    <div className="cell-actions">
                      {u.role === "GUEST" && (
                        <Link to={`/admin/guests?id=${u.id}`} className="btn btn-ghost btn-sm">
                          Bookings
                        </Link>
                      )}
                      {u.id === me.id ? (
                        <Link to="/account" className="btn btn-outline btn-sm">
                          My account
                        </Link>
                      ) : (
                        <>
                          <button type="button" className="btn btn-outline btn-sm" onClick={() => setEditing(u)}>
                            <Icon name="edit" size={14} /> Edit
                          </button>
                          <button
                            type="button"
                            className="btn btn-ghost btn-icon btn-sm"
                            aria-label={`Delete ${u.name}`}
                            onClick={() => setToDelete(u)}
                          >
                            <Icon name="trash" size={16} />
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {list.length === 0 && <EmptyState icon="users" title="No matching accounts" />}
        </div>
      )}

      {editing && (
        <UserFormModal
          key={editing === "new" ? "new" : editing.id}
          user={editing === "new" ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={onSaved}
        />
      )}

      <ConfirmDialog
        open={Boolean(toDelete)}
        title={`Delete ${toDelete?.name}?`}
        confirmLabel="Delete account"
        onConfirm={remove}
        onClose={() => setToDelete(null)}
      >
        Their account will be removed permanently and they will no longer be able to sign in.
      </ConfirmDialog>
    </>
  );
}

function UserFormModal({ user, onClose, onSaved }) {
  const isNew = !user;
  const [form, setForm] = useState({
    name: user?.name || "",
    email: user?.email || "",
    phoneNumber: user?.phoneNumber || "",
    password: "",
    role: user?.role || "RECEPTIONIST",
  });
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const update = (field) => (e) => {
    setForm({ ...form, [field]: e.target.value });
    setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setFieldErrors({});
    setSaving(true);
    const payload = {
      name: form.name.trim(),
      email: form.email.trim(),
      phoneNumber: form.phoneNumber.trim(),
      role: form.role,
      ...(form.password ? { password: form.password } : {}),
    };
    try {
      const saved = isNew ? await createStaffUser(payload) : await updateUser(user.id, payload);
      onSaved(saved, isNew);
      onClose();
    } catch (err) {
      setFieldErrors(err.fieldErrors || {});
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const hint = (field, text) =>
    fieldErrors[field] ? <span className="field-error">{fieldErrors[field]}</span> : text && <span className="field-hint">{text}</span>;

  return (
    <Modal
      open
      title={isNew ? "Add an account" : `Edit ${user.name}`}
      onClose={onClose}
      footer={
        <>
          <button type="button" className="btn btn-outline" onClick={onClose}>
            Cancel
          </button>
          <button type="submit" form="user-form" className="btn btn-primary" disabled={saving}>
            {saving ? "Saving…" : isNew ? "Create account" : "Save changes"}
          </button>
        </>
      }
    >
      <form id="user-form" className="stack" onSubmit={submit}>
        <Alert tone="danger">{error}</Alert>
        <div className="form-grid">
          <div className="field">
            <label htmlFor="u-name">Full name</label>
            <input id="u-name" required value={form.name} onChange={update("name")} aria-invalid={Boolean(fieldErrors.name)} />
            {hint("name")}
          </div>
          <div className="field">
            <label htmlFor="u-role">Role</label>
            <Dropdown
              id="u-role"
              variant="field"
              label="Role"
              value={form.role}
              options={[...ALL_ROLES].reverse().map((role) => ({ value: role, label: ROLE_LABELS[role] }))}
              onChange={(role) => setForm({ ...form, role })}
            />
          </div>
          <div className="field">
            <label htmlFor="u-email">Email</label>
            <input id="u-email" type="email" required value={form.email} onChange={update("email")} aria-invalid={Boolean(fieldErrors.email)} />
            {hint("email")}
          </div>
          <div className="field">
            <label htmlFor="u-phone">Phone</label>
            <input
              id="u-phone"
              type="tel"
              required
              pattern={PHONE_PATTERN}
              title="A Nigerian number, e.g. 08012345678"
              placeholder="08012345678"
              value={form.phoneNumber}
              onChange={update("phoneNumber")}
              aria-invalid={Boolean(fieldErrors.phoneNumber)}
            />
            {hint("phoneNumber")}
          </div>
          <div className="field">
            <label htmlFor="u-password">{isNew ? "Password" : "New password"}</label>
            <PasswordInput
              id="u-password"
              autoComplete="new-password"
              required={isNew}
              minLength={8}
              value={form.password}
              onChange={update("password")}
              aria-invalid={Boolean(fieldErrors.password)}
            />
            {hint("password", isNew ? "At least 8 characters" : "Leave blank to keep their password")}
          </div>
        </div>
      </form>
    </Modal>
  );
}
