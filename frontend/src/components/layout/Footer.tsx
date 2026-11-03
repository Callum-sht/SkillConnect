function Footer() {
  return (
    <footer className="bg-[#151515] px-4 pt-15 pb-6 text-white sm:px-6">
      <div className="mx-auto flex max-w-[1200px] flex-col justify-between gap-10 min-[701px]:flex-row">
        <div>
          <h3 className="text-xl font-bold">SkillConnect</h3>
          <p className="mt-2 text-zinc-400">
            Connecting local skills with local people.
          </p>
        </div>

        <nav className="flex flex-wrap gap-6" aria-label="Footer navigation">
          <a className="transition-colors hover:text-brand" href="#home">Home</a>
          <a className="transition-colors hover:text-brand" href="#categories">Categories</a>
          <a className="transition-colors hover:text-brand" href="#services">Services</a>
          <a className="transition-colors hover:text-brand" href="#how-it-works">How It Works</a>
        </nav>
      </div>

      <div className="mx-auto mt-10 max-w-[1200px] border-t border-[#333] pt-6 text-zinc-400">
        © 2026 SkillConnect
      </div>
    </footer>
  )
}

export default Footer
