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

const useAppContext = (): GlobalAppContext => {
    return useContext(AppContext)
}

type AppStateManager = {
    setUser: (user: UserDto | null) => void,
    setLoading: (isLoading: boolean) => void,
} & AppState

export const useAppStateManager = (): AppStateManager => {
    const {appState, setAppState} = useAppContext()

    const setUser = (user: UserDto | null) => {
        setAppState({...appState, user})
    }

    const setLoading = (isLoading: boolean) => {
        setAppState({...appState, isLoading})
    }

    return {
        ...appState,
        setUser,
        setLoading,
    }
}

// Use this hook only in _app.tsx, in rest of the components use useAppState
export const initGlobalState = (): GlobalAppContext => {
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

            fetchGetMe({headers: {"accept": "application/json"}})
                .then(res => {
                    setAppState(s => {
                        return {
                            ...s,
                            user: res.user,
                        }
                    })
                })
                .catch(err => {
                    console.error("Login failed", err)
                    if (err.status && err.status === 401) {
                        authService.logout()
                    }
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