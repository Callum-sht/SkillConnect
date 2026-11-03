import type { Service } from '../ServiceCard'

function HomeServiceCard({ service }: { service: Service }) {
  const icon = service.category === 'Tutoring' ? '📚' : service.category === 'Photography' ? '📷' : '💻'
  return (
    <article className="overflow-hidden rounded-[20px] border border-line bg-white shadow-sm transition duration-200 hover:-translate-y-1 hover:shadow-md">
      <div className="relative flex h-44 items-center justify-center overflow-hidden bg-gradient-to-br from-violet-50 to-sky-50">
        <span className="text-5xl" aria-hidden="true">{icon}</span>
        <span className="absolute top-4 left-4 rounded-full bg-white px-3 py-1.5 text-xs font-semibold">{service.category}</span>
      </div>
      <div className="p-5.5">
        <div className="text-sm font-semibold text-amber-500">★ {service.rating}</div>
        <h3 className="mt-2 text-xl font-semibold text-ink"><a className="hover:text-brand hover:underline" href={`/services/${service.id}`}>{service.title}</a></h3>
        <p className="mt-1 text-sm text-muted">by {service.provider}</p>
        <div className="mt-5 flex items-center justify-between border-t border-line pt-4"><span className="text-sm text-muted">From</span><strong>${service.price}<span className="text-sm font-normal text-muted"> /hr</span></strong></div>
      </div>
    </article>
  )
}

type FeaturedServicesProps = {
  services: Service[]
}

function FeaturedServices({ services }: FeaturedServicesProps) {
  return (
    <section className="mx-auto max-w-[1200px] px-4 py-12 sm:px-6 sm:py-16" id="services">
      <div className="mb-8 flex flex-wrap items-end justify-between gap-5">
        <div>
          <p className="font-bold text-brand">Discover services</p>
          <h2 className="mt-2 text-3xl font-bold text-ink sm:text-4xl">Featured services</h2>
        </div>
        <button className="font-bold text-brand hover:text-brand-dark" type="button">
          Browse all →
        </button>
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
        {services.map((service) => (
          <HomeServiceCard key={service.id} service={service} />
        ))}
      </div>
    </section>
  )
}

export default FeaturedServices
