function Hero() {
  return (
    <section
      className="mx-auto grid min-h-[560px] max-w-[1200px] grid-cols-1 items-center gap-10 px-4 py-12 sm:px-6 sm:py-16 lg:grid-cols-[1.2fr_0.8fr] lg:gap-14"
      id="home"
    >
      <div>
        <p className="font-bold text-brand">Local skills. Real people.</p>

        <h1 className="my-3 text-[clamp(2.5rem,4.5vw,3.65rem)] leading-[1.1] font-bold tracking-tight text-ink sm:mb-5">
          Find the right skills,
          <br />
          right in your community.
        </h1>

        <p className="max-w-[650px] text-lg leading-7 text-muted">
          Discover trusted local people offering tutoring, photography,
          coding help, music lessons, design services and more.
        </p>

        <div className="mt-8 flex max-w-[650px] flex-col gap-2.5 sm:flex-row">
          <input
            aria-label="Service search"
            className="min-w-0 flex-1 rounded-[10px] border border-line px-4 py-3.5 outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/20"
            type="text"
            placeholder="What service are you looking for?"
          />
          <button
            className="rounded-[10px] bg-brand px-4.5 py-3 font-semibold text-white transition-colors hover:bg-brand-dark"
            type="button"
          >
            Find Services
          </button>
        </div>

        <div className="mt-5 flex flex-wrap gap-3.5">
          <button
            className="rounded-[10px] bg-brand px-4.5 py-3 font-semibold text-white transition-colors hover:bg-brand-dark"
            type="button"
          >
            Explore Services
          </button>

          <button
            className="rounded-[10px] border border-line bg-white px-4.5 py-3 font-semibold text-ink transition-colors hover:bg-surface"
            type="button"
          >
            Offer a Service
          </button>
        </div>
      </div>

      <div className="flex min-h-[360px] flex-col justify-center gap-4.5 rounded-[28px] border border-violet-100 bg-gradient-to-br from-violet-50 to-surface p-6 sm:min-h-[420px] sm:p-10">
        <div className="flex items-center gap-4 rounded-2xl bg-white p-5 shadow-[0_10px_30px_rgba(0,0,0,0.06)]">
          <span className="text-[32px]">👨‍💻</span>
          <p>Coding Help</p>
        </div>

        <div className="flex items-center gap-4 rounded-2xl bg-white p-5 shadow-[0_10px_30px_rgba(0,0,0,0.06)]">
          <span className="text-[32px]">📷</span>
          <p>Photography</p>
        </div>

        <div className="flex items-center gap-4 rounded-2xl bg-white p-5 shadow-[0_10px_30px_rgba(0,0,0,0.06)]">
          <span className="text-[32px]">🎸</span>
          <p>Music Lessons</p>
        </div>
      </div>
    </section>
  )
}

export default Hero
