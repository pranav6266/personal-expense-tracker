const sizes = {
  md: { box: 'h-8 w-8 rounded-lg', icon: 16 },
  lg: { box: 'h-12 w-12 rounded-xl', icon: 24 },
}

export default function Logo({ size = 'md' }) {
  const { box, icon } = sizes[size]
  return (
    <div className={`${box} flex items-center justify-center bg-emerald-600 shadow-sm`} aria-hidden="true">
      <svg width={icon} height={icon} viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M19 7V5a2 2 0 0 0-2-2H5a2 2 0 0 0 0 4h14a2 2 0 0 1 2 2v3M3 5v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-3" />
        <path d="M21 12h-4a2 2 0 0 0 0 4h4z" />
      </svg>
    </div>
  )
}
