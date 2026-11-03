import { useEffect, useState } from 'react'
import DashboardLayout from '../components/layout/DashboardLayout'
import { getAllUsers, updateUserStatus, type AdminUser } from '../services/adminService'

function AdminUserManagementPage() {
  const [users, setUsers] = useState<AdminUser[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [actionMessage, setActionMessage] = useState('')
  const [busyUserId, setBusyUserId] = useState<number | null>(null)

  useEffect(() => {
    getAllUsers()
      .then(setUsers)
      .finally(() => setIsLoading(false))
  }, [])

  async function handleStatusChange(
    userId: number,
    newStatus: AdminUser['status'],
  ) {
    setBusyUserId(userId)
    setActionMessage('')

    const updatedUser = await updateUserStatus(userId, newStatus)

    if (updatedUser === null) {
      setActionMessage('Unable to update this user. Admin accounts cannot be deactivated.')
    } else {
      setUsers(users.map((user) => (user.userId === userId ? updatedUser : user)))
      setActionMessage('User status updated successfully.')
    }

    setBusyUserId(null)
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
            User Management
          </h1>

          <p className="mt-2 text-gray-600">
            View and manage registered SkillConnect users.
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
              Registered Users
            </h2>
          </div>

          {isLoading && (
            <p className="p-6 text-center text-gray-600">Loading users...</p>
          )}

          {!isLoading && (
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-surface text-sm text-gray-600">
                  <tr>
                    <th className="px-6 py-3 font-semibold">Name</th>
                    <th className="px-6 py-3 font-semibold">Email</th>
                    <th className="px-6 py-3 font-semibold">Role</th>
                    <th className="px-6 py-3 font-semibold">Status</th>
                    <th className="px-6 py-3 font-semibold">Action</th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-line">
                  {users.map((user) => (
                    <tr key={user.userId} className="hover:bg-surface">
                      <td className="px-6 py-4 font-medium text-ink">
                        {user.name}
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        {user.email}
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        {user.role}
                      </td>

                      <td className="px-6 py-4 text-gray-600">
                        {user.status}
                      </td>

                      <td className="px-6 py-4">
                        {user.status === 'ACTIVE' ? (
                          <button
                            className="rounded-[10px] border border-red-200 bg-red-50 px-3 py-1.5 text-sm font-semibold text-red-700 transition-colors hover:bg-red-100 disabled:opacity-50"
                            disabled={busyUserId === user.userId}
                            onClick={() => handleStatusChange(user.userId, 'SUSPENDED')}
                            type="button"
                          >
                            Suspend
                          </button>
                        ) : (
                          <button
                            className="rounded-[10px] border border-green-200 bg-green-50 px-3 py-1.5 text-sm font-semibold text-green-700 transition-colors hover:bg-green-100 disabled:opacity-50"
                            disabled={busyUserId === user.userId}
                            onClick={() => handleStatusChange(user.userId, 'ACTIVE')}
                            type="button"
                          >
                            Reactivate
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

export default AdminUserManagementPage