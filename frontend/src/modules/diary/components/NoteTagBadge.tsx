export default function NoteTagBadge({tag}: { tag: string }) {
    return (
        <span className="bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 w-auto">
            #{tag}
        </span>
    )
}