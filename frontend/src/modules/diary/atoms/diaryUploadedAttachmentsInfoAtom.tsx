import {atom} from 'jotai'
import {AttachmentUploadInfo} from "../services/attachmentService";

export const diaryUploadedAttachmentsInfoAtom = atom<AttachmentUploadInfo[]>([])