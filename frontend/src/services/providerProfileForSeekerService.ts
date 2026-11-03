export type ProviderProfileForSeeker = {
  name: string
  bio: string | null
  availability: string | null
  rating: number
  services: {
    id: number
    title: string
    category: string
    price: number
    rating: number
  }[]
}

export async function getProviderProfile(
  providerUserId: number,
): Promise<ProviderProfileForSeeker | null> {
  try {
    const response = await fetch(`/api/providers/${providerUserId}/public-profile`, {
      headers: { Accept: 'application/json' },
    })

    if (!response.ok) {
      return null
    }

    return await response.json()
  } catch {
    return null
  }
}