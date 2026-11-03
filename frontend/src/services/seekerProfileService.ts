export type SeekerProfile = {
  userId: number
  seekerAccountId: number | null
  name: string
  email: string
  role: string
  status: string
  bio: string | null
  createdAt: string
}

export async function getSeekerProfile(userId: number): Promise<SeekerProfile> {
  const response = await fetch(`/api/seekers/${userId}/profile`, {
    credentials: 'include',
    headers: { Accept: 'application/json' },
  })

  if (response.status === 401) {
    throw new Error('Please sign in to view your profile.')
  }
  if (response.status === 403) {
    throw new Error('This account is not a Seeker account.')
  }
  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to load your profile. Please try again.')
  }

  return response.json() as Promise<SeekerProfile>
}

export async function updateSeekerProfile(
  userId: number,
  changes: { name: string; bio: string },
): Promise<SeekerProfile> {
  const response = await fetch(`/api/seekers/${userId}/profile`, {
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
    throw new Error('Please check your name and bio before saving.')
  }
  if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('Unable to save your profile. Please try again.')
  }

  return response.json() as Promise<SeekerProfile>
}
