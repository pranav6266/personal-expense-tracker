import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'
import Logo from '../components/Logo'
import ExpenseForm from '../components/ExpenseForm'
import ExpenseList from '../components/ExpenseList'
import CategoryBreakdown from '../components/CategoryBreakdown'
import SummaryCards from '../components/SummaryCards'

const panel = 'rounded-2xl border border-zinc-200 bg-white p-5 shadow-sm sm:p-6 dark:border-zinc-800 dark:bg-zinc-900'

export default function Dashboard() {
  const [expenses, setExpenses] = useState([])
  const [loading, setLoading] = useState(true)
  const [user, setUser] = useState(null)
  const navigate = useNavigate()

  const signOut = useCallback(() => {
    localStorage.removeItem('token')
    navigate('/login')
  }, [navigate])

  const fetchExpenses = useCallback(async () => {
    setLoading(true)
    try {
      const resp = await api.get('/expenses')
      setExpenses(resp.data || [])
    } catch (err) {
      if (err.response?.status === 401 || err.response?.status === 403) {
        signOut()
      } else {
        console.error(err)
      }
    } finally {
      setLoading(false)
    }
  }, [signOut])

  useEffect(() => {
    if (!localStorage.getItem('token')) {
      navigate('/login')
      return
    }

    const validate = async () => {
      try {
        const resp = await api.get('/auth/validate')
        setUser(resp.data)
        fetchExpenses()
      } catch {
        signOut()
      }
    }
    validate()
  }, [navigate, fetchExpenses, signOut])

  const handleDelete = async id => {
    try {
      await api.delete(`/expenses/${id}`)
      setExpenses(prev => prev.filter(e => e.id !== id))
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-10 border-b border-zinc-200 bg-white/80 backdrop-blur dark:border-zinc-800 dark:bg-zinc-950/80">
        <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6">
          <div className="flex items-center gap-3">
            <Logo />
            <span className="text-lg font-semibold tracking-tight">Expense Tracker</span>
          </div>
          <div className="flex items-center gap-3">
            {user && (
              <span className="hidden text-sm text-zinc-500 sm:inline dark:text-zinc-400">
                {user.username}
              </span>
            )}
            <button
              onClick={signOut}
              className="rounded-lg px-3 py-2 text-sm font-medium transition hover:bg-zinc-100 dark:hover:bg-zinc-800"
            >
              Sign out
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl space-y-6 px-4 py-8 sm:px-6">
        <SummaryCards expenses={expenses} loading={loading} />

        <div className="grid items-start gap-6 lg:grid-cols-3">
          <div className="space-y-6 lg:col-span-2">
            <section className={panel}>
              <ExpenseForm onAdded={fetchExpenses} />
            </section>
            <section className={panel}>
              <ExpenseList expenses={expenses} onDelete={handleDelete} loading={loading} />
            </section>
          </div>
          <aside className={`${panel} lg:sticky lg:top-24`}>
            <CategoryBreakdown expenses={expenses} />
          </aside>
        </div>
      </main>
    </div>
  )
}
