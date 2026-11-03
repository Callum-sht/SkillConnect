import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import ProviderBookingRequestsPage from './pages/ProviderBookingRequestsPage'
import ProviderProfilePage from './pages/ProviderProfilePage'
import RequestBookingPage from './pages/RequestBookingPage'
import SeekerDashboard from './pages/SeekerDashboard'
import SeekerProfilePage from './pages/SeekerProfilePage'
import AdminUserManagementPage from './pages/AdminUserManagementPage'
import SeekerBookingHistoryPage from './pages/SeekerBookingHistoryPage'
import ProviderBookingHistoryPage from './pages/ProviderBookingHistoryPage'
import ServiceDetailPage from './pages/ServiceDetailPage'
import SeekerProfileForProviderPage from './pages/SeekerProfileForProviderPage'
import ConversationPage from './pages/ConversationPage'
import ConversationsPage from './pages/ConversationsPage'
import ProviderProfileForSeekerPage from './pages/ProviderProfileForSeekerPage'
import AdminServiceManagementPage from './pages/AdminServiceManagementPage'

function App() {
  const path = window.location.pathname.replace(/\/+$/, '') || '/'

  if (path === '/login') {
    return <LoginPage mode="login" />
  }

  if (path === '/register') {
    return <LoginPage mode="register" />
  }

  if (path === '/provider/bookings') {
    return <ProviderBookingRequestsPage />
  }

  if (path === '/provider/bookings/history') {
    return <ProviderBookingHistoryPage />
  }

  if (path === '/provider/profile') {
    return <ProviderProfilePage />
  }

  if (path === '/seeker/dashboard') {
    return <SeekerDashboard />
  }

  if (path === '/seeker/profile') {
    return <SeekerProfilePage />
  }

  if (path === '/seeker/bookings/new') {
    return <RequestBookingPage />
  }

  if (path === '/admin/users') {
    return <AdminUserManagementPage />
  }

  if (path === '/admin/services') {
    return <AdminServiceManagementPage />
  }

  if (path === '/seeker/bookings') {
    return <SeekerBookingHistoryPage />
  }

  if (/^\/services\/\d+$/.test(path)) {
    return <ServiceDetailPage />
  }

  if (path === '/messages') {
    return <ConversationsPage />
  }
  
  if (/^\/messages\/\d+$/.test(path)) {
    return <ConversationPage />
  }

  if (/^\/provider\/seekers\/\d+$/.test(path)) {
    return <SeekerProfileForProviderPage />
  }

  if (/^\/providers\/\d+$/.test(path)) {
    return <ProviderProfileForSeekerPage />
  }

  return <HomePage />
}

export default App