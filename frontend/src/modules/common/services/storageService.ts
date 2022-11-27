const localStorageStore = {
    get: (key: string): string | null => {
        if (typeof window === "undefined") return null;
        return window.localStorage.getItem(key);
    },
    set: (key: string, value: string) => {
        if (typeof window === "undefined") return;
        window.localStorage.setItem(key, value);
    }
}

export const storageService = {
    getApiToken: () => localStorageStore.get("apiToken"),
    setApiToken: (token: string) => localStorageStore.set("apiToken", token)
}



