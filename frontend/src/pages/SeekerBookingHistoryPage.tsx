import { useEffect, useState } from 'react'
import DashboardLayout from '../components/layout/DashboardLayout'
import { useSeekerBookingHistory } from '../hooks/useBookingHistory'
import { getCurrentUserId } from '../services/authService'
import { getSeekerProfile } from '../services/seekerProfileService'
import { submitReview } from '../services/reviewService'

function formatStatus(status: string) {
  return status.charAt(0) + status.slice(1).toLowerCase()
}

function SeekerBookingHistoryPage() {
  const userId = getCurrentUserId()

  if (userId === null) {
    return (
      <DashboardLayout userName="Guest" profileHref="/seeker/profile" bookingsHref="/seeker/bookings/his">
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
          Please sign in to view your booking history. <a className="underline" href="/login">Sign in</a>
        </p>
      </DashboardLayout>
    )
  }

  return <SeekerBookingHistoryContent userId={userId} />
}

function SeekerBookingHistoryContent({ userId }: { userId: number }) {
  const { bookings, isLoading, error } = useSeekerBookingHistory(userId)
  const [seekerName, setSeekerName] = useState('My Account')
  const [reviewingBookingId, setReviewingBookingId] = useState<number | null>(null)
  const [reviewRating, setReviewRating] = useState(0)
  const [reviewComment, setReviewComment] = useState('')
  const [reviewMessage, setReviewMessage] = useState('')
  const [reviewedBookingIds, setReviewedBookingIds] = useState<number[]>([])

  useEffect(() => {
    getSeekerProfile(userId)
      .then((profile) => {
        if (profile) {
          setSeekerName(profile.name)
        }
      })
      .catch(() => {})
  }, [userId])

  function startReview(bookingId: number) {
    setReviewingBookingId(bookingId)
    setReviewRating(0)
    setReviewComment('')
    setReviewMessage('')
  }

  function cancelReview() {
    setReviewingBookingId(null)
  }

  async function handleSubmitReview(bookingId: number) {
    if (reviewRating < 1 || reviewRating > 5) {
      setReviewMessage('Please select a rating between 1 and 5 stars.')
      return
    }

    const message = await submitReview(userId, bookingId, reviewRating, reviewComment)
    setReviewMessage(message)

    if (message === 'Review submitted successfully.') {
      setReviewedBookingIds([...reviewedBookingIds, bookingId])
      setReviewingBookingId(null)
    }
  }

  return (
    <DashboardLayout userName={seekerName} profileHref="/seeker/profile" bookingsHref="/seeker/bookings" messagesHref="/messages">
      <section className="space-y-8">
        <header className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
              Seeker dashboard
            </p>
            <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
              My bookings
            </h1>
            <p className="mt-3 max-w-2xl text-muted">
              View your current and previous service bookings.
            </p>
          </div>
          <a
            href="/seeker/dashboard"
            className="rounded-xl border border-line bg-white px-4 py-2.5 text-sm font-semibold text-ink transition hover:border-brand/40 hover:text-brand"
          >
            Back to dashboard
          </a>
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
                <p className="mt-1 text-sm text-muted">Provider: {booking.otherPartyName}</p>
                <p className="mt-1 text-sm text-muted">
                  {new Date(booking.bookingDate).toLocaleString()}
                </p>
                {booking.declineReason && (
                  <p className="mt-2 text-sm text-red-700">
                    Reason: {booking.declineReason}
                  </p>
                )}

                {booking.status === 'COMPLETED' &&
                  !reviewedBookingIds.includes(booking.bookingId) &&
                  reviewingBookingId !== booking.bookingId && (
                    <button
                      className="mt-3 rounded-[10px] bg-brand px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-brand-dark"
                      onClick={() => startReview(booking.bookingId)}
                      type="button"
                    >
                      Leave a review
                    </button>
                  )}

                {reviewedBookingIds.includes(booking.bookingId) && (
                  <p className="mt-3 text-sm font-medium text-green-700">
                    Thank you for your review!
                  </p>
                )}

                {reviewingBookingId === booking.bookingId && (
                  <div className="mt-4 rounded-xl border border-line bg-surface p-4">
                    <p className="text-sm font-semibold text-ink">Your rating</p>
                    <div className="mt-2 flex gap-1">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <button
                          aria-label={`${star} star${star === 1 ? '' : 's'}`}
                          className={`text-2xl ${star <= reviewRating ? 'text-amber-500' : 'text-gray-300'}`}
                          key={star}
                          onClick={() => setReviewRating(star)}
                          type="button"
                        >
                          ★
                        </button>
                      ))}
                    </div>

                    <label className="mt-3 block text-sm font-semibold text-ink" htmlFor={`comment-${booking.bookingId}`}>
                      Comment (optional)
                    </label>
                    <textarea
                      className="mt-1 min-h-20 w-full rounded-xl border border-line bg-white px-3 py-2 text-sm text-ink outline-none transition placeholder:text-muted focus:border-brand focus:ring-2 focus:ring-brand/15"
                      id={`comment-${booking.bookingId}`}
                      onChange={(event) => setReviewComment(event.target.value)}
                      placeholder="Share your experience..."
                      value={reviewComment}
                    />

                    {reviewMessage && (
                      <p className="mt-2 text-sm font-medium text-red-700">{reviewMessage}</p>
                    )}

                    <div className="mt-3 flex gap-2">
                      <button
                        className="rounded-[10px] bg-brand px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-brand-dark"
                        onClick={() => handleSubmitReview(booking.bookingId)}
                        type="button"
                      >
                        Submit review
                      </button>
                      <button
                        className="rounded-[10px] border border-line bg-white px-4 py-2 text-sm font-semibold text-ink transition-colors hover:bg-surface"
                        onClick={cancelReview}
                        type="button"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>
    </DashboardLayout>
  )
}

export default SeekerBookingHistoryPage