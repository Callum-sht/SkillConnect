export type ServiceDetail = {
  id: number
  title: string
  provider: string
  providerUserId: number
  category: string
  location: string
  description: string
  price: number
  rating: number
}

export type ServiceReview = {
  seekerName: string
  rating: number
  comment: string
  createdAt: string
}

export async function getServiceDetail(
  serviceId: number,
): Promise<ServiceDetail | null> {
  try {
    const response = await fetch(`/api/services/${serviceId}`, {
      headers: { Accept: 'application/json' },
    })

    if (response.status === 404) {
      return null
    }

    if (!response.ok) {
      throw new Error('Unable to load this service.')
    }

    return await response.json()
  } catch {
    return null
  }
}

export async function getServiceReviews(
  serviceId: number,
): Promise<ServiceReview[]> {
  try {
    const response = await fetch(`/api/services/${serviceId}/reviews`, {
      headers: { Accept: 'application/json' },
    })

    if (!response.ok) {
      return []
    }

    return await response.json()
  } catch {
    return []
  }
}