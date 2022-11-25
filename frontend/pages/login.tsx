import {usePostLogin} from "../src/api/apiComponents";
import MainLayout from "../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {useTranslations} from "use-intl";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";
import {AlertApiError} from "../src/modules/common/components/alert";

type FormValues = {
    email: string
    password: string
}

export default function LoginPage() {
    const t = useTranslations("LoginPage");
    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>();
    const {mutate: login, isError, error, isLoading} = usePostLogin()

    const onSubmit = (data: FormValues) => {
        login({
            body: {
                email: data.email,
                password: data.password
            }
        })
    }

    return (
        <MainLayout>
            <div className="mx-auto flex flex-col max-w-md rounded-md">
                <div className="mb-8 text-center">
                    <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                    <p className="dark:text-gray-400">{t("LoginDescription")}</p>
                </div>

                {isError && error && (
                    <AlertApiError error={error}/>
                )}

                <form onSubmit={handleSubmit(onSubmit)}>
                    <TextField label={t("Email")} name={"email"} type={"email"} placeholder={"john.doe@example.com"}
                               register={register}/>
                    <TextField label={t("Password")} name={"password"} type={"password"} placeholder={"********"}
                               register={register}/>

                    <FormSubmitButton label={t("Login")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}
