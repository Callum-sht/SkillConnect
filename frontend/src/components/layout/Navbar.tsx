function Navbar() {
  return (
    <header className="sticky top-0 z-50 border-b border-line bg-white">
      <div className="mx-auto flex min-h-18 max-w-[1200px] items-center justify-between gap-8 px-4 sm:px-6">
        <a
          className="text-2xl font-extrabold text-brand"
          href="/"
        >
          SkillConnect
        </a>

        <nav
          className="hidden items-center gap-7 min-[901px]:flex"
          aria-label="Main navigation"
        >
          <a
            className="font-medium text-gray-600 transition-colors hover:text-brand"
            href="/#home"
          >
            Home
          </a>

          <a
            className="font-medium text-gray-600 transition-colors hover:text-brand"
            href="/#categories"
          >
            Categories
          </a>

          <a
            className="font-medium text-gray-600 transition-colors hover:text-brand"
            href="/#services"
          >
            Services
          </a>

          <a
            className="font-medium text-gray-600 transition-colors hover:text-brand"
            href="/#how-it-works"
          >
            How It Works
          </a>
        </nav>

        <div className="flex gap-2 sm:gap-3">
          <a
            className="rounded-[10px] border border-line bg-white px-3 py-2.5 font-semibold transition-colors hover:bg-surface focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-100 sm:px-4.5"
            href="/login"
          >
            Log in
          </a>

          <a
            className="rounded-[10px] bg-brand px-3 py-2.5 font-semibold text-white transition-colors hover:bg-brand-dark focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-violet-100 sm:px-4.5"
            href="/register"
          >
            Sign up
          </a>
        </div>
      </div>
    </header>
  )
}

export default Navbar