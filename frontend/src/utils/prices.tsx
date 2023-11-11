export function formatPrice(price: number) {
    const formatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    });
    return formatter.format(price)
}

export const price_suggestion_links = 1120
export const price_suggestion_links_early_bird = 799
