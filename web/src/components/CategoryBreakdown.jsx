import { EXPENSE, currentMonthPrefix, formatAmount } from '../lib/format'

const MAX_ROWS = 6

// Ranked horizontal bars: one hue (magnitude), values labeled directly.
export default function CategoryBreakdown({ expenses = [] }) {
  const month = currentMonthPrefix()
  const sums = new Map()
  for (const e of expenses) {
    if (e.expenseType !== EXPENSE || !e.date?.startsWith(month)) continue
    const key = e.category?.trim() || 'Uncategorized'
    sums.set(key, (sums.get(key) || 0) + Number(e.amount || 0))
  }

  let rows = [...sums.entries()].sort((a, b) => b[1] - a[1])
  if (rows.length > MAX_ROWS) {
    const other = rows.slice(MAX_ROWS - 1).reduce((sum, [, v]) => sum + v, 0)
    rows = [...rows.slice(0, MAX_ROWS - 1), ['Other', other]]
  }
  const total = rows.reduce((sum, [, v]) => sum + v, 0)
  const max = rows[0]?.[1] || 0

  return (
    <div>
      <h2 className="text-base font-semibold">Spending by category</h2>
      <p className="mt-0.5 text-sm text-zinc-500 dark:text-zinc-400">This month</p>

      {rows.length === 0 ? (
        <p className="mt-6 rounded-xl border border-dashed border-zinc-300 px-4 py-8 text-center text-sm text-zinc-500 dark:border-zinc-700 dark:text-zinc-400">
          No expenses recorded this month yet.
        </p>
      ) : (
        <ul className="mt-5 space-y-4">
          {rows.map(([category, amount]) => {
            const share = total ? Math.round((amount / total) * 100) : 0
            return (
              <li key={category} title={`${category}: ${formatAmount(amount)} (${share}% of this month's spending)`}>
                <div className="flex items-baseline justify-between gap-3 text-sm">
                  <span className="truncate font-medium">{category}</span>
                  <span className="shrink-0 tabular-nums text-zinc-600 dark:text-zinc-300">
                    {formatAmount(amount)}
                    <span className="ml-2 text-zinc-400 dark:text-zinc-500">{share}%</span>
                  </span>
                </div>
                <div className="mt-1.5 h-2 rounded-full bg-zinc-100 dark:bg-zinc-800">
                  <div
                    className="h-2 rounded-full bg-[#059669]"
                    style={{ width: `${max ? Math.max((amount / max) * 100, 2) : 0}%` }}
                  />
                </div>
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}
