type BookingRequest = {
  serviceId: number
  bookingDate: string
  bookingTime: string
  bookingEndDate: string
  bookingEndTime: string
}

export async function requestBooking(
  seekerUserId: number,
  booking: BookingRequest,
): Promise<string> {
  try {
    const response = await fetch(`/api/seekers/${seekerUserId}/bookings`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(booking),
    })

    return await response.text()
  } catch {
    return 'Could not connect to server.'
  }
}
