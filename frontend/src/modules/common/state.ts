import {createContext, useContext, useEffect, useState} from "react";
import {UserDto} from "../../api/apiSchemas";
import {storageService} from "./services/storageService";
import {fetchGetMe} from "../../api/apiComponents";
import {authService} from "../auth/services/authService";

type ModalsState = {}

type AppState = {
    isLoading: boolean;
    setIsLoading: (isLoading: boolean) => void;
    user: UserDto | null;
    setUser: (user: UserDto | null) => void;
}

export const initialAppState = {
    isLoading: false,
    setIsLoading: () => {
    },
    user: null,
    setUser: (user: UserDto) => {
        this.user = user
    },
}

export const AppContext = createContext<AppState>(initialAppState)

export const useAppState = () => {
    const ctx = useContext<AppState>(AppContext);
    const [authInProgress, setAuthInProgress] = useState(false);

    const canBeAuthenticated = ctx.user === null && storageService.getApiToken() !== null

    useEffect(() => {
        if (canBeAuthenticated && !authInProgress) {
            setAuthInProgress(true);

            ctx.setIsLoading(true)
            fetchGetMe({})
                .then(res => {
                    ctx.setUser(res.user)
                })
                .catch(err => {
                    console.error(err)
                    authService.logout()
                })
                .finally(() => {
                    ctx.setIsLoading(false)
                    setAuthInProgress(false)
                })
        }
    }, [])

    return ctx
}