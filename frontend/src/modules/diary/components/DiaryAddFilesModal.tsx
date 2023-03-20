import {useAtom} from "jotai";
import {Dialog} from "@headlessui/react";
import React, {ChangeEvent, useRef} from "react";
import {diaryAddAttachmentModalAtom} from "../atoms/diaryAddAttachmentModalAtom";
import {CloudArrowUpIcon} from "@heroicons/react/24/outline";
import {usePostAttachments} from "../../../api/apiComponents";
import {AttachmentUploadInfo, FilesUpdateCallback, LocalNoteAttachment} from "../services/attachmentService";
import {diaryUploadedAttachmentsInfoAtom} from "../atoms/diaryUploadedAttachmentsInfoAtom";
import DialogWrapper, {CloseModalTopButton} from "./DialogWrapper";

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
        onUpdate,
        onDelete
    }: { addedFiles: LocalNoteAttachment[], onUpdate: FilesUpdateCallback, onDelete: (file: LocalNoteAttachment) => void }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddAttachmentModalAtom)

    return (
        <DialogWrapper modalAtom={diaryAddAttachmentModalAtom}>
            <DiaryAddFilesModalContent
                addedFiles={addedFiles}
                onUpdate={onUpdate}
                onDelete={onDelete}
                onClose={() => setIsMenuOpen(false)}
            />
        </DialogWrapper>
    )
}

function DiaryAddFilesModalContent({
                                       addedFiles,
                                       onDelete,
                                       onClose,
                                       onUpdate
                                   }: { addedFiles: LocalNoteAttachment[], onDelete: (file: LocalNoteAttachment) => void, onClose: () => void, onUpdate: FilesUpdateCallback }) {

    const {mutateAsync: generateUploadUrl} = usePostAttachments()

    const [filesUploadInfo, setFilesUploadInfo] = useAtom(diaryUploadedAttachmentsInfoAtom)

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


        onUpdate((oldFiles) => {
            const updatedFiles = oldFiles.map(file => {
                if (file.id === fileId) {
                    return {
                        ...file,
                        cloudAttachmentId: attachmentId
                    }
                } else {
                    return file
                }
            })

            console.log("setFileAttachmentId", fileId, attachmentId, updatedFiles)

            return updatedFiles
        })
    }

    const onFilesSelected = async (files: LocalNoteAttachment[]) => {
        onUpdate(oldFiles => {
            const oldFilesToKeep = oldFiles.filter(oldFile => !files.find(file => file.id === oldFile.id))
            return [...oldFilesToKeep, ...files]
        })

        for (const file of files) {
            const signedUploadUrl = await generateUploadUrl({
                body: {filename: file.originalFile.name}
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
        <div className="relative shadow">
            <CloseModalTopButton onClose={onClose}/>

            <div className="px-6 py-4 border-b rounded-t border-sep dark:border-sep-alt">
                <h3 className="text-base font-semibold text-dark dark:text-light lg:text-xl">
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
            <div className="flex items-center justify-end p-6 border-t border-sep dark:border-sep-alt rounded-b">
                <button
                    onClick={onClose}
                    className="inline-flex justify-center py-2 px-4 font-semibold border border-transparent shadow-sm text-sm font-medium rounded-md text-light bg-brand hover:bg-brand/80 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
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
                className="flex justify-center w-full h-32 px-4 transition border-2 border-sep dark:border-sep-alt border-dashed rounded-md appearance-none cursor-pointer hover:border-sep-alt focus:outline-none">
                    <span className="flex items-center space-x-2 text-nav-item dark:text-nav-item-alt">
                        <CloudArrowUpIcon className="w-6 h-6"/>
                        <span className="font-medium">
                            Drop files to Attach, or&nbsp;
                            <span className="font-semibold underline">browse</span>
                        </span>
                    </span>
            </label>
            <input onChange={handleFileChange} type="file" accept="image/*" multiple={true} className="hidden"
                   ref={inputRef}/>
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
        <div className="flex flex-row items-center justify-between w-full p-2 my-1 dark:bg-brand/20 text-dark dark:text-light rounded-lg">
            <div className="flex flex-row items-center w-full">
                <div
                    className="flex flex-row items-center justify-center w-12 h-12 mr-2 text-white bg-brand rounded-lg">
                    <span className="text-sm font-medium leading-none">{file.ext}</span>
                </div>
                <div className="flex flex-col flex-1">
                    <span className="text-sm font-medium leading-none">{getShortenedFilename(file.name)}</span>
                    <span className="text-xs font-normal leading-none text-light3">
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

                    {uploadInfo.progress === 0 && !uploadInfo.error && (
                        <span
                            className="inline-flex items-center bg-blue-100 text-blue-800 text-xs font-medium mr-2 px-2.5 py-0.5 rounded-full">
                            <span className="w-2 h-2 mr-1 bg-blue-500 rounded-full"></span>
                            Pending
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
            <div className="bg-brand text-xs font-medium text-blue-100 text-center p-0.5 leading-none rounded-full"
                 style={{width: percentage}}> {percentage}
            </div>
        </div>
    )
}

function getShortenedFilename(originalFilename: string) {
    const maxLength = 14;

    if (originalFilename.length <= maxLength) {
        return originalFilename;
    }

    const ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    const filename = originalFilename.substring(0, originalFilename.lastIndexOf('.'));

    const namePartLength = Math.floor(maxLength / 2);
    const namePart1 = filename.substring(0, namePartLength);
    const namePart2 = filename.substring(filename.length - namePartLength);

    return `${namePart1}...${namePart2}.${ext}`;
}