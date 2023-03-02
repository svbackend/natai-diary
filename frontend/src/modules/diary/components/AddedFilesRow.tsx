import {XMarkIcon} from "@heroicons/react/24/outline";
import {useEffect, useState} from "react";
import {LocalNoteAttachment} from "../services/attachmentService";

export function AddedFilesRow({files: files, onDelete: onDelete}: { files: LocalNoteAttachment[], onDelete: (file: LocalNoteAttachment) => void }) {
    const fileKey = (file: File) => `${file.name}-${file.size}`

    return (
        <div className="grid grid-cols-3 gap-2 mb-2 pb-2">
            {files.map(file => <AddedFileBadge key={fileKey(file.originalFile)} file={file} onDelete={onDelete}/>)}
        </div>
    )
}

function AddedFileBadge({file, onDelete}: { file: LocalNoteAttachment, onDelete: (file: LocalNoteAttachment) => void }) {
    return (
        <div className={"flex flex-col flex-1 border p-2 rounded"}>
            <AttachedFilePreview file={file.originalFile}/>

            <div className={"flex items-center mt-1 w-full"}>
                <span className="text-xs text-gray-500 whitespace-nowrap">{getShortenedFileName(file.name)}</span>
                <XMarkIcon className="w-4 h-4 inline ml-1 cursor-pointer" onClick={() => onDelete(file)}/>
            </div>

        </div>
    )
}

function AttachedFilePreview({file}: { file: File }) {
    const [preview, setPreview] = useState<string | ArrayBuffer | null>(null)

    const isImage = file.type.startsWith("image/")
    const [isPreviewLoading, setIsPreviewLoading] = useState(false)

    useEffect(() => {
        let fileReader: FileReader, isCancel = false;
        if (file && isImage) {
            setIsPreviewLoading(true)
            fileReader = new FileReader();
            fileReader.onload = (e) => {
                // @ts-ignore
                const { result } = e.target;
                if (result && !isCancel) {
                    setIsPreviewLoading(false)
                    setPreview(result)
                }
            }
            fileReader.readAsDataURL(file);
        }
        return () => {
            isCancel = true;
            if (fileReader && fileReader.readyState === 1) {
                fileReader.abort();
            }
        }

    }, [file]);

    return (
        <div className="flex flex-col items-center">
            {isPreviewLoading && <div className="h-16 w-16 bg-gray-200 rounded animate-pulse"/>}
            {isImage && !isPreviewLoading && preview && <img src={preview as string} alt={file.name} className="h-16 rounded"/>}
            {!isImage && <div className="w-16 h-16 bg-gray-200">{file.type}</div>}
        </div>
    )
}

const getShortenedFileName = (fileName: string) => {
    // returns file name with extension, but cuts middle part of the name if it's too long
    const MAX_LENGTH = 12;
    const EXTENSION_LENGTH = 4;
    const MAX_NAME_LENGTH = MAX_LENGTH - EXTENSION_LENGTH;

    if (fileName.length <= MAX_LENGTH) {
        return fileName;
    }

    const extension = fileName.slice(-EXTENSION_LENGTH);
    const name = fileName.slice(0, fileName.length - EXTENSION_LENGTH);

    const namePartLength = Math.floor(MAX_NAME_LENGTH / 2);
    const namePart1 = name.slice(0, namePartLength);
    const namePart2 = name.slice(-namePartLength);

    return `${namePart1}...${namePart2}${extension}`;
}