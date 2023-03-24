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

export const IMAGE_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif', 'svg', 'jfif', 'webp']

export const attachmentService = {
    isImage: (filename: string, mimeType?: string): boolean => {
        const ext = filename.split('.').pop()?.toLowerCase()

        if (ext && IMAGE_EXTENSIONS.includes(ext)) {
            return true
        }

        if (mimeType && mimeType.startsWith('image/')) {
            return true
        }

        return false
    }
}