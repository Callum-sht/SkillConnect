import { useState } from 'react'
import { logout } from '../../services/authService'

type DashboardHeaderProps = {
  userName: string
  avatarUrl?: string
  profileHref?: string
  bookingsHref?: string
  bookingRequestsHref?: string
  userManagementHref?: string
  serviceManagementHref?: string
  messagesHref?: string
}

function getInitials(name: string) {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
}

function DashboardHeader({
  userName,
  avatarUrl,
  profileHref = '#profile',
  bookingsHref,
  bookingRequestsHref,
  messagesHref,
  userManagementHref,
  serviceManagementHref,
}: DashboardHeaderProps) {
  const [logoutError, setLogoutError] = useState('')
  const [loggingOut, setLoggingOut] = useState(false)

  async function handleLogout() {
    if (loggingOut) return
    setLoggingOut(true)
    setLogoutError('')

    try {
      await logout()
      window.location.href = '/login'
    } catch {
      setLogoutError('Unable to sign out. Please try again.')
      setLoggingOut(false)
    }
  }

  return (
    <header className="sticky top-0 z-50 border-b border-line bg-white">
      <div className="mx-auto flex min-h-18 max-w-[1200px] items-center justify-between px-4 sm:px-6">
        <a className="text-2xl font-extrabold text-brand" href="/">
          SkillConnect
        </a>

        <details className="group relative">
          <summary
            aria-label={`Open ${userName}'s profile menu`}
            className="flex cursor-pointer list-none items-center gap-3 rounded-full p-1 outline-none transition hover:bg-surface focus-visible:ring-2 focus-visible:ring-brand [&::-webkit-details-marker]:hidden"
          >
            <span className="hidden font-medium text-ink sm:inline">{userName}</span>

            {avatarUrl ? (
              <img
                alt={`${userName}'s avatar`}
                className="size-10 rounded-full object-cover"
                src={avatarUrl}
              />
            ) : (
              <span
                aria-hidden="true"
                className="grid size-10 place-items-center rounded-full bg-brand font-bold text-white"
              >
                {getInitials(userName)}
              </span>
            )}
          </summary>

          <div className="absolute right-0 mt-2 w-52 rounded-xl border border-line bg-white p-2 shadow-lg">
            <p className="border-b border-line px-3 py-2 font-semibold text-ink">
              {userName}
            </p>
            <a
              className="mt-1 block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-surface hover:text-brand"
              href={profileHref}
            >
              View profile
            </a>
            {bookingRequestsHref && (
              <a
                className="mt-1 block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-surface hover:text-brand"
                href={bookingRequestsHref}
              >
                Booking requests
              </a>
            )}
            {bookingsHref && (
              <a
                className="mt-1 block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-surface hover:text-brand"
                href={bookingsHref}
              >
                My bookings
              </a>
            )}

            {messagesHref && (
              <a
                className="mt-1 block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-surface hover:text-brand"
                href={messagesHref}
              >
                Messages
              </a>
            )}

            {userManagementHref && (
              <a
                className="mt-1 block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-surface hover:text-brand"
                href={userManagementHref}
              >
                Manage users
              </a>
            )}
            {serviceManagementHref && (
              <a
                className="mt-1 block rounded-lg px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-surface hover:text-brand"
                href={serviceManagementHref}
              >
                Manage services
              </a>
            )}
            <button
              className="mt-1 block w-full rounded-lg px-3 py-2 text-left text-sm font-medium text-red-600 transition-colors hover:bg-red-50 hover:text-red-700 disabled:opacity-50"
              type="button"
              disabled={loggingOut}
              onClick={handleLogout}
            >
              {loggingOut ? 'Signing out...' : 'Logout'}
            </button>
            {logoutError && <p role="alert" className="px-3 py-2 text-xs text-red-600">{logoutError}</p>}
          </div>
        </details>
      </div>
    </header>
  )
}

export default DashboardHeader