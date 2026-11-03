import type { ProviderBookingRequest } from '../../services/providerBookingService'
import BookingStatusBadge from './BookingStatusBadge'

type BookingRequestCardProps = {
  booking: ProviderBookingRequest
  onAccept: (bookingId: number) => void
  onComplete: (bookingId: number) => void
  onDecline: (bookingId: number) => void
  onDeclineReasonChange: (bookingId: number, reason: string) => void
  declineReason: string
}

function formatBookingDate(value: string) {
  return new Intl.DateTimeFormat('en-AU', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

function BookingRequestCard({
  booking,
  onAccept,
  onComplete,
  onDecline,
  onDeclineReasonChange,
  declineReason,
}: BookingRequestCardProps) {
  return (
    <article className="grid grid-cols-1 gap-3 px-5 py-4 md:grid-cols-[1fr_1.2fr_1fr_0.6fr_0.8fr_1fr] md:items-center">
      <div>
        <span className="block text-xs font-semibold uppercase text-muted md:hidden">
          Seeker
        </span>
        <a
          className="font-semibold text-ink hover:text-brand hover:underline"
          href={`/provider/seekers/${booking.seekerUserId}`}
        >
          {booking.seekerName}
        </a>
      </div>

      <div>
        <span className="block text-xs font-semibold uppercase text-muted md:hidden">
          Service
        </span>
        <span className="text-ink">{booking.serviceName}</span>
      </div>

      <div>
        <span className="block text-xs font-semibold uppercase text-muted md:hidden">
          Date and time
        </span>
        <span className="text-muted">
          {formatBookingDate(booking.bookingDate)}
        </span>
      </div>

      <div>
        <span className="block text-xs font-semibold uppercase text-muted md:hidden">
          Price
        </span>
        <span className="text-muted">${booking.price}</span>
      </div>

      <div>
        <span className="block text-xs font-semibold uppercase text-muted md:hidden">
          Status
        </span>
        <BookingStatusBadge status={booking.status} />
      </div>

      <div>
        <span className="block text-xs font-semibold uppercase text-muted md:hidden">
          Action
        </span>
        {booking.status === 'PENDING' ? (
          <div className="space-y-2">
            <textarea
              className="min-h-18 w-full rounded-xl border border-line bg-white px-3 py-2 text-sm text-ink outline-none transition placeholder:text-muted focus:border-brand focus:ring-2 focus:ring-brand/15"
              onChange={(event) =>
                onDeclineReasonChange(booking.bookingId, event.target.value)
              }
              placeholder="Optional decline reason"
              value={declineReason}
            />
            <div className="flex flex-wrap gap-2">
              <button
                className="rounded-[10px] bg-brand px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-brand-dark focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand"
                onClick={() => onAccept(booking.bookingId)}
                type="button"
              >
                Accept
              </button>
              <button
                className="rounded-[10px] border border-red-200 bg-red-50 px-4 py-2 text-sm font-semibold text-red-700 transition-colors hover:bg-red-100 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-400"
                onClick={() => onDecline(booking.bookingId)}
                type="button"
              >
                Decline
              </button>
            </div>
          </div>
        ) : booking.status === 'ACCEPTED' ? (
          <button
            className="rounded-[10px] bg-brand px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-brand-dark focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand"
            onClick={() => onComplete(booking.bookingId)}
            type="button"
          >
            Mark as Completed
          </button>
        ) : (
          <span className="text-sm text-muted">Not available</span>
        )}
      </div>

      {booking.status === 'DECLINED' && booking.declineReason && (
        <div className="md:col-span-6">
          <span className="block text-xs font-semibold uppercase text-muted">
            Decline reason
          </span>
          <p className="mt-1 text-sm text-muted">{booking.declineReason}</p>
        </div>
      )}
    </article>
  )
}

export default BookingRequestCard