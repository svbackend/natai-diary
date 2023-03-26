import {ApiContext} from "./apiContext";
import {api} from "../modules/common/services/http";

export type ErrorWrapper<TError> =
    | TError
    | { status: "unknown"; payload: string };

export type ApiFetcherOptions<TBody, THeaders, TQueryParams, TPathParams> = {
    url: string;
    method: string;
    body?: TBody;
    headers?: THeaders;
    queryParams?: TQueryParams;
    pathParams?: TPathParams;
    signal?: AbortSignal;
} & ApiContext["fetcherOptions"];

export async function apiFetch<TData,
    TError,
    TBody extends {} | undefined | null,
    THeaders extends {},
    TQueryParams extends {},
    TPathParams extends {}>({
                                url,
                                method,
                                body,
                                headers,
                                pathParams,
                                queryParams,
                                signal,
                            }: ApiFetcherOptions<TBody,
    THeaders,
    TQueryParams,
    TPathParams>): Promise<TData> {

    const response = await api.fetch(
        `${resolveUrl(url, queryParams, pathParams)}`,
        {
            signal,
            method: method.toUpperCase(),
            body: body ? JSON.stringify(body) : undefined,
            headers: {
                "Content-Type": "application/json",
                ...headers,
            },
        }
    );
    console.log("PROMISE RESOLVED", response)
    if (!response.ok) {
        console.log("RESPONSE NOT OK")
        let error: ErrorWrapper<TError>;
        try {
            const payload = await response.json();
            console.log("ERROR_PAYLOAD 1", payload)
            error = {
                status: response.status,
                payload: payload,
            } as TError;
        } catch (e) {
            error = {
                status: "unknown" as const,
                payload:
                    e instanceof Error
                        ? `Unexpected error (${e.message})`
                        : "Unexpected error",
            };
        }

        console.log("ERROR_THROWN", error)

        throw error;
    }

    if (response.headers.get("content-type")?.includes("json")) {
        console.log("RESPONSE IS JSON, AWAIT")
        return await response.json();
    } else {
        console.log("RESPONSE IS NOT JSON, AWAIT BLOB")
        // if it is not a json response, assume it is a blob and cast it to TData
        return (await response.blob()) as unknown as TData;
    }

}

const resolveUrl = (
    url: string,
    queryParams: Record<string, string> = {},
    pathParams: Record<string, string> = {}
) => {
    let query = new URLSearchParams(queryParams).toString();
    if (query) query = `?${query}`;
    return url.replace(/\{\w*\}/g, (key) => pathParams[key.slice(1, -1)]) + query;
};
