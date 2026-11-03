import { useEffect, useState } from 'react'
import { startConversation } from '../services/conversationService'
import {
  getServiceDetail,
  getServiceReviews,
  type ServiceDetail,
  type ServiceReview,
} from '../services/serviceDetailService'

function getServiceIdFromPath(): number | null {
  const match = window.location.pathname.match(/^\/services\/(\d+)$/)
  return match ? Number(match[1]) : null
}

function ServiceDetailPage() {
  const serviceId = getServiceIdFromPath()
  const [service, setService] = useState<ServiceDetail | null>(null)
  const [reviews, setReviews] = useState<ServiceReview[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)
  const [isStartingConversation, setIsStartingConversation] = useState(false)
  const [conversationError, setConversationError] = useState<string | null>(
    null,
  )

  useEffect(() => {
    if (serviceId === null) {
      setIsLoading(false)
      setNotFound(true)
      return
    }

    Promise.all([
      getServiceDetail(serviceId),
      getServiceReviews(serviceId),
    ])
      .then(([serviceResult, reviewsResult]) => {
        if (serviceResult === null) {
          setNotFound(true)
        } else {
          setService(serviceResult)
          setReviews(reviewsResult)
        }
      })
      .finally(() => setIsLoading(false))
  }, [serviceId])

  async function handleMessageProvider() {
    if (service === null || isStartingConversation) {
      return
    }

    setIsStartingConversation(true)
    setConversationError(null)

    try {
      const conversation = await startConversation(service.providerUserId)
      window.location.href = `/messages/${conversation.conversationId}`
    } catch (error) {
      setConversationError(
        error instanceof Error
          ? error.message
          : 'Unable to start conversation.',
      )
    } finally {
      setIsStartingConversation(false)
    }
  }

  if (isLoading) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12 text-center text-muted">
        Loading service...
      </div>
    )
  }

  if (notFound || service === null) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12 text-center">
        <h1 className="text-2xl font-bold text-ink">Service not found</h1>
        <p className="mt-2 text-muted">
          This service may have been removed.{' '}
          <a className="text-brand underline" href="/">
            Go back home
          </a>
        </p>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-12">
      <button
        className="text-sm font-semibold text-brand hover:underline"
        onClick={() => window.history.back()}
        type="button"
      >
        Back
      </button>

      <span className="mt-6 inline-block rounded-full bg-surface px-3 py-1.5 text-sm">
        {service.category}
      </span>

      <h1 className="mt-3 text-3xl font-bold text-ink">{service.title}</h1>

      <p className="mt-1 text-muted">
        by{' '}
        <a
          className="font-semibold text-ink hover:text-brand hover:underline"
          href={`/providers/${service.providerUserId}`}
        >
          {service.provider}
        </a>
      </p>

      <div className="mt-4 flex items-center gap-4">
        <span className="font-bold text-amber-500">★ {service.rating}</span>
        <span className="text-muted">{service.location}</span>
      </div>

      <div className="mt-6 rounded-2xl border border-line bg-white p-5">
        <h2 className="font-semibold text-ink">Description</h2>
        <p className="mt-2 text-muted">{service.description}</p>
      </div>

      <div className="mt-6 flex items-center justify-between rounded-2xl border border-line bg-white p-5">
        <span className="text-muted">Hourly rate</span>
        <strong className="text-xl">${service.price}/hr</strong>
      </div>

      <div className="mt-6">
        <button
          className="w-full rounded-xl bg-brand px-5 py-3 font-semibold text-white transition hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={isStartingConversation}
          onClick={handleMessageProvider}
          type="button"
        >
          {isStartingConversation
            ? 'Opening conversation...'
            : 'Message Provider'}
        </button>

        {conversationError && (
          <p className="mt-2 text-sm text-red-600" role="alert">
            {conversationError}
          </p>
        )}
      </div>

      <div className="mt-6 rounded-2xl border border-line bg-white p-5">
        <h2 className="font-semibold text-ink">
          Reviews ({reviews.length})
        </h2>

        {reviews.length === 0 && (
          <p className="mt-2 text-muted">No reviews yet.</p>
        )}

        {reviews.length > 0 && (
          <div className="mt-4 space-y-4">
            {reviews.map((review, index) => (
              <div
                className="border-t border-line pt-4 first:border-t-0 first:pt-0"
                key={index}
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="font-semibold text-ink">
                    {review.seekerName}
                  </span>

                  <span className="font-semibold text-amber-500">
                    {'★'.repeat(review.rating)}
                    {'☆'.repeat(5 - review.rating)}
                  </span>
                </div>

                <p className="mt-1 text-xs text-muted">
                  {new Date(review.createdAt).toLocaleDateString()}
                </p>

                {review.comment && (
                  <p className="mt-2 text-muted">{review.comment}</p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default ServiceDetailPage