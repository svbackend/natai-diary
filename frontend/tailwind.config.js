const { blackA, mauve, violet } = require('@radix-ui/colors');

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
        ...blackA,
        ...mauve,
        ...violet,
        'brand': '#9571F8',
        'brand-alt': '#d1beff',
        'dark': '#29293E',
        'light': '#E8E8FF',
        'light2': '#ECEAFF',
        'light3': '#E8DFFF',
        'nav-item': '#6D6D9C',
        'nav-item-alt': '#A0A0D4',
        'nav-bg': '#1C1B20',
        'darkish': '#3C3867',
        'whitish': '#fafaff',
        'sep': '#AFAFE8',
        'sep-alt': '#4D4770',
        'copyright': '#232037',
        'why': '#2D2B53',
        'delimiter': '#2D2A53',
        'menu': '#313146',
        'menu-b': '#5a5a71',
        'field': '#201F24',
        'tag': '#342C4B',
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
  ],
}
