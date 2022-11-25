import {getCookie} from "../../../utils/cookie";

const BASE_API_URL = process.env.NEXT_PUBLIC_API_URL;
const DEFAULT_LOCALE = "en";

export class HttpClient {
    private static instance?: HttpClient

    constructor(
        private locale: string,
        private authToken: string | null,
    ) {
        HttpClient.instance = this;
    }

    static getInstance(): HttpClient {
        let locale: string = getCookie("NEXT_LOCALE") || DEFAULT_LOCALE
        let apiToken: string | null
        if (typeof window !== 'undefined') {
            apiToken = window.localStorage.getItem("auth_token")
        } else {
            apiToken = null
        }
        return HttpClient.instance || new HttpClient(locale, apiToken);
    }

    setAuthToken(token: string | null) {
        this.authToken = token;
    }

    fetch(url: string, init?: RequestInit & { json?: any }) {
        const headers = this.headers();

        init?.headers && Object.assign(headers, init.headers);

        if (init?.json) {
            init.body = JSON.stringify(init.json);
        }

        const finalInit = {
            ...init,
            headers,
        }

        return fetch(`${BASE_API_URL}${url}`, finalInit);
    }

    private headers() {
        type HeadersType = {
            [key: string]: string
        }
        const headers: HeadersType = {
            'x-locale': this.locale,
            'Content-Type': 'application/json',
        }

        if (this.authToken) {
            headers['X-API-TOKEN'] = this.authToken;
        }

        return headers;
    }
}

export const api = HttpClient.getInstance()