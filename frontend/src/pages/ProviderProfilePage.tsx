import { useEffect, useState } from "react";
import Footer from "../components/layout/Footer";
import ProviderNavbar from "../components/providerDashboard/ProviderNavbar";
import { getCurrentUserId } from "../services/authService";
import {
  addProviderService,
  deleteProviderService,
  getProviderProfile,
  getProviderServices,
  updateProviderProfile,
  type AddServiceInput,
  type ProviderProfile,
  type ProviderServiceListing,
} from "../services/providerProfileService";
import { getServiceCategories } from "../services/serviceListingService";

type ProfileDraft = {
  name: string;
  bio: string;
  availability: string;
  portfolio: string;
  location: string;
};

const emptyServiceDraft: AddServiceInput = {
  title: "",
  category: "",
  location: "",
  description: "",
  hourlyRate: 0,
};

function StarRating({ rating }: { rating: number | null }) {
  const value = rating ?? 0;
  return (
    <span className="flex items-center gap-1 text-sm font-semibold text-ink">
      <span className="flex text-amber-400" aria-hidden="true">
        {[1, 2, 3, 4, 5].map((star) => (
          <span key={star}>{value >= star ? "★" : "☆"}</span>
        ))}
      </span>
      {rating == null ? "New" : value.toFixed(1)}
    </span>
  );
}

function ProviderProfilePage() {
  const [profile, setProfile] = useState<ProviderProfile | null>(null);
  const [services, setServices] = useState<ProviderServiceListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  const [isEditing, setIsEditing] = useState(false);
  const [draft, setDraft] = useState<ProfileDraft>({
    name: "",
    bio: "",
    availability: "",
    portfolio: "",
    location: "",
  });
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [isAddingService, setIsAddingService] = useState(false);
  const [serviceDraft, setServiceDraft] =
    useState<AddServiceInput>(emptyServiceDraft);
  const [serviceError, setServiceError] = useState<string | null>(null);

  const [categories, setCategories] = useState<string[]>([]);
  const [isNewCategory, setIsNewCategory] = useState(false);

  const userId = getCurrentUserId();

  useEffect(() => {
    if (userId == null) {
      setLoadError("Please sign in as a provider to view your profile.");
      setLoading(false);
      return;
    }

    let cancelled = false;

    Promise.all([
      getProviderProfile(userId),
      getProviderServices(userId),
      getServiceCategories(),
    ])
      .then(([profileData, serviceData, categoryData]) => {
        if (cancelled) return;
        setProfile(profileData);
        setServices(serviceData);
        setCategories(categoryData);
      })
      .catch((error: unknown) => {
        if (cancelled) return;
        setLoadError(
          error instanceof Error
            ? error.message
            : "Unable to load your profile. Please try again.",
        );
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [userId]);

  const initials = (profile?.name ?? "P")
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  const memberSince = profile
    ? new Date(profile.createdAt).toLocaleDateString("en-AU", {
        month: "long",
        year: "numeric",
      })
    : "";

  const startEditing = () => {
    if (!profile) return;
    setDraft({
      name: profile.name,
      bio: profile.bio ?? "",
      availability: profile.availability ?? "",
      portfolio: profile.portfolio ?? "",
      location: profile.location ?? "",
    });
    setFormError(null);
    setSuccessMessage(null);
    setIsEditing(true);
  };

  const handleDraftChange =
    (field: keyof ProfileDraft) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setDraft({ ...draft, [field]: e.target.value });
    };

  const handleSave = async () => {
    if (userId == null || !profile) return;

    // Client-side validation: mandatory fields cannot be empty.
    if (!draft.name.trim()) {
      setFormError("Name is required.");
      return;
    }

    setFormError(null);
    try {
      const updated = await updateProviderProfile(userId, {
        name: draft.name,
        bio: draft.bio,
        availability: draft.availability,
        portfolio: draft.portfolio,
        location: draft.location,
      });
      setProfile(updated);
      setIsEditing(false);
      setSuccessMessage("Profile updated successfully.");
    } catch (error: unknown) {
      setFormError(
        error instanceof Error
          ? error.message
          : "Unable to save your profile. Please try again.",
      );
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setFormError(null);
  };

  const handleServiceDraftChange =
    (field: keyof AddServiceInput) =>
    (
      e: React.ChangeEvent<
        HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
      >,
    ) => {
      setServiceDraft({
        ...serviceDraft,
        [field]:
          field === "hourlyRate" ? Number(e.target.value) : e.target.value,
      });
    };

  const toggleNewCategory = () => {
    setIsNewCategory((prev) => !prev);
    setServiceDraft((prev) => ({ ...prev, category: "" }));
  };

  const handleAddService = async () => {
    if (userId == null) return;

    if (
      !serviceDraft.title.trim() ||
      !serviceDraft.category.trim() ||
      !serviceDraft.location.trim() ||
      !serviceDraft.description.trim()
    ) {
      setServiceError("All service fields are required.");
      return;
    }
    if (!(serviceDraft.hourlyRate > 0)) {
      setServiceError("Hourly rate must be greater than zero.");
      return;
    }

    setServiceError(null);
    try {
      const created = await addProviderService(userId, serviceDraft);
      setServices((prev) => [...prev, created]);
      setCategories((prev) =>
        prev.includes(created.category)
          ? prev
          : [...prev, created.category].sort((a, b) => a.localeCompare(b)),
      );
      setServiceDraft(emptyServiceDraft);
      setIsNewCategory(false);
      setIsAddingService(false);
    } catch (error: unknown) {
      setServiceError(
        error instanceof Error
          ? error.message
          : "Unable to add the service. Please try again.",
      );
    }
  };

  const handleDeleteService = async (id: number) => {
    if (userId == null) return;
    if (!window.confirm("Delete this service?")) return;

    try {
      await deleteProviderService(userId, id);
      setServices((prev) => prev.filter((service) => service.id !== id));
    } catch (error: unknown) {
      setServiceError(
        error instanceof Error
          ? error.message
          : "Unable to delete the service. Please try again.",
      );
    }
  };

  const inputClass =
    "mt-1 w-full rounded-xl border border-line bg-white px-3 py-2 text-ink focus:border-brand focus:outline-none";

  if (loading) {
    return (
      <>
        <ProviderNavbar />
        <div className="min-h-screen bg-surface">
          <main className="mx-auto w-full max-w-[1200px] px-4 py-8 sm:px-6">
            <p className="text-muted">Loading your profile…</p>
          </main>
        </div>
        <Footer />
      </>
    );
  }

  if (loadError || !profile) {
    return (
      <>
        <ProviderNavbar />
        <div className="min-h-screen bg-surface">
          <main className="mx-auto w-full max-w-[1200px] px-4 py-8 sm:px-6">
            <p className="rounded-xl border border-line bg-white p-4 font-semibold text-ink">
              {loadError ?? "Unable to load your profile. Please try again."}
            </p>
          </main>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <>
      <ProviderNavbar userName={profile.name} />
      <div className="min-h-screen bg-surface">
        <main className="mx-auto w-full max-w-[1200px] px-4 py-8 sm:px-6">
          <section id="profile" className="space-y-8">
            <header className="flex flex-wrap items-end justify-between gap-4">
              <div>
                <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
                  Provider profile
                </p>
                <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
                  Account and profile
                </h1>
                <p className="mt-3 text-muted">
                  {isEditing
                    ? "Update your personal information, then save your changes."
                    : "Review your personal information and provider profile."}
                </p>
              </div>

              {isEditing ? (
                <div className="flex gap-3">
                  <button
                    type="button"
                    onClick={handleCancel}
                    className="rounded-xl border border-line bg-white px-4 py-2.5 text-sm font-semibold text-ink transition hover:border-brand/40 hover:text-brand"
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    onClick={handleSave}
                    className="rounded-xl bg-brand px-4 py-2.5 text-sm font-semibold text-white transition hover:opacity-90"
                  >
                    Save changes
                  </button>
                </div>
              ) : (
                <button
                  type="button"
                  onClick={startEditing}
                  className="rounded-xl border border-line bg-white px-4 py-2.5 text-sm font-semibold text-ink transition hover:border-brand/40 hover:text-brand"
                >
                  Edit Profile
                </button>
              )}
            </header>

            {successMessage && (
              <p
                role="status"
                className="rounded-xl border border-line bg-brand/10 px-4 py-3 font-semibold text-brand-dark"
              >
                {successMessage}
              </p>
            )}
            {formError && (
              <p
                role="alert"
                className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 font-semibold text-red-700"
              >
                {formError}
              </p>
            )}

            <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_minmax(18rem,0.55fr)]">
              <article className="rounded-3xl border border-line bg-white p-6 shadow-sm sm:p-8">
                <div className="flex flex-col gap-5 sm:flex-row sm:items-center">
                  <div className="flex size-20 shrink-0 items-center justify-center rounded-full bg-brand text-xl font-bold text-white">
                    {initials}
                  </div>
                  <div className="min-w-0 flex-1">
                    {isEditing ? (
                      <label className="block">
                        <span className="text-sm font-medium text-muted">Name</span>
                        <input
                          type="text"
                          value={draft.name}
                          onChange={handleDraftChange("name")}
                          className={inputClass}
                        />
                      </label>
                    ) : (
                      <>
                        <h2 className="text-2xl font-bold text-ink">
                          {profile.name}
                        </h2>
                        <p className="mt-1 text-muted">{profile.email}</p>
                      </>
                    )}
                    <span className="mt-3 inline-flex rounded-full bg-brand/10 px-3 py-1 text-xs font-semibold text-brand-dark">
                      {profile.role}
                    </span>
                  </div>
                </div>

                <div className="mt-8 border-t border-line pt-6">
                  <h3 className="text-lg font-bold text-ink">About me</h3>
                  {isEditing ? (
                    <textarea
                      value={draft.bio}
                      onChange={handleDraftChange("bio")}
                      rows={5}
                      className={`${inputClass} mt-3 resize-y leading-7`}
                      placeholder="Tell clients who you are, your experience, and how you like to work."
                    />
                  ) : (
                    <p className="mt-3 leading-7 text-muted">
                      {profile.bio ?? "No bio added yet."}
                    </p>
                  )}
                </div>

                <div className="mt-8 border-t border-line pt-6">
                  <h3 className="text-lg font-bold text-ink">Portfolio</h3>
                  {isEditing ? (
                    <textarea
                      value={draft.portfolio}
                      onChange={handleDraftChange("portfolio")}
                      rows={3}
                      className={`${inputClass} mt-3 resize-y leading-7`}
                      placeholder="Links to your past work, one per line."
                    />
                  ) : (
                    <p className="mt-3 whitespace-pre-line leading-7 text-muted">
                      {profile.portfolio ?? "No portfolio added yet."}
                    </p>
                  )}
                </div>
              </article>

              <aside className="rounded-3xl border border-line bg-white p-6 shadow-sm sm:p-8">
                <h2 className="text-xl font-bold text-ink">Account details</h2>
                <dl className="mt-6 divide-y divide-line">
                  <div className="pb-4">
                    <dt className="text-sm font-medium text-muted">Email</dt>
                    <dd className="mt-1 break-all font-semibold text-ink">
                      {profile.email}
                    </dd>
                  </div>
                  <div className="py-4">
                    <dt className="text-sm font-medium text-muted">Location</dt>
                    <dd className="mt-1 font-semibold text-ink">
                      {isEditing ? (
                        <input
                          type="text"
                          value={draft.location}
                          onChange={handleDraftChange("location")}
                          className={inputClass}
                          aria-label="Location"
                        />
                      ) : (
                        profile.location ?? "Not set"
                      )}
                    </dd>
                  </div>
                  <div className="py-4">
                    <dt className="text-sm font-medium text-muted">
                      Availability
                    </dt>
                    <dd className="mt-1 font-semibold text-ink">
                      {isEditing ? (
                        <input
                          type="text"
                          value={draft.availability}
                          onChange={handleDraftChange("availability")}
                          className={inputClass}
                          aria-label="Availability"
                          placeholder="e.g. Weekdays after 5pm, weekends"
                        />
                      ) : (
                        profile.availability ?? "Not set"
                      )}
                    </dd>
                  </div>
                  <div className="py-4">
                    <dt className="text-sm font-medium text-muted">
                      Account status
                    </dt>
                    <dd className="mt-1 font-semibold text-ink">
                      {profile.status}
                    </dd>
                  </div>
                  <div className="pt-4">
                    <dt className="text-sm font-medium text-muted">
                      Member since
                    </dt>
                    <dd className="mt-1 font-semibold text-ink">{memberSince}</dd>
                  </div>
                </dl>
              </aside>
            </div>

            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-ink">My services</h2>
                <button
                  type="button"
                  onClick={() => {
                    setServiceError(null);
                    setIsAddingService(true);
                  }}
                  aria-label="Add a service"
                  className="flex size-10 items-center justify-center rounded-full bg-brand text-xl font-bold text-white transition hover:opacity-90"
                >
                  +
                </button>
              </div>

              {serviceError && (
                <p
                  role="alert"
                  className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 font-semibold text-red-700"
                >
                  {serviceError}
                </p>
              )}

              {isAddingService && (
                <div className="rounded-3xl border border-line bg-white p-6 shadow-sm">
                  <h3 className="text-lg font-bold text-ink">New service</h3>
                  <div className="mt-4 grid gap-4 sm:grid-cols-2">
                    <label className="block">
                      <span className="text-sm font-medium text-muted">Title</span>
                      <input
                        type="text"
                        value={serviceDraft.title}
                        onChange={handleServiceDraftChange("title")}
                        className={inputClass}
                      />
                    </label>
                    <label className="block">
                      <span className="text-sm font-medium text-muted">
                        Category
                      </span>
                      {isNewCategory ? (
                        <input
                          type="text"
                          value={serviceDraft.category}
                          onChange={handleServiceDraftChange("category")}
                          className={inputClass}
                          placeholder="Enter new category name"
                        />
                      ) : (
                        <select
                          value={serviceDraft.category}
                          onChange={handleServiceDraftChange("category")}
                          className={inputClass}
                        >
                          <option value="">Select a category</option>
                          {categories.map((category) => (
                            <option key={category} value={category}>
                              {category}
                            </option>
                          ))}
                        </select>
                      )}
                      <button
                        type="button"
                        onClick={toggleNewCategory}
                        className="mt-1 text-xs font-semibold text-brand hover:underline"
                      >
                        {isNewCategory
                          ? "Choose an existing category instead"
                          : "+ Create new category"}
                      </button>
                    </label>
                    <label className="block">
                      <span className="text-sm font-medium text-muted">
                        Location
                      </span>
                      <input
                        type="text"
                        value={serviceDraft.location}
                        onChange={handleServiceDraftChange("location")}
                        className={inputClass}
                      />
                    </label>
                    <label className="block">
                      <span className="text-sm font-medium text-muted">
                        Hourly rate ($)
                      </span>
                      <input
                        type="number"
                        min="1"
                        value={serviceDraft.hourlyRate || ""}
                        onChange={handleServiceDraftChange("hourlyRate")}
                        className={inputClass}
                      />
                    </label>
                    <label className="block sm:col-span-2">
                      <span className="text-sm font-medium text-muted">
                        Description
                      </span>
                      <textarea
                        value={serviceDraft.description}
                        onChange={handleServiceDraftChange("description")}
                        rows={3}
                        className={`${inputClass} resize-y`}
                      />
                    </label>
                  </div>
                  <div className="mt-5 flex gap-3">
                    <button
                      type="button"
                      onClick={handleAddService}
                      className="rounded-xl bg-brand px-4 py-2.5 text-sm font-semibold text-white transition hover:opacity-90"
                    >
                      Add service
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setIsAddingService(false);
                        setServiceDraft(emptyServiceDraft);
                        setIsNewCategory(false);
                        setServiceError(null);
                      }}
                      className="rounded-xl border border-line bg-white px-4 py-2.5 text-sm font-semibold text-ink transition hover:border-brand/40 hover:text-brand"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}

              <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                {services.map((service) => (
                  <article
                    key={service.id}
                    onClick={() => {
                      window.location.href = `/services/${service.id}`
                    }}
                    className="cursor-pointer overflow-hidden rounded-3xl border border-line bg-white shadow-sm transition hover:shadow-md"
                  >
                    <div className="relative">
                      <div className="flex h-44 w-full items-center justify-center bg-brand/5 text-sm text-muted">
                        {service.category}
                      </div>
                      <button
                        type="button"
                        onClick={(event) => {
                          event.stopPropagation()
                          handleDeleteService(service.id)
                        }}
                        aria-label={`Delete ${service.title}`}
                        className="absolute right-3 top-3 flex size-8 items-center justify-center rounded-full bg-white text-lg font-bold text-ink shadow-sm transition hover:bg-red-500 hover:text-white"
                      >
                        −
                      </button>
                    </div>
                    <div className="p-5">
                      <div className="flex items-start justify-between gap-3">
                        <h3 className="text-lg font-bold text-ink">
                          {service.title}
                        </h3>
                        <span className="whitespace-nowrap rounded-full bg-brand/10 px-3 py-1 text-sm font-bold text-brand-dark">
                          ${service.price}/hr
                        </span>
                      </div>
                      <div className="mt-2">
                        <StarRating rating={service.rating} />
                      </div>
                      <p className="mt-2 text-sm leading-6 text-muted">
                        {service.description}
                      </p>
                    </div>
                  </article>
                ))}
              </div>

              {services.length === 0 && !isAddingService && (
                <p className="text-muted">
                  You have no services yet. Use the + button to add your first
                  one.
                </p>
              )}
            </div>
          </section>
        </main>
      </div>
      <Footer />
    </>
  );
}

export default ProviderProfilePage;