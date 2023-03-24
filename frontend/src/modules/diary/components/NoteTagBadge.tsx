export default function NoteTagBadge({tag}: { tag: string }) {
    return (
        <span className="bg-light3 dark:bg-menu rounded-full px-3 py-1 text-[13px] dark:text-light font-semibold mr-2 w-auto">
            #{tag}
        </span>
    )
}