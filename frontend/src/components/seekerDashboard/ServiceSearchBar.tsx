type ServiceSearchBarProps = {
  value: string;
  onChange: (value: string) => void;
};

function ServiceSearchBar({ value, onChange }: ServiceSearchBarProps) {
  return (
    <label className="space-y-2">
      <span className="text-sm font-semibold text-ink">Search services</span>
      <input
        type="search"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="Try cleaning, tutoring, or a provider"
        className="w-full rounded-xl border border-line bg-white px-4 py-3 text-sm text-ink outline-none transition placeholder:text-muted focus:border-brand focus:ring-2 focus:ring-brand/15"
      />
    </label>
  );
}

export default ServiceSearchBar;
