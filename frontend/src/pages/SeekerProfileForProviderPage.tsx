import { useEffect, useState } from 'react'
import DashboardLayout from '../components/layout/DashboardLayout'
import { getCurrentUserId } from '../services/authService'
import {
  getSeekerProfileForProvider,
  type SeekerProfileForProvider,
} from '../services/seekerProfileForProviderService'

function getSeekerIdFromPath(): number | null {
  const match = window.location.pathname.match(/^\/provider\/seekers\/(\d+)$/)
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

function SeekerProfileForProviderPage() {
  const providerUserId = getCurrentUserId()
  const seekerUserId = getSeekerIdFromPath()

  if (providerUserId === null) {
    return (
      <DashboardLayout userName="Guest" profileHref="/provider/profile">
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
          Please sign in to view this profile. <a className="underline" href="/login">Sign in</a>
        </p>
      </DashboardLayout>
    )
  }

  if (seekerUserId === null) {
    return (
      <DashboardLayout userName="Provider" profileHref="/provider/profile">
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
          This profile link is not valid.
        </p>
      </DashboardLayout>
    )
  }

  return (
    <SeekerProfileForProviderContent
      providerUserId={providerUserId}
      seekerUserId={seekerUserId}
    />
  )
}

function SeekerProfileForProviderContent({
  providerUserId,
  seekerUserId,
}: {
  providerUserId: number
  seekerUserId: number
}) {
  const [profile, setProfile] = useState<SeekerProfileForProvider | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [forbidden, setForbidden] = useState(false)

  useEffect(() => {
    getSeekerProfileForProvider(providerUserId, seekerUserId)
      .then((result) => {
        if (result === null) {
          setForbidden(true)
        } else {
          setProfile(result)
        }
      })
      .finally(() => setIsLoading(false))
  }, [providerUserId, seekerUserId])

  return (
    <DashboardLayout
      userName="Provider"
      profileHref="/provider/profile"
      bookingsHref="/provider/bookings/history"
      bookingRequestsHref="/provider/bookings"
      messagesHref="/messages"
    >
      <button
        className="text-sm font-semibold text-brand hover:underline"
        onClick={() => window.history.back()}
        type="button"
      >
        Back
      </button>

      {isLoading && (
        <p className="mt-6 text-center text-muted">Loading profile...</p>
      )}

      {!isLoading && forbidden && (
        <div className="mt-6 rounded-2xl border border-red-200 bg-red-50 p-6 text-center text-red-700">
          <h1 className="text-lg font-semibold">You cannot view this profile.</h1>
          <p className="mt-2 text-sm">
            You can only view the profile of seekers who have booked one of your services.
          </p>
        </div>
      )}

      {!isLoading && profile && (
        <div className="mt-6 max-w-xl">
          <div className="flex items-center gap-4">
            <span className="grid size-16 place-items-center rounded-full bg-brand text-xl font-bold text-white">
              {getInitials(profile.name)}
            </span>
            <h1 className="text-2xl font-bold text-ink">{profile.name}</h1>
          </div>

          <div className="mt-6 rounded-2xl border border-line bg-white p-5">
            <h2 className="font-semibold text-ink">About</h2>
            <p className="mt-2 text-muted">
              {profile.bio ?? 'This seeker has not added a bio yet.'}
            </p>
          </div>

          <div className="mt-4 rounded-2xl border border-line bg-white p-5">
            <h2 className="font-semibold text-ink">Booking history with you</h2>
            <p className="mt-2 text-muted">
              {profile.completedBookingsCount} completed booking
              {profile.completedBookingsCount === 1 ? '' : 's'}
            </p>
          </div>
        </div>
      )}
    </DashboardLayout>
  )
}

export default SeekerProfileForProviderPage