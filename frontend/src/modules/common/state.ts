import {createContext, useContext, useEffect, useState} from "react";
import {UserDto} from "../../api/apiSchemas";
import {storageService} from "./services/storageService";
import {fetchGetMe} from "../../api/apiComponents";
import {authService} from "../auth/services/authService";

type ModalsState = {}

export type GlobalAppContext = {
    appState: AppState,
    setAppState: (appState: AppState) => void,
}

export type AppState = {
    isLoading: boolean;
    user: UserDto | null;
}

export const initialAppState = {
    isLoading: false,
    user: null,
}

export const initialContext = {
    appState: initialAppState,
    setAppState: () => {
    }
}


export const AppContext = createContext<GlobalAppContext>(initialContext)

export const useAppContext = (): GlobalAppContext => {
    return useContext(AppContext)
}

// Use this hook only in _app.tsx, in rest of the components use useAppContext
export const useAppState = (): GlobalAppContext => {
    const [appState, setAppState] = useState<AppState>(initialAppState)
    const [authInProgress, setAuthInProgress] = useState(false);

    const canBeAuthenticated = appState.user === null && storageService.getApiToken() !== null

    const isLoading = appState.isLoading || authInProgress

    useEffect(() => {
        if (canBeAuthenticated && !isLoading) {
            setAuthInProgress(true);
            setAppState(s => {
                return {...s, isLoading: true}
            })

            fetchGetMe({})
                .then(res => {
                    setAppState(s => {
                        return {
                            ...s,
                            user: res.user,
                        }
                    })
                })
                .catch(err => {
                    console.error(err)
                    authService.logout()
                })
                .finally(() => {
                    setAppState(s => {
                        return {...s, isLoading: false}
                    })
                    setAuthInProgress(false)
                })
        }
    }, [])

    return {appState, setAppState}
}