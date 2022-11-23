import type * as Schema from '../../api/apiSchemas';
import {useMutation} from "@tanstack/react-query";

/**
 * React Query hook for the /api/auth/login endpoint
 */
export function useLoginMutation() {
    return useMutation<Schema.LoginSuccessResponse, Schema.LoginErrorRef, Schema.LoginRequestRef>(
        async (data) => {
            const response = await fetch('http://127.0.0.1:8000/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });
            if (!response.ok) {
                throw await response.json();
            }
            return response.json();
        },
    );
}