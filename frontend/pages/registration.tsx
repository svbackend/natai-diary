import MainLayout from "../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";
import {usePostLogin, usePostRegistration} from "../src/api/apiComponents";
import {useAppStateManager} from "../src/modules/common/state";
import {AlreadyLoggedIn} from "../src/modules/auth/components/alreadyLoggedIn";
import {AlertApiError} from "../src/modules/common/components/alert";
import {validationService} from "../src/modules/common/services/validationService";
import {useRouter} from "next/router";
import {LoginSuccessResponse} from "../src/api/apiSchemas";
import {authService} from "../src/modules/auth/services/authService";
import Link from "next/link";
import {useTranslations} from "use-intl";
import {EaseOutTransition} from "../src/modules/common/components/EaseOutTransition";

type FormValues = {
    name: string
    email: string
    password: string
}

export default function RegistrationPage() {
    const t = useTranslations("RegistrationPage");
    const router = useRouter()
    const {register, handleSubmit, watch, formState: {errors}, setError} = useForm<FormValues>();

    const {mutateAsync: sendRegistrationRequest, isError, error, isLoading} = usePostRegistration()
    const {mutateAsync: sendLoginRequest, isError: isLoginError, isLoading: isLoginLoading} = usePostLogin()

    const isProcessing = isLoading || isLoginLoading

    const {user, setUser} = useAppStateManager()

    if (user) {
        return <AlreadyLoggedIn/>
    }

    const registerUser = async (data: FormValues) => {
        try {
            const res = await sendRegistrationRequest({
                body: {
                    name: data.name,
                    email: data.email,
                    password: data.password
                }
            })
        } catch (e) {
            // todo scroll into view AlertApiError
            validationService.handleValidationErrors(e, setError)
            return;
        }

        try {
            await loginUser(data)
        } catch (e) {
            // registration successful, but login failed
            await router.push("/login")
        }
    }

    const loginUser = async (data: FormValues) => {
        let response: LoginSuccessResponse

        response = await sendLoginRequest({
            body: {
                email: data.email,
                password: data.password
            }
        })

        authService.loginWithToken(response.apiToken)
        setUser(response.user)

        await router.push("/auth/not-verified")
    }

    return (
        <>
            <MainLayout>
                <div className="mx-auto flex flex-col max-w-md rounded-md">
                    <div className="mb-8 text-center">
                        <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                        <p>{t("Description")}</p>
                        <p>
                            {t("AlreadyHaveAnAccount")}
                            <Link href="/login" className="ml-1 text-blue-500 hover:underline">
                                {t("Login")}
                            </Link>
                        </p>
                    </div>

                    {isError && error && (
                        <EaseOutTransition>
                            <AlertApiError error={error}/>
                        </EaseOutTransition>
                    )}

                    <form onSubmit={handleSubmit(registerUser)}>
                        <TextField label={t("Name")} name={"name"} type={"text"} placeholder={"John Doe"}
                                   errors={errors}
                                   register={register("name", {required: true})}/>
                        <TextField label={t("Email")} name={"email"} type={"email"} placeholder={"john.doe@example.com"}
                                   errors={errors}
                                   register={register("email", {required: true})}/>
                        <TextField label={t("Password")} name={"password"} type={"password"} placeholder={"********"}
                                   errors={errors}
                                   register={register("password", {required: true})}/>

                        <FormSubmitButton loading={isProcessing} label={t("Register")}/>
                    </form>
                </div>
            </MainLayout>
        </>
    )
}
