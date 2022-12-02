import {UseFormSetError} from "react-hook-form/dist/types/form";

export const validationService = {
    handleValidationErrors(e: any, setError: UseFormSetError<any>) {
        if (typeof e.status !== 'number') {
            return;
        }

        if (e.status < 400) {
            return;
        }

        if (!e.payload) {
            return;
        }

        if ("errors" in e.payload) {
            for (const [key, value] of Object.entries(e.payload.errors)) {
                if (typeof value === 'object' && value !== null) {
                    if ("message" in value && "label" in value) {
                        setError(String(value.label), {message: String(value.message)});
                    }
                }
            }
        }
    }
}