export type AdminServiceListing = {
  id: number
  title: string
  provider: string
  category: string
  location: string
  status: 'ACTIVE' | 'INACTIVE' | 'REMOVED'
  price: number
}

export async function getAllServices(): Promise<AdminServiceListing[]> {
  try {
    const response = await fetch('/api/admin/services', {
      credentials: 'include',
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

export async function updateServiceStatus(
  serviceId: number,
  status: AdminServiceListing['status'],
): Promise<string> {
  try {
    const response = await fetch(`/api/admin/services/${serviceId}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ status }),
    })

    return await response.text()
  } catch {
    return 'Could not connect to server.'
  }
}