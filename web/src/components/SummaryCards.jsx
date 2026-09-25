import { EXPENSE, INCOME, currentMonthPrefix, formatAmount } from '../lib/format'

export default function SummaryCards({ expenses, loading }) {
  const month = currentMonthPrefix()
  const thisMonth = expenses.filter(e => e.date?.startsWith(month))
  const total = type =>
    thisMonth.filter(e => e.expenseType === type).reduce((sum, e) => sum + Number(e.amount || 0), 0)

  const spent = total(EXPENSE)
  const earned = total(INCOME)
  const monthName = new Date().toLocaleString('en-IN', { month: 'long' })

  const cards = [
    { label: `Spent in ${monthName}`, value: spent, tone: 'text-zinc-900 dark:text-zinc-100' },
    { label: `Income in ${monthName}`, value: earned, tone: 'text-emerald-700 dark:text-emerald-400' },
    {
      label: 'Balance this month',
      value: earned - spent,
      tone: earned - spent < 0 ? 'text-red-600 dark:text-red-400' : 'text-emerald-700 dark:text-emerald-400',
    },
  ]

  return (
    <div className="grid gap-4 sm:grid-cols-3">
      {cards.map(card => (
        <div
          key={card.label}
          className="rounded-2xl border border-zinc-200 bg-white p-5 shadow-sm dark:border-zinc-800 dark:bg-zinc-900"
        >
          <p className="text-sm text-zinc-500 dark:text-zinc-400">{card.label}</p>
          <p className={`mt-1 text-2xl font-semibold tracking-tight tabular-nums ${card.tone}`}>
            {loading ? '…' : formatAmount(card.value)}
          </p>
        </div>
      ))}
    </div>
  )
}
