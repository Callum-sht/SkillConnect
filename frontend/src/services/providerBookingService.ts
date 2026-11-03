export type BookingStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'DECLINED'
  | 'CANCELLED'
  | 'COMPLETED'

export type ProviderBookingRequest = {
  bookingId: number
  seekerName: string
  seekerUserId: number
  serviceName: string
  bookingDate: string
  bookingEndTime: string | null
  price: number
  status: BookingStatus
  declineReason?: string
}

type BookingResponseFromServer = {
  bookingId: number
  serviceTitle: string
  otherPartyName: string
  otherPartyUserId: number
  bookingDate: string
  bookingEndTime: string | null
  price: number
  status: BookingStatus
  declineReason: string | null
}

export async function getIncomingBookingRequests(
  providerUserId: number,
): Promise<ProviderBookingRequest[]> {
  const response = await fetch(`/api/providers/${providerUserId}/bookings`, {
    credentials: 'include',
    headers: { Accept: 'application/json' },
  })

  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to load booking requests. Please try again.')
  }

  const bookings: unknown = await response.json()
  if (!Array.isArray(bookings)) {
    throw new Error('The booking requests returned an unexpected response.')
  }

  return (bookings as BookingResponseFromServer[]).map((booking) => ({
    bookingId: booking.bookingId,
    seekerName: booking.otherPartyName,
    seekerUserId: booking.otherPartyUserId,
    serviceName: booking.serviceTitle,
    bookingDate: booking.bookingDate,
    bookingEndTime: booking.bookingEndTime,
    price: booking.price,
    status: booking.status,
    declineReason: booking.declineReason ?? undefined,
  }))
}

export async function declineBooking(
  providerId: number,
  bookingId: number,
  declineReason: string,
): Promise<string> {
  try {
    const response = await fetch(
      `/api/providers/${providerId}/bookings/${bookingId}/decline`,
      {
        method: 'PATCH',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ declineReason }),
      },
    )

    return await response.text()
  } catch {
    return 'Could not connect to server.'
  }
}

export async function acceptBooking(
  providerId: number,
  bookingId: number,
): Promise<string> {
  try {
    const response = await fetch(
      `/api/providers/${providerId}/bookings/${bookingId}/accept`,
      {
        method: 'PATCH',
        credentials: 'include',
      },
    )

    return await response.text()
  } catch {
    return 'Could not connect to server.'
  }
}

export async function completeBooking(
  providerId: number,
  bookingId: number,
): Promise<string> {
  try {
    const response = await fetch(
      `/api/providers/${providerId}/bookings/${bookingId}/complete`,
      {
        method: 'PATCH',
        credentials: 'include',
      },
    )

    return await response.text()
  } catch {
    return 'Could not connect to server.'
  }
}
