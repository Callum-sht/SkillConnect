import { useCallback, useEffect, useState } from 'react'
import BookingRequestCard from '../components/booking/BookingRequestCard'
import DashboardLayout from '../components/layout/DashboardLayout'
import { getCurrentUserId } from '../services/authService'
import {
  acceptBooking,
  completeBooking,
  declineBooking,
  getIncomingBookingRequests,
  type ProviderBookingRequest,
} from '../services/providerBookingService'

function ProviderBookingRequestsPage() {
  const currentProviderId = getCurrentUserId()

  if (currentProviderId === null) {
    return (
      <DashboardLayout userName="Guest" profileHref="/provider/profile">
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
          Please sign in to view your booking requests. <a className="underline" href="/login">Sign in</a>
        </p>
      </DashboardLayout>
    )
  }

  return <ProviderBookingRequestsContent currentProviderId={currentProviderId} />
}

function ProviderBookingRequestsContent({
  currentProviderId,
}: {
  currentProviderId: number
}) {
  const [bookings, setBookings] = useState<ProviderBookingRequest[]>([])
  const [declineReasons, setDeclineReasons] = useState<Record<number, string>>({})
  const [bookingMessage, setBookingMessage] = useState('')
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const loadBookings = useCallback(async () => {
    setIsLoading(true)
    setError(null)

    try {
      setBookings(await getIncomingBookingRequests(currentProviderId))
    } catch {
      setError('Unable to load booking requests. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }, [currentProviderId])

  useEffect(() => {
    void loadBookings()
  }, [loadBookings])

  async function handleAccept(bookingId: number) {
    const message = await acceptBooking(currentProviderId, bookingId)
    setBookingMessage(message)

    if (message !== 'Booking accepted successfully.') {
      return
    }

    await loadBookings()
  }

  async function handleComplete(bookingId: number) {
    const message = await completeBooking(currentProviderId, bookingId)
    setBookingMessage(message)

    if (message !== 'Booking marked as completed successfully.') {
      return
    }

    await loadBookings()
  }

  async function handleDecline(bookingId: number) {
    const reason = declineReasons[bookingId] ?? ''
    const message = await declineBooking(currentProviderId, bookingId, reason)
    setBookingMessage(message)

    if (message !== 'Booking declined successfully.') {
      return
    }

    await loadBookings()
  }

  function handleDeclineReasonChange(bookingId: number, reason: string) {
    setDeclineReasons({
      ...declineReasons,
      [bookingId]: reason,
    })
  }

  return (
    <DashboardLayout userName="Provider" profileHref="/provider/profile" bookingsHref="/provider/bookings/history" bookingRequestsHref="/provider/bookings" messagesHref="/messages">
      <section className="space-y-8">
        <header>
          <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
            Provider dashboard
          </p>
          <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
            Incoming booking requests
          </h1>
          <p className="mt-3 max-w-2xl text-muted">
            Review booking requests sent for your services.
          </p>
        </header>

        {bookingMessage && (
          <div className="rounded-2xl border border-line bg-white px-5 py-4 text-sm font-medium text-ink">
            {bookingMessage}
          </div>
        )}

        {error && (
          <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
            {error}
          </p>
        )}

        {isLoading && (
          <div className="rounded-2xl border border-line bg-white p-8 text-center text-muted">
            Loading booking requests...
          </div>
        )}

        {!isLoading && !error && bookings.length === 0 && (
          <div className="rounded-2xl border border-dashed border-line bg-white p-8 text-center">
            <h2 className="text-lg font-semibold text-ink">
              No incoming booking requests.
            </h2>
            <p className="mt-2 text-muted">
              New requests for your services will appear here.
            </p>
          </div>
        )}

        {!isLoading && !error && bookings.length > 0 && (
          <section aria-label="Incoming booking requests">
            <div className="overflow-hidden rounded-3xl border border-line bg-white shadow-sm">
              <div className="grid grid-cols-1 border-b border-line bg-surface px-5 py-3 text-sm font-semibold text-muted md:grid-cols-[1fr_1.2fr_1fr_0.6fr_0.8fr_1fr]">
                <span>Seeker</span>
                <span>Service</span>
                <span>Date and time</span>
                <span>Price</span>
                <span>Status</span>
                <span>Action</span>
              </div>

              <div className="divide-y divide-line">
                {bookings.map((booking) => (
                  <BookingRequestCard
                    booking={booking}
                    declineReason={declineReasons[booking.bookingId] ?? ''}
                    key={booking.bookingId}
                    onAccept={handleAccept}
                    onComplete={handleComplete}
                    onDecline={handleDecline}
                    onDeclineReasonChange={handleDeclineReasonChange}
                  />
                ))}
              </div>
            </div>
          </section>
        )}
      </section>
    </DashboardLayout>
  )
}

export default ProviderBookingRequestsPage
