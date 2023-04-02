import {createContext, useContext, useEffect, useState} from "react";
import {UserDto} from "../../api/apiSchemas";
import {storageService} from "./services/storageService";
import {fetchGetMe} from "../../api/apiComponents";
import {authService} from "../auth/services/authService";

export type GlobalAppContext = {
    appState: AppState,
    setAppState: (appState: AppState) => void,
}

export type AppState = {
    isLoading: boolean;
    isLoaded: boolean;
    user: UserDto | null;
}

export const initialAppState = {
    isLoading: false,
    isLoaded: false,
    user: null,
}

export const initialContext = {
    appState: initialAppState,
    setAppState: () => {
    }
}

export const AppContext = createContext<GlobalAppContext>(initialContext)

const useAppContext = (): GlobalAppContext => {
    return useContext(AppContext)
}

type AppStateManager = {
    setUser: (user: UserDto | null) => void,
} & AppState

export const useAppStateManager = (): AppStateManager => {
    const {appState, setAppState} = useAppContext()

    const setUser = (user: UserDto | null) => {
        setAppState({...appState, user})
    }

    return {
        ...appState,
        setUser,
    }
}

// Use this hook only in _app.tsx, in rest of the components use useAppState
export const useGlobalState = (): GlobalAppContext => {
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
                            isLoading: false,
                            isLoaded: true,
                        }
                    })
                })
                .catch(err => {
                    console.error("Login failed", err)
                    if (err.status && err.status === 401) {
                        authService.logout()
                    }
                    setAppState(s => {
                        return {...s, isLoading: false, isLoaded: true}
                    })
                })
                .finally(() => {
                    setAuthInProgress(false)
                })
        } else if (!canBeAuthenticated) {
            setAppState(s => {
                return {...s, isLoading: false, isLoaded: true}
            })
        }
    }, [])

    return {appState, setAppState}
}