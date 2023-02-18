/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./**/components/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'brand': '#9571F8',
        'brand-alt': '#d1beff',
        'dark': '#29293E',
        'light': '#E8E8FF',
        'nav-item': '#6D6D9C',
        'nav-item-alt': '#A0A0D4',
        'nav-bg': '#1C1B20',
        'darkish': '#3C3867',
        'sep': '#AFAFE8',
        'sep-alt': '#4D4770',
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
  ],
}
