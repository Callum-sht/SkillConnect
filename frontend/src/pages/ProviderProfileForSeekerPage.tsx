import { useEffect, useState } from 'react'
import {
  getProviderProfile,
  type ProviderProfileForSeeker,
} from '../services/providerProfileForSeekerService'

function getProviderIdFromPath(): number | null {
  const match = window.location.pathname.match(/^\/providers\/(\d+)$/)
  return match ? Number(match[1]) : null
}

function getInitials(name: string) {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
}

function ProviderProfileForSeekerPage() {
  const providerUserId = getProviderIdFromPath()
  const [profile, setProfile] = useState<ProviderProfileForSeeker | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    if (providerUserId === null) {
      setIsLoading(false)
      setNotFound(true)
      return
    }

    getProviderProfile(providerUserId)
      .then((result) => {
        if (result === null) {
          setNotFound(true)
        } else {
          setProfile(result)
        }
      })
      .finally(() => setIsLoading(false))
  }, [providerUserId])

  if (isLoading) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12 text-center text-muted">
        Loading profile...
      </div>
    )
  }

  if (notFound || profile === null) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12 text-center">
        <h1 className="text-2xl font-bold text-ink">Provider not found</h1>
        <p className="mt-2 text-muted">
          This profile may not exist.{' '}
          <a className="text-brand underline" href="/">
            Go back home
          </a>
        </p>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-12">
      <button
        className="text-sm font-semibold text-brand hover:underline"
        onClick={() => window.history.back()}
        type="button"
      >
        Back
      </button>

      <div className="mt-6 flex items-center gap-4">
        <span className="grid size-16 place-items-center rounded-full bg-brand text-xl font-bold text-white">
          {getInitials(profile.name)}
        </span>
        <div>
          <h1 className="text-2xl font-bold text-ink">{profile.name}</h1>
          <span className="font-bold text-amber-500">★ {profile.rating}</span>
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-line bg-white p-5">
        <h2 className="font-semibold text-ink">About</h2>
        <p className="mt-2 text-muted">
          {profile.bio ?? 'This provider has not added a bio yet.'}
        </p>
      </div>

      <div className="mt-4 rounded-2xl border border-line bg-white p-5">
        <h2 className="font-semibold text-ink">Availability</h2>
        <p className="mt-2 text-muted">
          {profile.availability ?? 'Not specified.'}
        </p>
      </div>

      <div className="mt-4 rounded-2xl border border-line bg-white p-5">
        <h2 className="font-semibold text-ink">Services offered</h2>
        {profile.services.length === 0 ? (
          <p className="mt-2 text-muted">No services listed yet.</p>
        ) : (
          <ul className="mt-3 space-y-3">
            {profile.services.map((service) => (
              <li key={service.id}>
                <a
                  className="flex items-center justify-between rounded-xl border border-line p-3 hover:border-brand"
                  href={`/services/${service.id}`}
                >
                  <div>
                    <p className="font-semibold text-ink">{service.title}</p>
                    <p className="text-sm text-muted">{service.category}</p>
                  </div>
                  <strong>${service.price}/hr</strong>
                </a>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}

export default ProviderProfileForSeekerPage