import type { BookingStatus } from '../../services/providerBookingService'

type BookingStatusBadgeProps = {
  status: BookingStatus
}

function BookingStatusBadge({ status }: BookingStatusBadgeProps) {
  let className =
    'inline-flex rounded-full px-3 py-1 text-xs font-semibold bg-brand/10 text-brand-dark'

  if (status === 'ACCEPTED' || status === 'COMPLETED') {
    className =
      'inline-flex rounded-full px-3 py-1 text-xs font-semibold bg-green-100 text-green-700'
  }

  if (status === 'DECLINED' || status === 'CANCELLED') {
    className =
      'inline-flex rounded-full px-3 py-1 text-xs font-semibold bg-red-100 text-red-700'
  }

  return <span className={className}>{status}</span>
}

export default BookingStatusBadge
