export function getOffsetByPage(page: number, limit: number): number {
    return (page - 1) * limit;
}

export function urlParams(params): string {
    return Object.keys(params)
        .map(key => `${key}=${params[key]}`)
        .join('&');
}