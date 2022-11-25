// Use type safe message keys with `next-intl`
type Messages = typeof import('./i18n/messages/en.json');
declare interface IntlMessages extends Messages {}