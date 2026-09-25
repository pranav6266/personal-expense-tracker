import { INCOME, formatAmount } from '../lib/format'

const formatDate = iso =>
  new Date(`${iso}T00:00:00`).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })

export default function ExpenseList({ expenses = [], onDelete, loading }) {
  const sorted = [...expenses].sort((a, b) => (b.date || '').localeCompare(a.date || '') || b.id - a.id)

  return (
    <div>
      <h2 className="text-base font-semibold">Transactions</h2>

      {loading ? (
        <p className="mt-6 text-sm text-zinc-500 dark:text-zinc-400">Loading…</p>
      ) : sorted.length === 0 ? (
        <p className="mt-6 rounded-xl border border-dashed border-zinc-300 px-4 py-8 text-center text-sm text-zinc-500 dark:border-zinc-700 dark:text-zinc-400">
          No transactions yet. Add your first one above.
        </p>
      ) : (
        <ul className="mt-4 divide-y divide-zinc-100 dark:divide-zinc-800">
          {sorted.map(exp => {
            const isIncome = exp.expenseType === INCOME
            return (
              <li key={exp.id} className="flex items-center gap-4 py-3">
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">{exp.note || exp.category}</p>
                  <p className="truncate text-xs text-zinc-500 dark:text-zinc-400">
                    {formatDate(exp.date)} · {exp.category} · {exp.account}
                  </p>
                </div>
                <span
                  className={`shrink-0 text-sm font-semibold tabular-nums ${
                    isIncome ? 'text-emerald-700 dark:text-emerald-400' : ''
                  }`}
                >
                  {isIncome ? '+' : '−'}
                  {formatAmount(exp.amount)}
                </span>
                <button
                  onClick={() => onDelete?.(exp.id)}
                  aria-label={`Delete ${exp.note || exp.category} on ${exp.date}`}
                  className="shrink-0 rounded-md p-1.5 text-zinc-400 transition hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6" />
                  </svg>
                </button>
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}
