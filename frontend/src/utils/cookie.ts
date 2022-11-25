export function getCookie (name) {
    if (typeof document === 'undefined' || typeof document.cookie === 'undefined') {
        return null;
    }

    let value = `; ${document.cookie}`;
    let parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}