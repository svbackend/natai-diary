export function getCookie(name: string) {
    if (typeof document === 'undefined' || typeof document.cookie === 'undefined') {
        return null;
    }

    let value = `; ${document.cookie}`;
    let parts = value.split(`; ${name}=`);

    if (parts.length === 2) {
        // @ts-ignore
        return parts.pop().split(';').shift();
    }
}