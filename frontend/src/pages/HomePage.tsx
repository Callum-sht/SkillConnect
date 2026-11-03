import FeaturedServices from '../components/home/FeaturedServices'
import Hero from '../components/home/Hero'
import Footer from '../components/layout/Footer'
import Navbar from '../components/layout/Navbar'
import type { Service } from '../components/ServiceCard'

const categories = [
  { icon: '📚', name: 'Tutoring' },
  { icon: '💻', name: 'Coding' },
  { icon: '📷', name: 'Photography' },
  { icon: '🎵', name: 'Music' },
  { icon: '🎨', name: 'Design' },
  { icon: '🛠️', name: 'Handyman' },
]

const featuredServices: Service[] = [
  {
    id: 1,
    title: 'Math Tutoring',
    provider: 'Cochi Johnson',
    category: 'Tutoring',
    price: 35,
    rating: 4.9,
  },
  {
    id: 2,
    title: 'Portrait Photography',
    provider: 'Henry Smith',
    category: 'Photography',
    price: 60,
    rating: 4.8,
  },
  {
    id: 3,
    title: 'Java Programming Help',
    provider: 'Blue Sky',
    category: 'Coding',
    price: 45,
    rating: 5.0,
  },
]

const workflowSteps = [
  {
    number: '01',
    title: 'Find a service',
    description: 'Browse local services and find someone with the skills you need.',
  },
  {
    number: '02',
    title: 'Request a booking',
    description: 'Choose a suitable date and time and send a booking request.',
  },
  {
    number: '03',
    title: 'Get connected',
    description: 'The provider confirms the request and delivers the service.',
  },
  {
    number: '04',
    title: 'Leave a review',
    description: 'Share your experience after the booking has been completed.',
  },
]

function HomePage() {
  return (
    <>
      <Navbar />

      <main>
        <Hero />

        <section className="mx-auto max-w-[1200px] px-4 py-12 sm:px-6 sm:py-16" id="categories">
          <div className="mb-8 flex flex-wrap items-end justify-between gap-5">
            <div>
              <p className="font-bold text-brand">Browse skills</p>
              <h2 className="mt-2 text-3xl font-bold text-ink sm:text-4xl">Popular categories</h2>
            </div>
            <button className="font-bold text-brand hover:text-brand-dark" type="button">
              View all categories →
            </button>
          </div>

          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
            {categories.map((category) => (
              <article
                className="rounded-2xl border border-line bg-white px-4.5 py-6 shadow-sm transition-transform hover:-translate-y-1"
                key={category.name}
              >
                <span className="text-3xl" aria-hidden="true">{category.icon}</span>
                <h3 className="mt-4 font-semibold text-ink">{category.name}</h3>
              </article>
            ))}
          </div>
        </section>

        <FeaturedServices services={featuredServices} />

        <section
          className="mt-8 bg-surface px-4 py-14 sm:px-6 sm:py-16"
          id="how-it-works"
        >
          <div className="mx-auto max-w-[1152px]">
            <div className="mb-9">
              <p className="font-bold text-brand">Simple and easy</p>
              <h2 className="mt-2 text-3xl font-bold text-ink sm:text-4xl">How SkillConnect works</h2>
            </div>

            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
              {workflowSteps.map((step) => (
                <article className="rounded-[18px] bg-white p-6.5" key={step.number}>
                  <span className="font-extrabold text-brand">{step.number}</span>
                  <h3 className="mt-4 text-xl font-semibold text-ink">{step.title}</h3>
                  <p className="mt-2 leading-relaxed text-muted">{step.description}</p>
                </article>
              ))}
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </>
  )
}

export default HomePage
