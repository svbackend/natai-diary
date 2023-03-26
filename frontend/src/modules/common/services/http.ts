import {getCookie} from "../../../utils/cookie";
import {storageService} from "./storageService";

const BASE_API_URL = process.env.NEXT_PUBLIC_API_URL;
const BASE_INTERNAL_API_URL = process.env.NEXT_PUBLIC_INTERNAL_API_URL;
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
        const apiToken = storageService.getApiToken();
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

        const baseUrl = typeof window === 'undefined' ? BASE_INTERNAL_API_URL : BASE_API_URL;

        const finalUrl = `${baseUrl}${url}`

        console.log("TRYING TO FETCH", finalUrl)
        console.log("WITH INIT", finalInit)

        return fetch(finalUrl, finalInit);
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