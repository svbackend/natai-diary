export type FilesUpdateCallback = (cb: FilesUpdateCallback1) => void

export type FilesUpdateCallback1 = (oldFiles: LocalNoteAttachment[]) => LocalNoteAttachment[]

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