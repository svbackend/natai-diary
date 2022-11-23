import {useLoginMutation} from "../src/hooks/apiHooks";

export default function Home() {
    const loginMutation = useLoginMutation();

    const login = () => {
        loginMutation.mutate({email: ",", password: ""})
    }

    return (
        <div>
            {loginMutation.isLoading && <>Loading..</>}

            {loginMutation.error && (
                <>
                    Status = {loginMutation.error.code}
                </>
            )}

            {loginMutation.data && loginMutation.data.apiToken && (
                <>
                    <h1>Token = {loginMutation.data.apiToken}</h1>
                </>
            )}

            Hello World!

            <button onClick={login}>Login!</button>
        </div>
    )
}
