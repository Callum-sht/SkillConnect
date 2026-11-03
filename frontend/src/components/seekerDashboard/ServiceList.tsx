import ServiceCard, { type Service } from '../ServiceCard'

type ServiceListProps = {
  services: Service[]
  isLoading?: boolean
  error?: string | null
  onRequestBooking?: (service: Service) => void
}

function ServiceList({
  services,
  isLoading = false,
  error = null,
  onRequestBooking,
}: ServiceListProps) {
  if (isLoading) {
    return (
      <div className="rounded-2xl border border-line bg-white p-8 text-center text-muted">
        Loading services...
      </div>
    )
  }

  if (error) {
    return (
      <div
        className="rounded-2xl border border-red-200 bg-red-50 p-8 text-center text-red-700"
        role="alert"
      >
        {error}
      </div>
    )
  }

  if (services.length === 0) {
    return (
      <div className="rounded-2xl border border-dashed border-line bg-white p-8 text-center">
        <h2 className="text-lg font-semibold text-ink">No services found</h2>
        <p className="mt-2 text-muted">Try changing your search or filters.</p>
      </div>
    )
  }

  return (
    <section aria-label="Available services">
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
        {services.map((service) => (
          <ServiceCard
            actions={
              onRequestBooking ? (
                <button
                  className="w-full rounded-[10px] bg-brand px-4 py-2.5 font-semibold text-white transition-colors hover:bg-brand-dark focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand"
                  onClick={() => onRequestBooking(service)}
                  type="button"
                >
                  Request booking
                </button>
              ) : undefined
            }
            key={service.id}
            service={service}
          />
        ))}
      </div>
    </section>
  )
}

export default ServiceList
