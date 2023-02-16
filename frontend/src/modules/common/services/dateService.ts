export const dateService = {
    toReadableDMY(date: Date): string {
        // converts date object to string in format like 01 Jan 2023
        const day = date.getDate();
        const month = date.toLocaleString('en-us', { month: 'short' });
        const year = date.getFullYear();
        return `${day < 10 ? '0' + day : day} ${month} ${year}`;
    },
    toDMY(date: Date): string {
        // converts date object to string in format like 01.01.2023
        const day = date.getDate();
        const month = date.getMonth() + 1;
        const year = date.getFullYear();
        return `${day < 10 ? '0' + day : day} ${month < 10 ? '0' + month : month} ${year}`;
    },
    toYMD(date: Date): string {
        // converts date object to string in format like 2023-01-01
        const day = date.getDate();
        const month = date.getMonth() + 1;
        const year = date.getFullYear();
        return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
    }
}