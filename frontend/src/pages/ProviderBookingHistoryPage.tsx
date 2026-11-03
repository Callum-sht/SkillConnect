import { useEffect, useState } from 'react'
import DashboardLayout from '../components/layout/DashboardLayout'
import { useProviderBookingHistory } from '../hooks/useBookingHistory'
import { getCurrentUserId } from '../services/authService'
import { getProviderProfile } from '../services/providerProfileService'

function formatStatus(status: string) {
  return status.charAt(0) + status.slice(1).toLowerCase()
}

function ProviderBookingHistoryPage() {
  const userId = getCurrentUserId()

  if (userId === null) {
    return (
      <DashboardLayout userName="Guest" profileHref="/provider/profile" bookingsHref="/provider/bookings/history" bookingRequestsHref="/provider/bookings">
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
          Please sign in to view your booking history. <a className="underline" href="/login">Sign in</a>
        </p>
      </DashboardLayout>
    )
  }

  return <ProviderBookingHistoryContent userId={userId} />
}

function ProviderBookingHistoryContent({ userId }: { userId: number }) {
  const { bookings, isLoading, error } = useProviderBookingHistory(userId)
  const [providerName, setProviderName] = useState('My Account')

  useEffect(() => {
    getProviderProfile(userId)
      .then((profile) => {
        if (profile) {
          setProviderName(profile.name)
        }
      })
      .catch(() => {})
  }, [userId])

  return (
    <DashboardLayout userName={providerName} profileHref="/provider/profile" bookingsHref="/provider/bookings/history" bookingRequestsHref="/provider/bookings" messagesHref="/messages">
      <section className="space-y-8">
        <header>
          <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
            Provider dashboard
          </p>
          <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
            My bookings
          </h1>
          <p className="mt-3 max-w-2xl text-muted">
            View your current and previous service bookings.
          </p>
        </header>

        {error && (
          <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
            {error}
          </p>
        )}

        {isLoading && <p className="text-muted">Loading your bookings...</p>}

        {!isLoading && !error && bookings.length === 0 && (
          <p className="rounded-3xl border border-line bg-white p-6 text-center text-muted">
            You have no bookings yet.
          </p>
        )}

        {!isLoading && bookings.length > 0 && (
          <div className="space-y-4">
            {bookings.map((booking) => (
              <div
                key={booking.bookingId}
                className="rounded-2xl border border-line bg-white p-5 shadow-sm"
              >
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <h2 className="font-semibold text-ink">{booking.serviceTitle}</h2>
                  <span className="rounded-full bg-brand/10 px-3 py-1 text-xs font-semibold text-brand">
                    {formatStatus(booking.status)}
                  </span>
                </div>
                <p className="mt-1 text-sm text-muted">Seeker: {booking.otherPartyName}</p>
                <p className="mt-1 text-sm text-muted">
                  {new Date(booking.bookingDate).toLocaleString()}
                </p>
                {booking.declineReason && (
                  <p className="mt-2 text-sm text-red-700">
                    Reason: {booking.declineReason}
                  </p>
                )}
              </div>
            ))}
          </div>
        )}
      </section>
    </DashboardLayout>
  )
}

export default ProviderBookingHistoryPage