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
        // converts date object to string in format like "2023-01-01 11:59:59" but in UTC timezone
        const day = date.getUTCDate();
        const month = date.getUTCMonth() + 1;
        const year = date.getUTCFullYear();
        const hours = date.getUTCHours();
        const minutes = date.getUTCMinutes();
        const seconds = date.getUTCSeconds();

        return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day} ${hours < 10 ? '0' + hours : hours}:${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
    },
    imitateBackendFormat(date: Date): string {
        // backend datetime format is "2023-03-16T20:28:51+00:00", convert date to string
        const day = date.getDate();
        const month = date.getMonth() + 1;
        const year = date.getFullYear();
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const seconds = date.getSeconds();

        return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}T${hours < 10 ? '0' + hours : hours}:${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}+00:00`;
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
        // backend datetime format is "2023-03-16T20:28:51+00:00"
        const [date, time] = datetime.split('T');
        const [year, month, day] = date.split('-');
        const [hours, minutes, seconds] = time.split(':');

        const secondsWithOffset = seconds.split('+')[0];

        return new Date(
            parseInt(year),
            parseInt(month) - 1,
            parseInt(day),
            parseInt(hours),
            parseInt(minutes),
            parseInt(secondsWithOffset),
        )
    },
    toLocalShortDate(from: Date) {
        // converts date object to string in format like 01 Jan
        return from.toLocaleDateString(undefined, {
            day: '2-digit',
            month: 'short',
        });
    }
}