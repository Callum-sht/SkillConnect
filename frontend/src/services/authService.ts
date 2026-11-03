export type UserRole = 'PROVIDER' | 'SEEKER'

export type LoginInput = {
  email: string
  password: string
}

export type RegisterInput = LoginInput & {
  fullName: string
  role: UserRole
}

export type LoginResponse = {
  userId: number
  role: UserRole
}

const userIdKey = 'authenticatedUserId'

export function getCurrentUserId(): number | null {
  const stored = sessionStorage.getItem(userIdKey)
  const userId = Number(stored)
  return stored && Number.isSafeInteger(userId) && userId > 0 ? userId : null
}

async function postAuth(
  path: string,
  payload: LoginInput | RegisterInput,
): Promise<Response> {
  let response: Response

  try {
    response = await fetch(`/api/auth/${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(payload),
    })
  } catch {
    throw new Error(
      'Unable to connect. Please check your connection and try again.',
    )
  }

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error('Email or password is incorrect.')
    }

    if (response.status === 409 && path === 'register') {
      throw new Error(
        'This email already exists. Please sign in.',
      )
    }

    if (response.status === 400) {
      throw new Error('Please check your details and try again.')
    }

    if (response.status === 403 && path === 'login') {
      throw new Error(
        'Your account has been disabled. Please contact support.',
      )
    }

    if (response.status === 403) {
      throw new Error(
        'This request could not be verified. Please refresh and try again.',
      )
    }

    if (response.status === 404 || response.status === 405) {
      throw new Error(
        'The authentication service is not available yet. Please try again later.',
      )
    }

    throw new Error('Something went wrong. Please try again later.')
  }

  return response
}

export async function login(
  input: LoginInput,
): Promise<LoginResponse> {
  const response = await postAuth('login', input)
  const result = await response.json() as LoginResponse
  if (!Number.isSafeInteger(result.userId) || result.userId <= 0) {
    throw new Error('The authentication service returned an unexpected response.')
  }

  sessionStorage.setItem(userIdKey, String(result.userId))
  return result
}

export async function register(
  input: RegisterInput,
): Promise<void> {
  await postAuth('register', input)
}

export async function logout(): Promise<void> {
  try {
    const response = await fetch('/api/auth/logout', {
      method: 'POST',
      credentials: 'include',
    })
    if (!response.ok) {
      throw new Error('Logout failed.')
    }
  } catch {
    throw new Error('Unable to sign out. Please try again.')
  }

  sessionStorage.removeItem(userIdKey)
}
