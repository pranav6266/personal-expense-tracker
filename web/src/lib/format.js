const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
})

export const formatAmount = value => currency.format(Number(value) || 0)

export const EXPENSE = 0
export const INCOME = 1

export const currentMonthPrefix = (now = new Date()) =>
  `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
