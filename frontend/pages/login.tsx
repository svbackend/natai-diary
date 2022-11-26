import {usePostLogin} from "../src/api/apiComponents";
import MainLayout from "../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {useTranslations} from "use-intl";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";
import {AlertApiError} from "../src/modules/common/components/alert";
import {api} from "../src/modules/common/services/http";

type FormValues = {
    email: string
    password: string
}

export default function LoginPage() {
    const t = useTranslations("LoginPage");
    const {register, handleSubmit, formState: {errors}} = useForm<FormValues>();
    const {mutate: login2, mutateAsync: login, data, isError, error, isLoading} = usePostLogin()

    const onSubmit = async (data: FormValues) => {
        login2(
            {
                body: {
                    email: data.email,
                    password: data.password
                }
            }
        )

        // try {
        //     const response = await login({
        //         body: {
        //             email: data.email,
        //             password: data.password
        //         }
        //     })
        //
        //     api.setAuthToken(response.apiToken)
        // } catch (e) {
        //     console.log("catch", e)
        // }


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
                    <TextField label={t("Email")} name={"email"} type={"email"} placeholder={"john.doe@example.com"}
                               register={register("email")}/>
                    <TextField label={t("Password")} name={"password"} type={"password"} placeholder={"********"}
                               register={register("password")}/>

                    <FormSubmitButton label={t("Login")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}
