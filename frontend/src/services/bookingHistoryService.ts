export type BookingStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'CANCELLED' | 'COMPLETED'

export type BookingHistoryItem = {
  bookingId: number
  serviceTitle: string
  otherPartyName: string
  bookingDate: string
  bookingEndTime: string | null
  price: number
  status: BookingStatus
  declineReason: string | null
}

export async function getSeekerBookingHistory(
  userId: number,
  signal?: AbortSignal,
): Promise<BookingHistoryItem[]> {
  const response = await fetch(`/api/seekers/${userId}/bookings`, {
    credentials: 'include',
    headers: { Accept: 'application/json' },
    signal,
  })

  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to load your booking history. Please try again.')
  }

  const bookings: unknown = await response.json()
  if (!Array.isArray(bookings)) {
    throw new Error('The booking history returned an unexpected response.')
  }

  return bookings as BookingHistoryItem[]
}

export async function getProviderBookingHistory(
  userId: number,
  signal?: AbortSignal,
): Promise<BookingHistoryItem[]> {
  const response = await fetch(`/api/providers/${userId}/bookings`, {
    credentials: 'include',
    headers: { Accept: 'application/json' },
    signal,
  })

  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to load your booking history. Please try again.')
  }

  const bookings: unknown = await response.json()
  if (!Array.isArray(bookings)) {
    throw new Error('The booking history returned an unexpected response.')
  }

  return bookings as BookingHistoryItem[]
}
