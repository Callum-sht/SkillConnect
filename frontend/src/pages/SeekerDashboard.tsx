import { useMemo, useState } from "react";
import DashboardLayout from "../components/layout/DashboardLayout";
import ServiceFilters from "../components/seekerDashboard/ServiceFilters";
import ServiceList from "../components/seekerDashboard/ServiceList";
import ServiceSearchBar from "../components/seekerDashboard/ServiceSearchBar";
import { useSeekerProfile } from "../hooks/useSeekerProfile";
import { useServiceListings } from "../hooks/useServiceListings";

function SeekerDashboard() {
  const { profile, isLoading, error: profileError } = useSeekerProfile();
  const { services, isLoading: servicesLoading, error: servicesError } = useServiceListings();
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const [location, setLocation] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  const categories = useMemo(() => [...new Set(services.map((service) => service.category))], [services]);
  const locations = useMemo(() => [...new Set(services.map((service) => service.location))], [services]);

  const filteredServices = useMemo(() => {
    const normalizedKeyword = keyword.trim().toLowerCase();

    return services.filter((service) => {
      const matchesKeyword =
        normalizedKeyword.length === 0 ||
        [service.title, service.provider, service.category].some((value) =>
          value.toLowerCase().includes(normalizedKeyword),
        );
      const matchesCategory = !category || service.category === category;
      const matchesLocation = !location || service.location === location;
      const matchesPrice = !maxPrice || service.price <= Number(maxPrice);

      return matchesKeyword && matchesCategory && matchesLocation && matchesPrice;
    });
  }, [category, keyword, location, maxPrice, services]);

  const hasActiveFilters = Boolean(keyword || category || location || maxPrice);

  const clearFilters = () => {
    setKeyword("");
    setCategory("");
    setLocation("");
    setMaxPrice("");
  };

  return (
    <DashboardLayout
      userName={profile?.name ?? (isLoading ? "Loading..." : "Guest")}
      profileHref="/seeker/profile" bookingsHref="/seeker/bookings" messagesHref="/messages"
    >
      <section className="space-y-8">
        {profileError && <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">{profileError} <a className="underline" href="/login">Sign in</a></p>}
        <header>
          <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
            Seeker dashboard
          </p>
          <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
            Find the right service
          </h1>
          <p className="mt-3 max-w-2xl text-muted">
            Search available services and narrow the results by category,
            location, or price.
          </p>
        </header>

        <div className="rounded-3xl border border-line bg-white p-5 shadow-sm sm:p-6">
          <div className="grid gap-4 lg:grid-cols-[2fr_3fr]">
            <ServiceSearchBar value={keyword} onChange={setKeyword} />
            <ServiceFilters
              categories={categories}
              locations={locations}
              category={category}
              location={location}
              maxPrice={maxPrice}
              onCategoryChange={setCategory}
              onLocationChange={setLocation}
              onMaxPriceChange={setMaxPrice}
            />
          </div>
        </div>

        <div className="flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm text-muted" aria-live="polite">
            {servicesLoading ? "Loading services..." : `Showing ${filteredServices.length} of ${services.length} services`}
          </p>
          {hasActiveFilters && (
            <button
              type="button"
              onClick={clearFilters}
              className="text-sm font-semibold text-brand transition hover:text-brand-dark"
            >
              Clear filters
            </button>
          )}
        </div>

        <ServiceList
          services={filteredServices}
          isLoading={servicesLoading}
          error={servicesError}
          onRequestBooking={(service) =>
            window.location.href = `/seeker/bookings/new?serviceId=${service.id}`
          }
        />
      </section>
    </DashboardLayout>
  );
}

export default SeekerDashboard;
