export type NoteContentProps = {
    content: string
}

export default function NoteContent(props: NoteContentProps) {
    const paragraphs = props.content.split("\n\n")

    let htmlContent = "";
    for (const paragraph of paragraphs) {
        const lines = paragraph.split("\n")
        htmlContent += "<p>" + lines.join("<br/>") + "</p>"
    }

    const content = {__html: htmlContent}
    return (<div dangerouslySetInnerHTML={content}></div>)
}