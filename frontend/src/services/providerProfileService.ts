export type ProviderProfile = {
  userId: number
  providerAccountId: number | null
  name: string
  email: string
  role: string
  status: string
  bio: string | null
  availability: string | null
  portfolio: string | null
  location: string | null
  rating: number | null
  createdAt: string
}
 
export type ProviderServiceListing = {
  id: number
  title: string
  provider: string
  category: string
  location: string
  description: string
  price: number
  rating: number | null
}
 
export type AddServiceInput = {
  title: string
  category: string
  location: string
  description: string
  hourlyRate: number
}
 
function ensureJson(response: Response, fallbackMessage: string) {
  if (
    !response.ok ||
    !response.headers.get('content-type')?.includes('application/json')
  ) {
    throw new Error(fallbackMessage)
  }
}
 
export async function getProviderProfile(
  userId: number,
): Promise<ProviderProfile> {
  const response = await fetch(`/api/providers/${userId}/profile`, {
    credentials: 'include',
    headers: { Accept: 'application/json' },
  })
 
  if (response.status === 401) {
    throw new Error('Please sign in to view your profile.')
  }
  if (response.status === 403) {
    throw new Error('This account is not a Provider account.')
  }
  ensureJson(response, 'Unable to load your profile. Please try again.')
 
  return response.json() as Promise<ProviderProfile>
}
 
export async function updateProviderProfile(
  userId: number,
  changes: {
    name?: string
    bio?: string
    availability?: string
    portfolio?: string
    location?: string
  },
): Promise<ProviderProfile> {
  const response = await fetch(`/api/providers/${userId}/profile`, {
    method: 'PATCH',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(changes),
  })
 
  if (response.status === 401) {
    throw new Error('Please sign in again to update your profile.')
  }
  if (response.status === 403) {
    throw new Error('You cannot update this profile.')
  }
  if (response.status === 400) {
    throw new Error('Please check your details before saving.')
  }
  ensureJson(response, 'Unable to save your profile. Please try again.')
 
  return response.json() as Promise<ProviderProfile>
}
 
export async function getProviderServices(
  userId: number,
): Promise<ProviderServiceListing[]> {
  const response = await fetch(`/api/providers/${userId}/services`, {
    credentials: 'include',
    headers: { Accept: 'application/json' },
  })
 
  if (response.status === 401) {
    throw new Error('Please sign in to view your services.')
  }
  if (response.status === 403) {
    throw new Error('You cannot view these services.')
  }
  ensureJson(response, 'Unable to load your services. Please try again.')
 
  return response.json() as Promise<ProviderServiceListing[]>
}
 
export async function addProviderService(
  userId: number,
  service: AddServiceInput,
): Promise<ProviderServiceListing> {
  const response = await fetch(`/api/providers/${userId}/services`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(service),
  })
 
  if (response.status === 401) {
    throw new Error('Please sign in again to add a service.')
  }
  if (response.status === 403) {
    throw new Error('You cannot add services to this account.')
  }
  if (response.status === 400) {
    throw new Error('Please fill in all service details before saving.')
  }
  ensureJson(response, 'Unable to add the service. Please try again.')
 
  return response.json() as Promise<ProviderServiceListing>
}
 
export async function deleteProviderService(
  userId: number,
  serviceId: number,
): Promise<void> {
  const response = await fetch(
    `/api/providers/${userId}/services/${serviceId}`,
    {
      method: 'DELETE',
      credentials: 'include',
    },
  )
 
  if (response.status === 401) {
    throw new Error('Please sign in again to delete a service.')
  }
  if (response.status === 403) {
    throw new Error('You can only delete your own services.')
  }
  if (response.status === 400) {
    throw new Error('This service could not be deleted.')
  }
  if (!response.ok) {
    throw new Error('Unable to delete the service. Please try again.')
  }
}
 