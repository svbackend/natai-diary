import {usePostLogin, usePostPasswordReset} from "../src/api/apiComponents";
import MainLayout from "../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {useTranslations} from "use-intl";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";
import {AlertApiError} from "../src/modules/common/components/alert";
import {useRouter} from "next/router";
import {LoginSuccessResponse} from "../src/api/apiSchemas";
import {authService} from "../src/modules/auth/services/authService";
import {useAppStateManager} from "../src/modules/common/state";
import {AlreadyLoggedIn} from "../src/modules/auth/components/alreadyLoggedIn";
import {EaseOutTransition} from "../src/modules/common/components/EaseOutTransition";
import Link from "next/link";
import {defaultMetadata} from "../src/utils/seo";
import {Seo} from "../src/modules/common/components/GlobalSeo";

type FormValues = {
    email: string
    password: string
}

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function LoginPage() {
    const router = useRouter()
    const t = useTranslations("LoginPage");
    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>();
    const {mutateAsync: sendLoginRequest, isError, error, isLoading} = usePostLogin()
    const {user, setUser} = useAppStateManager()

    if (user) {
        return <AlreadyLoggedIn/>
    }

    const onSubmit = async (data: FormValues) => {
        let response: LoginSuccessResponse

        try {
            response = await sendLoginRequest({
                body: {
                    email: data.email,
                    password: data.password
                }
            })
        } catch (e) {
            // todo scroll into view AlertApiError
            return;
        }

        authService.loginWithToken(response.apiToken)
        setUser(response.user)

        const from = authService.getRedirectUrl(router.query.from)
        await router.push(from)
    }

    return (
        <MainLayout>
            <div className="mx-auto flex flex-col max-w-md rounded-md">
                <div className="mb-8 text-center">
                    <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                    <p>{t("LoginDescription")}</p>
                    <p>
                        {t("DontHaveAnAccount")}
                        <Link href="/registration" className="ml-1 text-blue-500 hover:underline">
                            {t("Register")}
                        </Link>
                    </p>
                </div>

                {isError && error && (
                    <EaseOutTransition>
                        <AlertApiError error={error}/>
                    </EaseOutTransition>
                )}

                <form onSubmit={handleSubmit(onSubmit)}>
                    <TextField
                        label={t("Email")}
                        name={"email"}
                        type={"email"}
                        placeholder={"john.doe@example.com"}
                        register={register("email", {required: true})}
                    />

                    <TextField
                        label={t("Password")}
                        name={"password"}
                        type={"password"}
                        placeholder={"********"}
                        register={register("password", {required: true})}
                    />

                    <p className={"mb-4"}>
                        {t("ForgotPassword")}
                        <Link href={"/auth/password-recover-request?email=" + (watch("email") || "")}
                              className={"ml-1 text-blue-500"}>
                            {t("ResetPassword")}
                        </Link>
                    </p>

                    <FormSubmitButton label={t("Login")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}
