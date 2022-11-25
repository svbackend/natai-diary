export const getTranslations = (locale: string) => {
    return require(`./messages/${locale}.json`);
}