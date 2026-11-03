import { useEffect, useState } from 'react'
import { getCurrentUserId } from '../services/authService'
import { getSeekerProfile, updateSeekerProfile } from '../services/seekerProfileService'
import type { SeekerProfile } from '../services/seekerProfileService'

export function useSeekerProfile() {
  const userId = getCurrentUserId()
  const [profile, setProfile] = useState<SeekerProfile | null>(null)
  const [isLoading, setIsLoading] = useState(userId !== null)
  const [error, setError] = useState<string | null>(
    userId === null ? 'Please sign in to view your profile.' : null,
  )

  useEffect(() => {
    if (userId === null) return
    let active = true

    getSeekerProfile(userId)
      .then((result) => {
        if (active) setProfile(result)
      })
      .catch((cause: unknown) => {
        if (active) setError(cause instanceof Error ? cause.message : 'Unable to load your profile.')
      })
      .finally(() => {
        if (active) setIsLoading(false)
      })

    return () => { active = false }
  }, [userId])

  async function saveProfile(changes: { name: string; bio: string }) {
    if (userId === null) {
      throw new Error('Please sign in to update your profile.')
    }

    const updated = await updateSeekerProfile(userId, changes)
    setProfile(updated)
  }

  return { profile, isLoading, error, saveProfile }
}
