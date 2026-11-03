import { useState } from "react";
import type { FormEvent } from "react";
import DashboardLayout from "../components/layout/DashboardLayout";
import { useSeekerProfile } from "../hooks/useSeekerProfile";

function SeekerProfilePage() {
  const { profile, isLoading, error, saveProfile } = useSeekerProfile();
  const [editing, setEditing] = useState(false);
  const [nameInput, setNameInput] = useState("");
  const [bioInput, setBioInput] = useState("");
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");
  const [savedMessage, setSavedMessage] = useState("");

  function startEditing() {
    if (!profile) return;
    setNameInput(profile.name);
    setBioInput(profile.bio ?? "");
    setSaveError("");
    setSavedMessage("");
    setEditing(true);
  }

  async function handleSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (saving) return;

    const name = nameInput.trim();
    const bio = bioInput.trim();
    if (!name || name.length > 100 || bio.length > 1000) {
      setSaveError("Name is required (up to 100 characters); bio can be up to 1000 characters.");
      return;
    }

    setSaving(true);
    setSaveError("");
    try {
      await saveProfile({ name, bio });
      setEditing(false);
      setSavedMessage("Profile updated.");
    } catch (cause) {
      setSaveError(cause instanceof Error ? cause.message : "Unable to save your profile.");
    } finally {
      setSaving(false);
    }
  }

  if (!profile) {
    return (
      <DashboardLayout userName="Guest" profileHref="/seeker/profile" bookingsHref="/seeker/bookings">
        <div className="rounded-3xl border border-line bg-white p-8 text-center">
          <h1 className="text-2xl font-bold text-ink">{isLoading ? "Loading profile..." : "Profile unavailable"}</h1>
          {error && <p role="alert" className="mt-3 text-muted">{error}</p>}
          {!isLoading && <a className="mt-5 inline-block font-semibold text-brand" href="/login">Go to sign in</a>}
        </div>
      </DashboardLayout>
    );
  }

  const initials = profile.name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <DashboardLayout userName={profile.name} profileHref="#profile" bookingsHref="/seeker/bookings" messagesHref="/messages">
      <section id="profile" className="space-y-8">
        <header className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
              Seeker profile
            </p>
            <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
              Account and profile
            </h1>
            <p className="mt-3 text-muted">
              Review your personal information and seeker profile.
            </p>
          </div>
          <a
            href="/seeker/dashboard"
            className="rounded-xl border border-line bg-white px-4 py-2.5 text-sm font-semibold text-ink transition hover:border-brand/40 hover:text-brand"
          >
            Back to dashboard
          </a>
        </header>

        <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_minmax(18rem,0.55fr)]">
          <article className="rounded-3xl border border-line bg-white p-6 shadow-sm sm:p-8">
            <div className="flex flex-col gap-5 sm:flex-row sm:items-center">
              <div className="flex size-20 shrink-0 items-center justify-center rounded-full bg-brand text-xl font-bold text-white">
                {initials}
              </div>
              <div>
                <h2 className="text-2xl font-bold text-ink">{profile.name}</h2>
                <p className="mt-1 text-muted">{profile.email}</p>
                <span className="mt-3 inline-flex rounded-full bg-brand/10 px-3 py-1 text-xs font-semibold text-brand-dark">
                  Seeker
                </span>
              </div>
            </div>

            <div className="mt-8 border-t border-line pt-6">
              <div className="flex items-center justify-between gap-4">
                <h3 className="text-lg font-bold text-ink">{editing ? "Edit profile" : "About me"}</h3>
                {!editing && (
                  <button type="button" onClick={startEditing} className="text-sm font-semibold text-brand hover:text-brand-dark">
                    Edit profile
                  </button>
                )}
              </div>

              {savedMessage && <p role="status" className="mt-3 text-sm text-green-700">{savedMessage}</p>}

              {editing ? (
                <form onSubmit={handleSave} className="mt-5 space-y-4">
                  <div>
                    <label htmlFor="seeker-name" className="block text-sm font-semibold text-ink">Name</label>
                    <input
                      id="seeker-name"
                      name="name"
                      type="text"
                      required
                      maxLength={100}
                      value={nameInput}
                      onChange={(event) => setNameInput(event.target.value)}
                      className="mt-2 w-full rounded-xl border border-line bg-white px-4 py-3 text-ink focus:border-brand focus:outline-none"
                    />
                  </div>
                  <div>
                    <label htmlFor="seeker-bio" className="block text-sm font-semibold text-ink">Bio (optional)</label>
                    <textarea
                      id="seeker-bio"
                      name="bio"
                      rows={5}
                      maxLength={1000}
                      value={bioInput}
                      onChange={(event) => setBioInput(event.target.value)}
                      className="mt-2 w-full rounded-xl border border-line bg-white px-4 py-3 text-ink focus:border-brand focus:outline-none"
                    />
                    <p className="mt-1 text-xs text-muted">{bioInput.length}/1000 characters</p>
                  </div>
                  {saveError && <p role="alert" className="text-sm text-red-700">{saveError}</p>}
                  <div className="flex flex-wrap gap-3">
                    <button type="submit" disabled={saving} className="rounded-xl bg-brand px-4 py-2.5 text-sm font-semibold text-white hover:bg-brand-dark disabled:opacity-50">
                      {saving ? "Saving..." : "Save changes"}
                    </button>
                    <button type="button" disabled={saving} onClick={() => { setEditing(false); setSaveError(""); }} className="rounded-xl border border-line px-4 py-2.5 text-sm font-semibold text-ink hover:bg-surface disabled:opacity-50">
                      Cancel
                    </button>
                  </div>
                </form>
              ) : (
                <p className="mt-3 leading-7 text-muted">{profile.bio || "No bio added yet."}</p>
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
                <dt className="text-sm font-medium text-muted">Account status</dt>
                <dd className="mt-1 font-semibold text-ink">{profile.status}</dd>
              </div>
              <div className="pt-4">
                <dt className="text-sm font-medium text-muted">Member since</dt>
                <dd className="mt-1 font-semibold text-ink">
                  {new Intl.DateTimeFormat("en-AU", { month: "long", year: "numeric" }).format(new Date(profile.createdAt))}
                </dd>
              </div>
            </dl>
          </aside>
        </div>
      </section>
    </DashboardLayout>
  );
}

export default SeekerProfilePage;
