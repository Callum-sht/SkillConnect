import { useState } from 'react'
import BookingStatusBadge from '../components/booking/BookingStatusBadge'
import DashboardLayout from '../components/layout/DashboardLayout'
import { requestBooking } from '../services/bookingService'
import { useSeekerProfile } from '../hooks/useSeekerProfile'
import { useServiceListings } from '../hooks/useServiceListings'

type FormErrors = {
  serviceId?: string
  bookingDate?: string
  bookingTime?: string
  bookingEndDate?: string
  bookingEndTime?: string
}

const inputClass =
  'min-h-12 w-full rounded-xl border border-line bg-white px-4 py-3 text-base text-ink outline-none transition focus:border-brand focus:ring-4 focus:ring-brand/10 aria-[invalid=true]:border-red-600 aria-[invalid=true]:bg-red-50'

const labelClass = 'mb-2 block text-sm font-semibold text-ink'

function getTodayValue() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

function getServiceIdFromUrl() {
  const params = new URLSearchParams(window.location.search)
  return params.get('serviceId') ?? ''
}

function isPastDateTime(date: string, time: string) {
  return new Date(`${date}T${time}`) <= new Date()
}

function isEndAfterStart(
  startDate: string,
  startTime: string,
  endDate: string,
  endTime: string,
) {
  return new Date(`${endDate}T${endTime}`) > new Date(`${startDate}T${startTime}`)
}

function RequestBookingPage() {
  const { profile, isLoading } = useSeekerProfile()
  const { services, isLoading: servicesLoading, error: servicesError } = useServiceListings()
  const initialServiceId = getServiceIdFromUrl()
  const [serviceId, setServiceId] = useState(initialServiceId)
  const [bookingDate, setBookingDate] = useState('')
  const [bookingTime, setBookingTime] = useState('')
  const [bookingEndDate, setBookingEndDate] = useState('')
  const [bookingEndTime, setBookingEndTime] = useState('')
  const [errors, setErrors] = useState<FormErrors>({})
  const [submitted, setSubmitted] = useState(false)
  const [requestMessage, setRequestMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const selectedService = services.find(
    (service) => String(service.id) === serviceId,
  )

  function validate() {
    const nextErrors: FormErrors = {}
    const today = getTodayValue()

    if (!selectedService) {
      nextErrors.serviceId = 'Please select a service.'
    }

    if (!bookingDate) {
      nextErrors.bookingDate = 'Please select a booking date.'
    } else if (bookingDate < today) {
      nextErrors.bookingDate = 'Please select today or a future date.'
    }

    if (!bookingTime) {
      nextErrors.bookingTime = 'Please select a booking time.'
    }

    if (!bookingEndDate) {
      nextErrors.bookingEndDate = 'Please select an end date.'
    }

    if (!bookingEndTime) {
      nextErrors.bookingEndTime = 'Please select an end time.'
    }

    if (
      bookingDate &&
      bookingTime &&
      !nextErrors.bookingDate &&
      isPastDateTime(bookingDate, bookingTime)
    ) {
      nextErrors.bookingTime = 'Please select a future time.'
    }

    if (
      bookingDate &&
      bookingTime &&
      bookingEndDate &&
      bookingEndTime &&
      !isEndAfterStart(bookingDate, bookingTime, bookingEndDate, bookingEndTime)
    ) {
      nextErrors.bookingEndTime = 'Please select an end time after the start time.'
    }

    return nextErrors
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const nextErrors = validate()
    setErrors(nextErrors)
    setSubmitted(false)
    setRequestMessage('')

    if (Object.keys(nextErrors).length > 0) {
      return
    }

    if (!profile) {
      setRequestMessage('Please sign in to request a booking.')
      return
    }

    setIsSubmitting(true)

    const message = await requestBooking(profile.userId, {
      serviceId: Number(serviceId),
      bookingDate,
      bookingTime,
      bookingEndDate,
      bookingEndTime,
    })

    const wasSuccessful = message === 'Booking request submitted successfully.'
    setRequestMessage(wasSuccessful ? '' : message)
    setSubmitted(wasSuccessful)
    setIsSubmitting(false)
  }

  return (
    <DashboardLayout userName={profile?.name ?? (isLoading ? 'Loading...' : 'Guest')} profileHref="/seeker/profile" bookingsHref="/seeker/bookings" messagesHref="/messages">
      <section className="space-y-8">
        <header className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.18em] text-brand">
              Seeker dashboard
            </p>
            <h1 className="mt-2 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
              Request a booking
            </h1>
            <p className="mt-3 max-w-2xl text-muted">
              Choose a service and preferred time to send a booking request.
            </p>
          </div>

          <a
            className="rounded-xl border border-line bg-white px-4 py-2.5 text-sm font-semibold text-ink transition hover:border-brand/40 hover:text-brand"
            href="/seeker/dashboard"
          >
            Back to dashboard
          </a>
        </header>

        <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_minmax(18rem,0.55fr)]">
          <form
            className="rounded-3xl border border-line bg-white p-6 shadow-sm sm:p-8"
            noValidate
            onSubmit={handleSubmit}
          >
            {requestMessage && (
              <div
                className="mb-6 rounded-2xl border border-brand/20 bg-brand/5 px-5 py-4 text-sm font-medium text-brand-dark"
                role="status"
              >
                {requestMessage}
              </div>
            )}

            {submitted && selectedService && (
              <div
                className="mb-6 rounded-2xl border border-brand/20 bg-brand/5 px-5 py-4 text-sm font-medium text-brand-dark"
                role="status"
              >
                Booking request created for {selectedService.title}. Status:
                PENDING.
              </div>
            )}

            <div className="mb-5">
              <label className={labelClass} htmlFor="serviceId">
                Selected service
              </label>
              <select
                aria-describedby={errors.serviceId ? 'serviceId-error' : undefined}
                aria-invalid={Boolean(errors.serviceId)}
                className={inputClass}
                id="serviceId"
                name="serviceId"
                disabled={servicesLoading || Boolean(servicesError)}
                onChange={(event) => {
                  setServiceId(event.target.value)
                  setSubmitted(false)
                  setRequestMessage('')
                  setErrors((current) => ({ ...current, serviceId: undefined }))
                }}
                required
                value={serviceId}
              >
                <option value="">Select a service</option>
                {services.map((service) => (
                  <option key={service.id} value={service.id}>
                    {service.title} by {service.provider}
                  </option>
                ))}
              </select>
              {servicesError && <p role="alert" className="mt-2 text-sm text-red-700">{servicesError}</p>}
              {errors.serviceId && (
                <p className="mt-1.5 text-xs leading-5 text-red-700" id="serviceId-error">
                  {errors.serviceId}
                </p>
              )}
            </div>

            <div className="grid gap-5 sm:grid-cols-2">
              <div>
                <label className={labelClass} htmlFor="bookingDate">
                  Booking date
                </label>
                <input
                  aria-describedby={
                    errors.bookingDate ? 'bookingDate-error' : undefined
                  }
                  aria-invalid={Boolean(errors.bookingDate)}
                  className={inputClass}
                  id="bookingDate"
                  min={getTodayValue()}
                  name="bookingDate"
                  onChange={(event) => {
                    setBookingDate(event.target.value)
                    setSubmitted(false)
                    setRequestMessage('')
                    setErrors((current) => ({
                      ...current,
                      bookingDate: undefined,
                      bookingTime: undefined,
                      bookingEndDate: undefined,
                      bookingEndTime: undefined,
                    }))
                  }}
                  required
                  type="date"
                  value={bookingDate}
                />
                {errors.bookingDate && (
                  <p
                    className="mt-1.5 text-xs leading-5 text-red-700"
                    id="bookingDate-error"
                  >
                    {errors.bookingDate}
                  </p>
                )}
              </div>

              <div>
                <label className={labelClass} htmlFor="bookingTime">
                  Booking time
                </label>
                <input
                  aria-describedby={
                    errors.bookingTime ? 'bookingTime-error' : undefined
                  }
                  aria-invalid={Boolean(errors.bookingTime)}
                  className={inputClass}
                  id="bookingTime"
                  name="bookingTime"
                  onChange={(event) => {
                    setBookingTime(event.target.value)
                    setSubmitted(false)
                    setRequestMessage('')
                    setErrors((current) => ({
                      ...current,
                      bookingTime: undefined,
                      bookingEndTime: undefined,
                    }))
                  }}
                  required
                  type="time"
                  value={bookingTime}
                />
                {errors.bookingTime && (
                  <p
                    className="mt-1.5 text-xs leading-5 text-red-700"
                    id="bookingTime-error"
                  >
                    {errors.bookingTime}
                  </p>
                )}
              </div>
            </div>

            <div className="mt-5 grid gap-5 sm:grid-cols-2">
              <div>
                <label className={labelClass} htmlFor="bookingEndDate">
                  End date
                </label>
                <input
                  aria-describedby={
                    errors.bookingEndDate ? 'bookingEndDate-error' : undefined
                  }
                  aria-invalid={Boolean(errors.bookingEndDate)}
                  className={inputClass}
                  id="bookingEndDate"
                  min={bookingDate || getTodayValue()}
                  name="bookingEndDate"
                  onChange={(event) => {
                    setBookingEndDate(event.target.value)
                    setSubmitted(false)
                    setRequestMessage('')
                    setErrors((current) => ({
                      ...current,
                      bookingEndDate: undefined,
                      bookingEndTime: undefined,
                    }))
                  }}
                  required
                  type="date"
                  value={bookingEndDate}
                />
                {errors.bookingEndDate && (
                  <p
                    className="mt-1.5 text-xs leading-5 text-red-700"
                    id="bookingEndDate-error"
                  >
                    {errors.bookingEndDate}
                  </p>
                )}
              </div>

              <div>
                <label className={labelClass} htmlFor="bookingEndTime">
                  End time
                </label>
                <input
                  aria-describedby={
                    errors.bookingEndTime ? 'bookingEndTime-error' : undefined
                  }
                  aria-invalid={Boolean(errors.bookingEndTime)}
                  className={inputClass}
                  id="bookingEndTime"
                  name="bookingEndTime"
                  onChange={(event) => {
                    setBookingEndTime(event.target.value)
                    setSubmitted(false)
                    setRequestMessage('')
                    setErrors((current) => ({
                      ...current,
                      bookingEndTime: undefined,
                    }))
                  }}
                  required
                  type="time"
                  value={bookingEndTime}
                />
                {errors.bookingEndTime && (
                  <p
                    className="mt-1.5 text-xs leading-5 text-red-700"
                    id="bookingEndTime-error"
                  >
                    {errors.bookingEndTime}
                  </p>
                )}
              </div>
            </div>

            <button
              className="mt-7 w-full rounded-[10px] bg-brand px-4 py-3 font-semibold text-white transition-colors hover:bg-brand-dark focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand"
              disabled={isSubmitting || isLoading || servicesLoading || Boolean(servicesError)}
              type="submit"
            >
              {isSubmitting ? 'Submitting...' : 'Submit booking request'}
            </button>
          </form>

          <aside className="rounded-3xl border border-line bg-white p-6 shadow-sm sm:p-8">
            <h2 className="text-xl font-bold text-ink">Request summary</h2>
            {selectedService ? (
              <dl className="mt-6 divide-y divide-line">
                <div className="pb-4">
                  <dt className="text-sm font-medium text-muted">Service</dt>
                  <dd className="mt-1 font-semibold text-ink">
                    {selectedService.title}
                  </dd>
                </div>
                <div className="py-4">
                  <dt className="text-sm font-medium text-muted">Provider</dt>
                  <dd className="mt-1 font-semibold text-ink">
                    {selectedService.provider}
                  </dd>
                </div>
                <div className="py-4">
                  <dt className="text-sm font-medium text-muted">Category</dt>
                  <dd className="mt-1 font-semibold text-ink">
                    {selectedService.category}
                  </dd>
                </div>
                <div className="py-4">
                  <dt className="text-sm font-medium text-muted">Price</dt>
                  <dd className="mt-1 font-semibold text-ink">
                    ${selectedService.price}/hr
                  </dd>
                </div>
                <div className="pt-4">
                  <dt className="text-sm font-medium text-muted">Status</dt>
                  <dd className="mt-2">
                    <BookingStatusBadge status="PENDING" />
                  </dd>
                </div>
              </dl>
            ) : (
              <p className="mt-4 text-muted">
                Select a service to see the request details.
              </p>
            )}
          </aside>
        </div>
      </section>
    </DashboardLayout>
  )
}

export default RequestBookingPage
