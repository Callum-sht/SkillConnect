import type { Service } from '../components/ServiceCard'

export type AvailableService = Service & {
  location: string
  description: string
}

export async function getAvailableServices(signal?: AbortSignal): Promise<AvailableService[]> {
  const response = await fetch('/api/services', {
    credentials: 'include',
    headers: { Accept: 'application/json' },
    signal,
  })

  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to load services. Please try again.')
  }

  const services: unknown = await response.json()
  if (!Array.isArray(services)) {
    throw new Error('The service list returned an unexpected response.')
  }

  return services as AvailableService[]
}

export async function getServiceCategories(signal?: AbortSignal): Promise<string[]> {
  const response = await fetch('/api/services/categories', {
    credentials: 'include',
    headers: { Accept: 'application/json' },
    signal,
  })

  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to load categories. Please try again.')
  }

  const categories: unknown = await response.json()
  if (!Array.isArray(categories)) {
    throw new Error('The category list returned an unexpected response.')
  }

  return categories as string[]
}