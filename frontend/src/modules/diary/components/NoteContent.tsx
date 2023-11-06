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

    // todo if you ever will add note sharing, you need to sanitize this html
    const content = {__html: htmlContent}
    return (<div dangerouslySetInnerHTML={content}></div>)
}