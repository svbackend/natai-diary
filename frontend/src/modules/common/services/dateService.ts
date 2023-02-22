export const dateService = {
    toReadableDMY(date: Date): string {
        // converts date object to string in format like 01 Jan 2023
        const day = date.getDate();
        const month = date.toLocaleString('en-us', {month: 'short'});
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
    },
    toHMS(date: Date): string {
        // converts date object to string in format like 11:59:59
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const seconds = date.getSeconds();
        return `${hours < 10 ? '0' + hours : hours}:${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
    },
    toBackendFormat(date: Date): string {
        // converts date object to string in format like "2023-01-01 11:59:59"
        const ymd = dateService.toYMD(date);
        const hms = dateService.toHMS(date);

        return `${ymd} ${hms}`;
    },
    fromYmd(date: string): Date {
        // backend date format is "2023-01-01"
        const [year, month, day] = date.split('-');

        return new Date(
            parseInt(year),
            parseInt(month) - 1,
            parseInt(day),
        );
    },
    fromBackendFormat(datetime: string): Date {
        // backend datetime format is "2023-01-01 11:59:59"

        const [ymd, hms] = datetime.split(' ');
        const [year, month, day] = ymd.split('-');
        const [hours, minutes, seconds] = hms.split(':');

        return new Date(
            parseInt(year),
            parseInt(month) - 1,
            parseInt(day),
            parseInt(hours),
            parseInt(minutes),
            parseInt(seconds)
        );
    }
}