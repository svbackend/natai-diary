import {usePostLogin} from "../src/api/apiComponents";
import MainLayout from "../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {useTranslations} from "use-intl";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";
import {AlertApiError} from "../src/modules/common/components/alert";
import {useRouter} from "next/router";
import {LoginSuccessResponse} from "../src/api/apiSchemas";
import {authService} from "../src/modules/auth/services/authService";

type FormValues = {
    email: string
    password: string
}

export default function LoginPage() {
    const router = useRouter()
    const t = useTranslations("LoginPage");
    const {register, handleSubmit, formState: {errors}} = useForm<FormValues>();
    const {mutateAsync: login, data, isError, error, isLoading} = usePostLogin()

    const onSubmit = async (data: FormValues) => {
        let response: LoginSuccessResponse

        try {
            response = await login({
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

        const from = authService.getRedirectUrl(router.query.from)
        await router.push(from)
    }

    return (
        <MainLayout>
            <div className="mx-auto flex flex-col max-w-md rounded-md">
                <div className="mb-8 text-center">
                    <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                    <p>{t("LoginDescription")}</p>
                </div>

                {isError && error && (
                    <AlertApiError error={error}/>
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

                    <FormSubmitButton label={t("Login")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}
