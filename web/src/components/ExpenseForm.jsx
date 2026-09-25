import { useState } from 'react'
import api from '../api'
import { EXPENSE, INCOME } from '../lib/format'

const today = () => new Date().toISOString().slice(0, 10)

const field =
  'w-full rounded-lg border border-zinc-200 bg-white px-3 py-2 text-sm outline-none transition focus:border-emerald-500 focus:ring-4 focus:ring-emerald-500/15 dark:border-zinc-800 dark:bg-zinc-950'

export default function ExpenseForm({ onAdded }) {
  const [expenseType, setExpenseType] = useState(EXPENSE)
  const [date, setDate] = useState(today)
  const [amount, setAmount] = useState('')
  const [category, setCategory] = useState('')
  const [account, setAccount] = useState('Cash')
  const [note, setNote] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const submit = async e => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      await api.post('/expenses', {
        expenseType,
        date,
        amount: parseFloat(amount),
        category: category.trim(),
        account: account.trim(),
        note: note.trim(),
      })
      setAmount('')
      setNote('')
      onAdded?.()
    } catch (err) {
      setError(err.response?.data?.message || err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between gap-4">
        <h2 className="text-base font-semibold">Add a transaction</h2>
        <div role="radiogroup" aria-label="Type" className="inline-flex rounded-lg bg-zinc-100 p-1 text-sm dark:bg-zinc-800">
          {[
            [EXPENSE, 'Expense'],
            [INCOME, 'Income'],
          ].map(([value, label]) => (
            <button
              key={value}
              type="button"
              role="radio"
              aria-checked={expenseType === value}
              onClick={() => setExpenseType(value)}
              className={`rounded-md px-3 py-1 font-medium transition ${
                expenseType === value
                  ? 'bg-white shadow-sm dark:bg-zinc-950'
                  : 'text-zinc-500 hover:text-zinc-900 dark:text-zinc-400 dark:hover:text-zinc-100'
              }`}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      <form onSubmit={submit} className="mt-5 grid gap-4 sm:grid-cols-2">
        <label className="space-y-1.5">
          <span className="text-sm font-medium">Amount (₹)</span>
          <input type="number" inputMode="decimal" min="0.01" step="0.01" value={amount} onChange={e => setAmount(e.target.value)} required className={field} />
        </label>
        <label className="space-y-1.5">
          <span className="text-sm font-medium">Date</span>
          <input type="date" value={date} onChange={e => setDate(e.target.value)} required className={field} />
        </label>
        <label className="space-y-1.5">
          <span className="text-sm font-medium">Category</span>
          <input value={category} onChange={e => setCategory(e.target.value)} required placeholder="Groceries" className={field} />
        </label>
        <label className="space-y-1.5">
          <span className="text-sm font-medium">Account</span>
          <input value={account} onChange={e => setAccount(e.target.value)} required placeholder="Cash, UPI, Card…" className={field} />
        </label>
        <label className="space-y-1.5 sm:col-span-2">
          <span className="text-sm font-medium">Note <span className="font-normal text-zinc-400">(optional)</span></span>
          <input value={note} onChange={e => setNote(e.target.value)} className={field} />
        </label>

        {error && (
          <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700 sm:col-span-2 dark:bg-red-500/10 dark:text-red-400">
            {error}
          </p>
        )}

        <div className="sm:col-span-2">
          <button
            type="submit"
            disabled={loading}
            className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-500 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? 'Saving…' : `Add ${expenseType === INCOME ? 'income' : 'expense'}`}
          </button>
        </div>
      </form>
    </div>
  )
}
