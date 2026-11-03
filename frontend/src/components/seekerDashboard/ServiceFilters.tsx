type ServiceFiltersProps = {
  categories: string[];
  locations: string[];
  category: string;
  location: string;
  maxPrice: string;
  onCategoryChange: (value: string) => void;
  onLocationChange: (value: string) => void;
  onMaxPriceChange: (value: string) => void;
};

function ServiceFilters({
  categories,
  locations,
  category,
  location,
  maxPrice,
  onCategoryChange,
  onLocationChange,
  onMaxPriceChange,
}: ServiceFiltersProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-3">
      <label className="space-y-2">
        <span className="text-sm font-semibold text-ink">Category</span>
        <select
          value={category}
          onChange={(event) => onCategoryChange(event.target.value)}
          className="w-full rounded-xl border border-line bg-white px-4 py-3 text-sm text-ink outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/15"
        >
          <option value="">All categories</option>
          {categories.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      </label>

      <label className="space-y-2">
        <span className="text-sm font-semibold text-ink">Location</span>
        <select
          value={location}
          onChange={(event) => onLocationChange(event.target.value)}
          className="w-full rounded-xl border border-line bg-white px-4 py-3 text-sm text-ink outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/15"
        >
          <option value="">All locations</option>
          {locations.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      </label>

      <label className="space-y-2">
        <span className="text-sm font-semibold text-ink">Maximum price</span>
        <select
          value={maxPrice}
          onChange={(event) => onMaxPriceChange(event.target.value)}
          className="w-full rounded-xl border border-line bg-white px-4 py-3 text-sm text-ink outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/15"
        >
          <option value="">Any price</option>
          <option value="25">Up to $25</option>
          <option value="50">Up to $50</option>
          <option value="75">Up to $75</option>
          <option value="100">Up to $100</option>
        </select>
      </label>
    </div>
  );
}

export default ServiceFilters;
