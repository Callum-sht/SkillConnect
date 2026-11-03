import type { ReactNode } from 'react'
import DashboardHeader from './DashboardHeader'

type DashboardLayoutProps = {
  children: ReactNode
  userName: string
  avatarUrl?: string
  profileHref?: string
  bookingsHref?: string
  bookingRequestsHref?: string
  userManagementHref?: string
  serviceManagementHref?: string
  messagesHref?: string
}

function DashboardLayout({
  children,
  userName,
  avatarUrl,
  profileHref,
  bookingsHref,
  bookingRequestsHref,
  messagesHref,
  userManagementHref,
  serviceManagementHref,
}: DashboardLayoutProps) {
  return (
    <div className="min-h-screen bg-surface">
      <DashboardHeader
        avatarUrl={avatarUrl}
        profileHref={profileHref}
        bookingsHref={bookingsHref}
        bookingRequestsHref={bookingRequestsHref}
        messagesHref={messagesHref}
        userManagementHref={userManagementHref}
        serviceManagementHref={serviceManagementHref}
        userName={userName}
      />

      <main className="mx-auto w-full max-w-[1200px] px-4 py-8 sm:px-6">
        {children}
      </main>
    </div>
  )
}

export default DashboardLayout