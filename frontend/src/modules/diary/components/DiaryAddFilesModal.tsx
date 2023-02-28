import {useAtom} from "jotai";
import {Dialog} from "@headlessui/react";
import React, {ChangeEvent, useRef, useState} from "react";
import {diaryAddAttachmentModalAtom} from "../atoms/diaryAddAttachmentModalAtom";
import {CloudArrowUpIcon} from "@heroicons/react/24/outline";
import {usePostAttachments} from "../../../api/apiComponents";

export type AttachmentUploadInfo = {
    fileId: string
    isLoading: boolean
    error: null | string
    progress: number
}

export type LocalNoteAttachment = {
    id: string

    cloudAttachmentId: null | string
    name: string
    size: number
    type: string
    ext: string
    originalFile: File
}

const uploadFileRequest = (url: string, file: File, onProgress: (progress: number) => void) => {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();
        xhr.open("PUT", url);
        xhr.setRequestHeader("Content-Type", file.type);
        xhr.upload.onprogress = (event) => {
            onProgress(event.loaded / event.total)
        }
        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(xhr.response);
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.onerror = () => reject(xhr.statusText);
        xhr.send(file);
    });
};

export function DiaryAddFilesModal(
    {
        addedFiles,
        onAdd,
        onDelete
    }: { addedFiles: LocalNoteAttachment[], onAdd: (files: LocalNoteAttachment[]) => void, onDelete: (file: LocalNoteAttachment) => void }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddAttachmentModalAtom)

    return (
        <Dialog
            open={isMenuOpen}
            onClose={() => setIsMenuOpen(false)}
            className="relative z-50"
        >
            {/* The backdrop, rendered as a fixed sibling to the panel container */}
            <div className="fixed inset-0 bg-black/30" aria-hidden="true"/>

            {/* Full-screen scrollable container */}
            <div className="fixed inset-0 overflow-y-auto">
                {/* Container to center the panel */}
                <div className="flex min-h-full items-center justify-center p-4">
                    {/* The actual dialog panel  */}
                    <Dialog.Panel className="mx-auto max-w-lg rounded bg-white">
                        <DiaryAddFilesModalContent
                            addedFiles={addedFiles}
                            onAdd={onAdd}
                            onDelete={onDelete}
                            onClose={() => setIsMenuOpen(false)}
                        />
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

function DiaryAddFilesModalContent({
                                       addedFiles,
                                       onDelete,
                                       onClose,
                                       onAdd
                                   }: { addedFiles: LocalNoteAttachment[], onDelete: (file: LocalNoteAttachment) => void, onClose: () => void, onAdd: (files: LocalNoteAttachment[]) => void }) {

    const {mutateAsync: generateUploadUrl} = usePostAttachments()

    const [filesUploadInfo, setFilesUploadInfo] = useState<AttachmentUploadInfo[]>([])

    const updateFileUploadInfo = (info: AttachmentUploadInfo) => {
        setFilesUploadInfo(oldFilesUploadInfo => {
            let found = false

            const newFilesUploadInfo = oldFilesUploadInfo.map(fileUploadInfo => {
                if (fileUploadInfo.fileId === info.fileId) {
                    found = true
                    return info
                } else {
                    return fileUploadInfo
                }
            })

            if (!found) {
                newFilesUploadInfo.push(info)
            }

            console.log(info.fileId, newFilesUploadInfo)

            return newFilesUploadInfo
        })
    }

    const setFileAttachmentId = (fileId: string, attachmentId: string) => {
        const updatedFiles = addedFiles.map(file => {
            if (file.id === fileId) {
                return {
                    ...file,
                    cloudAttachmentId: attachmentId
                }
            } else {
                return file
            }
        })

        onAdd(updatedFiles)
    }

    const onFilesSelected = async (files: LocalNoteAttachment[]) => {
        onAdd(files)

        for (const file of files) {
            const signedUploadUrl = await generateUploadUrl({
                body: {ext: file.ext}
            })

            updateFileUploadInfo({
                fileId: file.id,
                isLoading: true,
                error: null,
                progress: 0
            })

            try {
                await uploadFile(file, signedUploadUrl.uploadUrl)

                setFileAttachmentId(file.id, signedUploadUrl.attachmentId)

                updateFileUploadInfo({
                    fileId: file.id,
                    isLoading: false,
                    error: null,
                    progress: 100
                })
            } catch (e) {
                let strErr = "" + e

                updateFileUploadInfo({
                    fileId: file.id,
                    isLoading: false,
                    error: strErr || 'Unknown error',
                    progress: 0
                })
            }


        }
    }

    const uploadFile = async (file: LocalNoteAttachment, signedUploadUrl: string) => {
        const onProgress = (progress: number) => {
            const progressToSet = Math.round(progress * 100)
            updateFileUploadInfo({
                fileId: file.id,
                isLoading: true,
                error: null,
                progress: progressToSet
            })
        }
        await uploadFileRequest(signedUploadUrl, file.originalFile, onProgress)
    }

    const emptyUploadInfo: AttachmentUploadInfo = {
        fileId: '',
        isLoading: false,
        error: null,
        progress: 0
    }
    const getUploadInfoByFile = (file: LocalNoteAttachment): AttachmentUploadInfo => {
        return filesUploadInfo.find(f => f.fileId === file.id) || emptyUploadInfo
    }

    return (
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button type="button"
                    onClick={onClose}
                    className="absolute top-3 right-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 ml-auto inline-flex items-center dark:hover:bg-gray-800 dark:hover:text-white"
            >
                <svg aria-hidden="true" className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"></path>
                </svg>
                <span className="sr-only">Close modal</span>
            </button>

            <div className="px-6 py-4 border-b rounded-t dark:border-gray-600">
                <h3 className="text-base font-semibold text-gray-900 lg:text-xl dark:text-white">
                    Attach Files
                </h3>
            </div>

            <div className="p-6">
                <div className="flex flex-row flex-wrap max-w-full">
                    <DropZone onAdd={onFilesSelected}/>
                </div>

                <div className="flex flex-col my-2 overflow-auto">
                    {addedFiles.map(file => <AddedFileRow key={file.id} file={file} onDelete={onDelete}
                                                          uploadInfo={getUploadInfoByFile(file)}/>)}
                </div>
            </div>

            {/* "Done" button */}
            <div className="flex items-center justify-end p-6 border-t border-gray-300 rounded-b dark:border-gray-600">
                <button
                    onClick={onClose}
                    className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm p-2 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                    Done
                </button>
            </div>
        </div>
    )
}

function DropZone({onAdd}: { onAdd: (files: LocalNoteAttachment[]) => void }) {
    const inputRef = useRef<HTMLInputElement | null>(null);

    const onClick = () => {
        inputRef.current?.click();
    }

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (!e.target.files?.length) return;

        const files = Array.from(e.target.files).map(file => {
            return {
                id: `${file.name}-${file.size}-${file.type}`,
                cloudAttachmentId: null,
                name: file.name,
                size: file.size,
                type: file.type,
                ext: file.name.split('.').pop() || '',
                originalFile: file
            } as LocalNoteAttachment
        })

        onAdd(files)
    };

    return (
        <div className="w-full">
            <label
                onClick={onClick}
                className="flex justify-center w-full h-32 px-4 transition bg-white border-2 border-gray-300 border-dashed rounded-md appearance-none cursor-pointer hover:border-gray-400 focus:outline-none">
                    <span className="flex items-center space-x-2">
                        <CloudArrowUpIcon className="w-6 h-6 text-gray-600"/>
                        <span className="font-medium text-gray-600">
                            Drop files to Attach, or&nbsp;
                            <span className="text-blue-600 underline">browse</span>
                        </span>
                    </span>
            </label>
            <input onChange={handleFileChange} type="file" multiple={true} className="hidden" ref={inputRef}/>
        </div>
    )
}

function AddedFileRow({
                          file,
                          onDelete,
                          uploadInfo
                      }: { file: LocalNoteAttachment, onDelete: (file: LocalNoteAttachment) => void, uploadInfo: AttachmentUploadInfo }) {
    const bytesToReadableStr = (bytes: number) => {
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
        if (bytes === 0) return '0 Byte'
        const i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)).toString())
        if (i === 0) return `${bytes} ${sizes[i]}`
        return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${sizes[i]}`
    }

    return (
        <div className="flex flex-row items-center justify-between w-full p-2 my-1 bg-gray-100 rounded-lg">
            <div className="flex flex-row items-center">
                <div
                    className="flex flex-row items-center justify-center w-8 h-8 mr-2 text-white bg-gray-500 rounded-lg">
                    <span className="text-sm font-medium leading-none">{file.ext}</span>
                </div>
                <div className="flex flex-col">
                    <span className="text-sm font-medium leading-none">{file.name}</span>
                    <span className="text-xs font-normal leading-none text-gray-500">
                        {bytesToReadableStr(file.size)}
                    </span>

                    {uploadInfo.isLoading && (
                        <ProgressBar progress={uploadInfo.progress}/>
                    )}

                    {uploadInfo.progress === 100 && !uploadInfo.isLoading && (
                        <span
                            className="inline-flex items-center bg-green-100 text-green-800 text-xs font-medium mr-2 px-2.5 py-0.5 rounded-full">
                            <span className="w-2 h-2 mr-1 bg-green-500 rounded-full"></span>
                            Uploaded
                        </span>
                    )}

                    {uploadInfo.error && (
                        <span className="text-xs font-normal leading-none text-red-500">
                            {uploadInfo.error}
                        </span>
                    )}
                </div>
            </div>
            <button onClick={() => onDelete(file)}
                    className="flex flex-row items-center justify-center w-8 h-8 p-1 text-gray-500 rounded-lg hover:bg-gray-200">
                <svg aria-hidden="true" className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"></path>
                </svg>
            </button>
        </div>
    )
}

function ProgressBar({progress}: { progress: number }) {
    const percentage = `${progress}%`

    return (
        <div className="w-full bg-gray-200 rounded-full dark:bg-gray-700">
            <div className="bg-blue-600 text-xs font-medium text-blue-100 text-center p-0.5 leading-none rounded-full"
                 style={{width: percentage}}> {percentage}
            </div>
        </div>
    )
}