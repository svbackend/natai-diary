import {XMarkIcon} from "@heroicons/react/24/outline";

export function AddedFilesRow({files: files, onDelete: onDelete}: { files: FileList, onDelete: (file: File) => void }) {
    const filesArr = Array.from(files)

    const fileKey = (file: File) => `${file.name}-${file.size}`

    return (
        <div className="flex flex-row my-2 overflow-auto">
            {filesArr.map(file => <AddedFileBadge key={fileKey(file)} file={file} onDelete={onDelete}/>)}
        </div>
    )
}

function AddedFileBadge({file, onDelete}: { file: File, onDelete: (file: File) => void }) {
    return (
        <span className="bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 w-auto">
            {file.name}
            <XMarkIcon className="w-4 h-4 inline ml-2 cursor-pointer" onClick={() => onDelete(file)}/>
        </span>
    )
}