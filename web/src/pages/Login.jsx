import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api'
import Logo from '../components/Logo'

const inputClass =
  'w-full rounded-xl border border-zinc-200 bg-white px-4 py-3 text-sm outline-none transition focus:border-emerald-500 focus:ring-4 focus:ring-emerald-500/15 dark:border-zinc-800 dark:bg-zinc-950'

export default function Login({ mode = 'login' }) {
  const isSignup = mode === 'signup'
  const [fullName, setFullName] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  const submit = async e => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const resp = isSignup
        ? await api.post('/signup', { fullName, username, password })
        : await api.post('/login', { username, password })
      const token = resp.data?.token
      if (token) {
        localStorage.setItem('token', token)
        navigate('/dashboard')
      } else {
        setError(resp.data?.message || 'Unexpected response from the server.')
      }
    } catch (err) {
      setError(err.response?.data?.message?.replace(/^Error\s*:\s*/, '') || err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center p-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center gap-4 text-center">
          <Logo size="lg" />
          <div className="space-y-1">
            <h1 className="text-2xl font-semibold tracking-tight">
              {isSignup ? 'Create your account' : 'Welcome back'}
            </h1>
            <p className="text-sm text-zinc-500 dark:text-zinc-400">
              {isSignup ? 'Start tracking where your money goes.' : 'Sign in to your expense tracker.'}
            </p>
          </div>
        </div>

        <form
          onSubmit={submit}
          className="space-y-4 rounded-2xl border border-zinc-200 bg-white p-6 shadow-sm dark:border-zinc-800 dark:bg-zinc-900"
        >
          {isSignup && (
            <label className="block space-y-1.5">
              <span className="text-sm font-medium">Full name</span>
              <input
                value={fullName}
                onChange={e => setFullName(e.target.value)}
                required
                autoComplete="name"
                className={inputClass}
              />
            </label>
          )}
          <label className="block space-y-1.5">
            <span className="text-sm font-medium">Username</span>
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              autoComplete="username"
              className={inputClass}
            />
          </label>
          <label className="block space-y-1.5">
            <span className="text-sm font-medium">Password</span>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              minLength={isSignup ? 8 : undefined}
              autoComplete={isSignup ? 'new-password' : 'current-password'}
              className={inputClass}
            />
          </label>

          {error && (
            <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700 dark:bg-red-500/10 dark:text-red-400">
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? 'Please wait…' : isSignup ? 'Create account' : 'Sign in'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-zinc-500 dark:text-zinc-400">
          {isSignup ? 'Already have an account? ' : 'New here? '}
          <Link
            to={isSignup ? '/login' : '/signup'}
            className="font-medium text-emerald-700 hover:underline dark:text-emerald-400"
          >
            {isSignup ? 'Sign in' : 'Create an account'}
          </Link>
        </p>
      </div>
    </main>
  )
}
