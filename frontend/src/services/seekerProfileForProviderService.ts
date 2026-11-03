export type SeekerProfileForProvider = {
  name: string
  bio: string | null
  completedBookingsCount: number
}

export async function getSeekerProfileForProvider(
  providerUserId: number,
  seekerUserId: number,
): Promise<SeekerProfileForProvider | null> {
  try {
    const response = await fetch(
      `/api/providers/${providerUserId}/seekers/${seekerUserId}/profile`,
      {
        credentials: 'include',
        headers: { Accept: 'application/json' },
      },
    )

    if (!response.ok) {
      return null
    }

    return await response.json()
  } catch {
    return null
  }
}