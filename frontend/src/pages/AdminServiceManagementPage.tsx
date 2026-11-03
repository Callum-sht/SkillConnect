import { useEffect, useState } from 'react'
import DashboardLayout from '../components/layout/DashboardLayout'
import { getAllServices, updateServiceStatus, type AdminServiceListing } from '../services/adminServiceListingService'

function AdminServiceManagementPage() {
  const [services, setServices] = useState<AdminServiceListing[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [actionMessage, setActionMessage] = useState('')
  const [busyServiceId, setBusyServiceId] = useState<number | null>(null)

  useEffect(() => {
    getAllServices()
      .then(setServices)
      .finally(() => setIsLoading(false))
  }, [])

  async function handleStatusChange(
    serviceId: number,
    newStatus: AdminServiceListing['status'],
  ) {
    setBusyServiceId(serviceId)
    const message = await updateServiceStatus(serviceId, newStatus)
    setActionMessage(message)

    if (message === 'Service status updated successfully.') {
      setServices(
        services.map((service) =>
          service.id === serviceId ? { ...service, status: newStatus } : service,
        ),
      )
    }

    setBusyServiceId(null)
  }

  return (
    <DashboardLayout userName="Admin"
    userManagementHref="/admin/users"
    serviceManagementHref="/admin/services">
      <div className="space-y-6">
        <div>
          <p className="text-sm font-semibold uppercase tracking-wide text-brand">
            Administration
          </p>

          <h1 className="mt-1 text-3xl font-bold text-ink">
            Service Listings
          </h1>

          <p className="mt-2 text-gray-600">
            View and manage all service listings on SkillConnect.
          </p>
        </div>

        {actionMessage && (
          <div className="rounded-xl border border-line bg-white px-5 py-3 text-sm font-medium text-ink">
            {actionMessage}
          </div>
        )}

        <section className="overflow-hidden rounded-xl border border-line bg-white">
          <div className="border-b border-line px-6 py-4">
            <h2 className="text-lg font-semibold text-ink">
              All Listings
            </h2>
          </div>

          {isLoading && (
            <p className="p-6 text-center text-gray-600">Loading services...</p>
          )}

          {!isLoading && (
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-surface text-sm text-gray-600">
                  <tr>
                    <th className="px-6 py-3 font-semibold">Title</th>
                    <th className="px-6 py-3 font-semibold">Provider</th>
                    <th className="px-6 py-3 font-semibold">Category</th>
                    <th className="px-6 py-3 font-semibold">Price</th>
                    <th className="px-6 py-3 font-semibold">Status</th>
                    <th className="px-6 py-3 font-semibold">Action</th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-line">
                  {services.map((service) => (
                    <tr key={service.id} className="hover:bg-surface">
                      <td className="px-6 py-4 font-medium text-ink">
                        {service.title}
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        {service.provider}
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        {service.category}
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        ${service.price}/hr
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        {service.status}
                      </td>

                      <td className="px-6 py-4">
                        {service.status === 'REMOVED' ? (
                          <button
                            className="rounded-[10px] border border-green-200 bg-green-50 px-3 py-1.5 text-sm font-semibold text-green-700 transition-colors hover:bg-green-100 disabled:opacity-50"
                            disabled={busyServiceId === service.id}
                            onClick={() => handleStatusChange(service.id, 'ACTIVE')}
                            type="button"
                          >
                            Restore
                          </button>
                        ) : (
                          <button
                            className="rounded-[10px] border border-red-200 bg-red-50 px-3 py-1.5 text-sm font-semibold text-red-700 transition-colors hover:bg-red-100 disabled:opacity-50"
                            disabled={busyServiceId === service.id}
                            onClick={() => handleStatusChange(service.id, 'REMOVED')}
                            type="button"
                          >
                            Remove
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>
    </DashboardLayout>
  )
}

export default AdminServiceManagementPage