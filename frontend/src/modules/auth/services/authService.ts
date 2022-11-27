import {storageService} from "../../common/services/storageService";
import {api} from "../../common/services/http";
import {NextRouter} from "next/router";

export const authService = {
    loginWithToken: (apiToken: string) => {
        storageService.setApiToken(apiToken);
        api.setAuthToken(apiToken);
    },
    logout: () => {
        storageService.deleteApiToken();
        api.setAuthToken(null);
    },
    getRedirectUrl: (from: string | string[] | undefined): string => {
        if (typeof from === "undefined") {
            return "/";
        }

        if (Array.isArray(from)) {
            return "/";
        }

        if (from.startsWith("/registration")) {
            return "/";
        }

        return from;
    },
    createUrlForRedirect: (router: NextRouter): string => {
        const path = router.asPath;

        if (path.startsWith("/registration") || path.startsWith("/login")) {
            return "";
        }

        if (path === "/") {
            return "";
        }

        return `?from=${encodeURIComponent(path)}`;
    },
}