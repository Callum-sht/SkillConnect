import { useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { login, register } from '../services/authService'
import type { UserRole } from '../services/authService'

type LoginPageProps = {
  mode: 'login' | 'register'
}

type FormValues = {
  fullName: string
  email: string
  password: string
  confirmPassword: string
  role: UserRole
}

type FieldName = keyof FormValues
type FormErrors = Partial<Record<FieldName, string>>

const inputClass =
  'min-h-12 w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-base text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-violet-600 focus:ring-4 focus:ring-violet-100 aria-[invalid=true]:border-red-600 aria-[invalid=true]:bg-red-50 disabled:cursor-not-allowed disabled:opacity-60'

const labelClass = 'mb-2 block text-sm font-semibold text-slate-900'

const errorClass = 'mt-1.5 text-xs leading-5 text-red-700'

export default function LoginPage({ mode }: LoginPageProps) {
  const isRegister = mode === 'register'
  const submitting = useRef(false)

  const [values, setValues] = useState<FormValues>({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'SEEKER',
  })

  const [errors, setErrors] = useState<FormErrors>({})
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [serverError, setServerError] = useState('')
  const [success, setSuccess] = useState(false)

  function updateField(name: FieldName, value: string) {
    setValues((current) => ({
      ...current,
      [name]: value,
    }))

    setErrors((current) => ({
      ...current,
      [name]: undefined,
    }))

    setServerError('')
  }

  function validate(): FormErrors {
    const nextErrors: FormErrors = {}

    if (isRegister && !values.fullName.trim()) {
      nextErrors.fullName = 'Please enter your full name.'
    }

    if (!values.email.trim()) {
      nextErrors.email = 'Please enter your email address.'
    } else if (
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email.trim())
    ) {
      nextErrors.email = 'Please enter a valid email address.'
    }

    if (!values.password) {
      nextErrors.password = 'Please enter your password.'
    } else if (isRegister && values.password.length < 8) {
      nextErrors.password = 'Use at least 8 characters.'
    }

    if (isRegister) {
      if (!values.confirmPassword) {
        nextErrors.confirmPassword = 'Please confirm your password.'
      } else if (values.password !== values.confirmPassword) {
        nextErrors.confirmPassword = 'Your passwords do not match.'
      }
    }

    return nextErrors
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (submitting.current) {
      return
    }

    const form = event.currentTarget
    const nextErrors = validate()

    setErrors(nextErrors)
    setServerError('')

    const firstInvalidField = Object.keys(nextErrors)[0]

    if (firstInvalidField) {
      const input = form.elements.namedItem(firstInvalidField)

      if (input instanceof HTMLElement) {
        input.focus()
      }

      return
    }

    submitting.current = true
    setLoading(true)

    try {
      if (isRegister) {
        await register({
          fullName: values.fullName.trim(),
          email: values.email.trim(),
          password: values.password,
          role: values.role,
        })
      } else {
        const result = await login({
          email: values.email.trim(),
          password: values.password,
        })

        if (result.role === 'SEEKER') {
          window.location.href = '/seeker/dashboard'
          return
        }

        if (result.role === 'PROVIDER') {
          window.location.href = '/provider/profile'
          return
        }

        if (result.role === 'ADMIN') {
          window.location.href = '/admin/users'
          return
        }
      }

      setValues((current) => ({
        ...current,
        password: '',
        confirmPassword: '',
      }))

      setSuccess(true)
    } catch (error) {
      setServerError(
        error instanceof Error
          ? error.message
          : 'Something went wrong. Please try again.',
      )
    } finally {
      submitting.current = false
      setLoading(false)
    }
  }

  return (
    <div className="min-h-dvh bg-slate-50 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex min-h-19 max-w-6xl items-center justify-between gap-4 px-4 py-4 sm:px-6">
          <a
            className="rounded text-xl font-bold tracking-tight text-violet-600 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-100 sm:text-2xl"
            href="/"
            aria-label="SkillConnect home"
          >
            SkillConnect
          </a>

          <a
            className="rounded text-xs font-medium text-slate-600 transition hover:text-violet-600 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-100 sm:text-sm"
            href="/"
          >
            Back to home
          </a>
        </div>
      </header>

      <main className="px-4 py-8 sm:py-14">
        <section
          className="mx-auto w-full max-w-md rounded-3xl border border-slate-200 bg-white p-6 shadow-xl shadow-violet-950/5 sm:p-9"
          aria-labelledby="login-page-title"
        >
          {success ? (
            <div className="text-center" role="status">
              <span
                className="mx-auto mb-5 grid size-14 place-items-center rounded-full bg-emerald-100 text-3xl text-emerald-700"
                aria-hidden="true"
              >
                ✓
              </span>

              <h1
                id="login-page-title"
                className="mb-3 text-3xl font-bold tracking-tight"
              >
                {isRegister ? 'Account created' : 'You’re signed in'}
              </h1>

              <p className="text-sm leading-7 text-slate-600">
                {isRegister
                  ? 'Your account is ready. Sign in to get started.'
                  : 'Welcome back to SkillConnect.'}
              </p>

              <a
                className="mt-6 flex min-h-12 w-full items-center justify-center rounded-xl bg-violet-600 px-4 py-3 font-semibold text-white transition hover:bg-violet-700 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-200"
                href={isRegister ? '/login' : '/'}
              >
                {isRegister ? 'Continue to sign in' : 'Back to home'}
              </a>
            </div>
          ) : (
            <>
              <div className="mb-7 text-center">
                <span className="text-xs font-bold tracking-widest text-violet-600">
                  YOUR LOCAL SKILLS COMMUNITY
                </span>

                <h1
                  id="login-page-title"
                  className="my-3 text-3xl font-bold tracking-tight sm:text-4xl"
                >
                  {isRegister ? 'Create your account' : 'Welcome back'}
                </h1>

                <p className="text-sm leading-7 text-slate-600">
                  {isRegister
                    ? 'Find local expertise or share your skills with others.'
                    : 'Sign in to manage your services, bookings, and profile.'}
                </p>
              </div>

              <form
                noValidate
                onSubmit={handleSubmit}
                aria-busy={loading}
              >
                {serverError && (
                  <div
                    className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm leading-6 text-red-800"
                    role="alert"
                  >
                    {serverError}
                  </div>
                )}

                <fieldset
                  className="m-0 min-w-0 border-0 p-0"
                  disabled={loading}
                >
                  {isRegister && (
                    <>
                      <div className="mb-5">
                        <label className={labelClass} htmlFor="fullName">
                          Full name
                        </label>

                        <input
                          className={inputClass}
                          id="fullName"
                          name="fullName"
                          autoComplete="name"
                          placeholder="Your full name"
                          value={values.fullName}
                          onChange={(event) =>
                            updateField('fullName', event.target.value)
                          }
                          required
                          aria-invalid={Boolean(errors.fullName)}
                          aria-describedby={
                            errors.fullName ? 'fullName-error' : undefined
                          }
                        />

                        {errors.fullName && (
                          <p id="fullName-error" className={errorClass}>
                            {errors.fullName}
                          </p>
                        )}
                      </div>

                      <div className="mb-6">
                        <p className={labelClass}>I want to</p>

                        <div
                          className="grid grid-cols-1 gap-3 sm:grid-cols-2"
                          role="radiogroup"
                          aria-label="Account role"
                        >
                          <label
                            className={`flex cursor-pointer items-center gap-3 rounded-xl border p-3.5 transition focus-within:ring-4 focus-within:ring-violet-100 ${values.role === 'SEEKER'
                                ? 'border-violet-600 bg-violet-50'
                                : 'border-slate-300 bg-white hover:border-violet-300'
                              }`}
                          >
                            <input
                              className="size-4 accent-violet-600"
                              type="radio"
                              name="role"
                              value="SEEKER"
                              checked={values.role === 'SEEKER'}
                              onChange={() =>
                                updateField('role', 'SEEKER')
                              }
                            />

                            <span>
                              <strong className="block text-sm font-semibold">
                                Find services
                              </strong>

                              <small className="mt-1 block text-xs text-slate-600">
                                Join as a Seeker
                              </small>
                            </span>
                          </label>

                          <label
                            className={`flex cursor-pointer items-center gap-3 rounded-xl border p-3.5 transition focus-within:ring-4 focus-within:ring-violet-100 ${values.role === 'PROVIDER'
                                ? 'border-violet-600 bg-violet-50'
                                : 'border-slate-300 bg-white hover:border-violet-300'
                              }`}
                          >
                            <input
                              className="size-4 accent-violet-600"
                              type="radio"
                              name="role"
                              value="PROVIDER"
                              checked={values.role === 'PROVIDER'}
                              onChange={() =>
                                updateField('role', 'PROVIDER')
                              }
                            />

                            <span>
                              <strong className="block text-sm font-semibold">
                                Offer services
                              </strong>

                              <small className="mt-1 block text-xs text-slate-600">
                                Join as a Provider
                              </small>
                            </span>
                          </label>
                        </div>
                      </div>
                    </>
                  )}

                  <div className="mb-5">
                    <label className={labelClass} htmlFor="email">
                      Email address
                    </label>

                    <input
                      className={inputClass}
                      id="email"
                      name="email"
                      type="email"
                      autoComplete="email"
                      placeholder="you@example.com"
                      value={values.email}
                      onChange={(event) =>
                        updateField('email', event.target.value)
                      }
                      required
                      aria-invalid={Boolean(errors.email)}
                      aria-describedby={
                        errors.email ? 'email-error' : undefined
                      }
                    />

                    {errors.email && (
                      <p id="email-error" className={errorClass}>
                        {errors.email}
                      </p>
                    )}
                  </div>

                  <div className="mb-5">
                    <div className="mb-2 flex items-center justify-between">
                      <label
                        className="block text-sm font-semibold"
                        htmlFor="password"
                      >
                        Password
                      </label>

                      {!isRegister && (
                        <a
                          className="rounded text-xs font-semibold text-violet-600 hover:underline focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-100"
                          href="/forgot-password"
                        >
                          Forgot password?
                        </a>
                      )}
                    </div>

                    <div className="relative">
                      <input
                        className={`${inputClass} pr-20`}
                        id="password"
                        name="password"
                        type={showPassword ? 'text' : 'password'}
                        autoComplete={
                          isRegister ? 'new-password' : 'current-password'
                        }
                        placeholder={
                          isRegister
                            ? 'Create a password'
                            : 'Enter your password'
                        }
                        value={values.password}
                        onChange={(event) =>
                          updateField('password', event.target.value)
                        }
                        required
                        aria-invalid={Boolean(errors.password)}
                        aria-describedby={
                          isRegister
                            ? 'password-hint password-error'
                            : 'password-error'
                        }
                      />

                      <button
                        className="absolute right-1 top-1/2 min-h-10 -translate-y-1/2 rounded-lg px-3 text-xs font-semibold text-violet-600 transition hover:bg-violet-50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-violet-200"
                        type="button"
                        onClick={() =>
                          setShowPassword((current) => !current)
                        }
                        aria-label={
                          showPassword ? 'Hide passwords' : 'Show passwords'
                        }
                        aria-pressed={showPassword}
                      >
                        {showPassword ? 'Hide' : 'Show'}
                      </button>
                    </div>

                    {isRegister && (
                      <p
                        id="password-hint"
                        className="mt-1.5 text-xs leading-5 text-slate-600"
                      >
                        Use at least 8 characters.
                      </p>
                    )}

                    {errors.password && (
                      <p id="password-error" className={errorClass}>
                        {errors.password}
                      </p>
                    )}
                  </div>

                  {isRegister && (
                    <div className="mb-5">
                      <label
                        className={labelClass}
                        htmlFor="confirmPassword"
                      >
                        Confirm password
                      </label>

                      <input
                        className={inputClass}
                        id="confirmPassword"
                        name="confirmPassword"
                        type={showPassword ? 'text' : 'password'}
                        autoComplete="new-password"
                        placeholder="Re-enter your password"
                        value={values.confirmPassword}
                        onChange={(event) =>
                          updateField(
                            'confirmPassword',
                            event.target.value,
                          )
                        }
                        required
                        aria-invalid={Boolean(errors.confirmPassword)}
                        aria-describedby={
                          errors.confirmPassword
                            ? 'confirmPassword-error'
                            : undefined
                        }
                      />

                      {errors.confirmPassword && (
                        <p
                          id="confirmPassword-error"
                          className={errorClass}
                        >
                          {errors.confirmPassword}
                        </p>
                      )}
                    </div>
                  )}

                  <button
                    className="flex min-h-12 w-full items-center justify-center rounded-xl bg-violet-600 px-4 py-3 font-semibold text-white transition hover:bg-violet-700 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-200 disabled:cursor-wait disabled:opacity-60"
                    type="submit"
                    disabled={loading}
                  >
                    {loading
                      ? isRegister
                        ? 'Creating account…'
                        : 'Signing in…'
                      : isRegister
                        ? 'Create account'
                        : 'Sign in'}
                  </button>
                </fieldset>
              </form>

              <p className="mt-6 border-t border-slate-200 pt-5 text-center text-sm leading-7 text-slate-600">
                {isRegister
                  ? 'Already have an account? '
                  : 'New to SkillConnect? '}

                <a
                  className="rounded font-semibold text-violet-600 hover:underline focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-100"
                  href={isRegister ? '/login' : '/register'}
                >
                  {isRegister ? 'Sign in' : 'Create an account'}
                </a>
              </p>
            </>
          )}
        </section>
      </main>
    </div>
  )
}
