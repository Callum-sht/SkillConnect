import { useEffect, useState } from 'react'
import {
  getProviderBookingHistory,
  getSeekerBookingHistory,
} from '../services/bookingHistoryService'
import type { BookingHistoryItem } from '../services/bookingHistoryService'

export function useSeekerBookingHistory(userId: number) {
  const [bookings, setBookings] = useState<BookingHistoryItem[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getSeekerBookingHistory(userId)
      .then(setBookings)
      .catch(() => setError('Unable to load your booking history. Please try again.'))
      .finally(() => setIsLoading(false))
  }, [userId])

  return { bookings, isLoading, error }
}

export function useProviderBookingHistory(userId: number) {
  const [bookings, setBookings] = useState<BookingHistoryItem[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getProviderBookingHistory(userId)
      .then(setBookings)
      .catch(() => setError('Unable to load your booking history. Please try again.'))
      .finally(() => setIsLoading(false))
  }, [userId])

  return { bookings, isLoading, error }
}