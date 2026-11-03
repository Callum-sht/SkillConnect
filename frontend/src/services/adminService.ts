export type AdminUser = {
  userId: number
  name: string
  email: string
  role: 'ADMIN' | 'PROVIDER' | 'SEEKER'
  status: 'ACTIVE' | 'SUSPENDED' | 'DEACTIVATED'
  createdAt: string
}

export async function getAllUsers(): Promise<AdminUser[]> {
  try {
    const response = await fetch('/api/admin/users', {
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

export async function updateUserStatus(
  userId: number,
  status: AdminUser['status'],
): Promise<AdminUser | null> {
  try {
    const response = await fetch(`/api/admin/users/${userId}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ status }),
    })

    if (!response.ok) {
      return null
    }

    return await response.json()
  } catch {
    return null
  }
}