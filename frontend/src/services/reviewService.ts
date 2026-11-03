export async function submitReview(
  seekerUserId: number,
  bookingId: number,
  rating: number,
  comment: string,
): Promise<string> {
  try {
    const response = await fetch(
      `/api/seekers/${seekerUserId}/bookings/${bookingId}/review`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ rating, comment }),
      },
    )

    return await response.text()
  } catch {
    return 'Could not connect to server.'
  }
}