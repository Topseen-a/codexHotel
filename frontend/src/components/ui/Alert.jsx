import Icon from "./Icon";

const ICONS = { success: "checkCircle", danger: "alertCircle", info: "info", warning: "alertCircle" };

export default function Alert({ tone = "info", children, className = "" }) {
  if (!children) return null;
  return (
    <div className={`alert alert-${tone} ${className}`} role={tone === "danger" ? "alert" : "status"}>
      <Icon name={ICONS[tone]} size={18} />
      <div>{children}</div>
    </div>
  );
}
