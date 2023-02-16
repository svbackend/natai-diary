export const titleGeneratorService = {
    generateTitle(): string {
        const today = new Date();

        const emojis = [
            "\uD83C\uDF31",
            "\uD83D\uDE04",
            "\uD83C\uDFB8",
            "\uD83D\uDC36",
            "☀️",
            "\uD83C\uDF0E",
            "☘️",
            "\uD83D\uDD25",
            "\uD83C\uDF3F",
            "\uD83C\uDF42",
        ]

        const weekDay = today.toLocaleString('default', {weekday: 'long'});

        const randomEmoji = emojis[Math.floor(Math.random() * emojis.length)];

        return `${weekDay} ${randomEmoji}`;
    }
}
