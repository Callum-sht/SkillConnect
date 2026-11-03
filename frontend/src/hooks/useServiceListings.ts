import { useEffect, useState } from 'react'
import { getAvailableServices } from '../services/serviceListingService'
import type { AvailableService } from '../services/serviceListingService'

export function useServiceListings() {
  const [services, setServices] = useState<AvailableService[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getAvailableServices()
      .then(setServices)
      .catch(() => setError('Unable to load services. Please try again.'))
      .finally(() => setIsLoading(false))
  }, [])

  return { services, isLoading, error }
}
