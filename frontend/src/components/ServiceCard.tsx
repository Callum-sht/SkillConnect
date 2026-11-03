import type { ReactNode } from 'react'

export type Service = {
  id: number
  title: string
  provider: string
  category: string
  price: number
  rating: number
}

type ServiceCardProps = {
  service: Service
  actions?: ReactNode
}

function ServiceCard({ service, actions }: ServiceCardProps) {
  return (
    <article className="overflow-hidden rounded-[20px] border border-line bg-white transition-transform hover:-translate-y-1">
      <div className="h-50 bg-surface p-4">
        <span className="inline-block rounded-full bg-white px-3 py-1.5 text-sm">
          {service.category}
        </span>
      </div>

      <div className="p-5.5">
        <div className="font-bold text-amber-500">★ {service.rating}</div>
        <h3 className="mt-2 text-xl font-semibold text-ink">
          <a className="hover:text-brand hover:underline" href={`/services/${service.id}`}>
            {service.title}
          </a>
        </h3>
        <p className="mt-1 text-muted">by {service.provider}</p>

        <div className="mt-6 flex items-center justify-between">
          <span>From</span>
          <strong>${service.price}/hr</strong>
        </div>

        {actions && (
          <div className="mt-5 flex flex-wrap gap-2 border-t border-line pt-5">
            {actions}
          </div>
        )}
      </div>
    </article>
  )
}

export default ServiceCard
