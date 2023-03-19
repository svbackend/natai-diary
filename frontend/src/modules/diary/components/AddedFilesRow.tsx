import {XMarkIcon} from "@heroicons/react/24/outline";
import {useEffect, useState} from "react";
import {attachmentService, LocalNoteAttachment} from "../services/attachmentService";
import {CloudAttachmentDto} from "../../../api/apiSchemas";

export function AddedFilesRow(
    {
        localFiles,
        onDeleteLocal,
        cloudFiles,
        onDeleteCloud,
    }: {
        localFiles: LocalNoteAttachment[],
        onDeleteLocal: (file: LocalNoteAttachment) => void,
        cloudFiles?: CloudAttachmentDto[],
        onDeleteCloud?: (file: CloudAttachmentDto) => void,
    }
) {
    const fileKey = (file: File) => `${file.name}-${file.size}`

    return (
        <div className="grid grid-cols-3 gap-2 mb-2 pb-2">
            {localFiles.map(file => <AddedFileBadge key={fileKey(file.originalFile)} file={file} onDelete={onDeleteLocal}/>)}
            {onDeleteCloud && cloudFiles?.map(file => <AddedCloudFileBadge key={file.attachmentId} file={file} onDelete={onDeleteCloud}/>)}
        </div>
    )
}

function AddedFileBadge({file, onDelete}: { file: LocalNoteAttachment, onDelete: (file: LocalNoteAttachment) => void }) {
    return (
        <div className={"flex flex-col flex-1 border border-transparent p-2 rounded"}>
            <AttachedFilePreview file={file} onDelete={onDelete} />

            <div className={"flex justify-center mt-1 w-full"}>
                <span className="text-xs text-gray-500 whitespace-nowrap">{getShortenedFileName(file.name)}</span>
            </div>

        </div>
    )
}

function AddedCloudFileBadge({file, onDelete}: { file: CloudAttachmentDto, onDelete: (file: CloudAttachmentDto) => void }) {
    const fileName = file.key.split("/").pop() || ""
    return (
        <div className={"flex flex-col flex-1 border border-transparent p-2 rounded"}>
            <CloudFilePreview file={file} onDelete={onDelete} />

            <div className={"flex justify-center mt-1 w-full"}>
                <span className="text-xs text-gray-500 whitespace-nowrap">{getShortenedFileName(fileName)}</span>
            </div>

        </div>
    )
}

function AttachedFilePreview({file, onDelete}: { file: LocalNoteAttachment, onDelete: (file: LocalNoteAttachment) => void }) {
    const [preview, setPreview] = useState<string | ArrayBuffer | null>(null)

    const isImage = attachmentService.isImage(file.name, file.type)
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
            fileReader.readAsDataURL(file.originalFile);
        }
        return () => {
            isCancel = true;
            if (fileReader && fileReader.readyState === 1) {
                fileReader.abort();
            }
        }

    }, [file]);

    return (
        <div className="flex flex-col items-center relative">
            {isPreviewLoading && <div className="h-16 w-16 bg-gray-200 rounded animate-pulse"/>}
            {isImage && !isPreviewLoading && preview && <div className="h-16 w-16 rounded" style={{
                backgroundImage: `url(${preview as string})`,
                backgroundSize: "cover",
            }}/>}
            {!isImage && <div className="w-16 h-16 bg-blue-200">{file.type}</div>}
            <div
                onClick={() => onDelete(file)}
                className="absolute cursor-pointer inline-flex items-center justify-center w-6 h-6 text-xs font-bold text-light bg-copyright border-2 border-menu rounded-full -top-3 right-1">
                <XMarkIcon className="w-3 h-3 inline"/>
            </div>
        </div>
    )
}

function CloudFilePreview({file, onDelete}: { file: CloudAttachmentDto, onDelete: (file: CloudAttachmentDto) => void }) {
    const isImage = attachmentService.isImage(file.key, file.metadata?.mimeType || "")
    const preview = file.previews.find(p => p.type === "md")
    const previewUrl = preview?.signedUrl || file.signedUrl
    return (
        <div className="flex flex-col items-center relative">
            {isImage && preview && <div className="h-16 w-16 rounded" style={{
                backgroundImage: `url(${previewUrl as string})`,
                backgroundSize: "cover",
            }}/>}
            {!isImage && <div className="w-16 h-16 bg-blue-200">File</div>}
            <div
                onClick={() => onDelete(file)}
                className="absolute cursor-pointer inline-flex items-center justify-center w-6 h-6 text-xs font-bold text-light bg-copyright border-2 border-menu rounded-full -top-3 right-1">
                <XMarkIcon className="w-3 h-3 inline"/>
            </div>
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