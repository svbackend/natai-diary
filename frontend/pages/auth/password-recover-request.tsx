import MainLayout from "../../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {useRouter} from "next/router";
import {useAppStateManager} from "../../src/modules/common/state";
import {AlreadyLoggedIn} from "../../src/modules/auth/components/alreadyLoggedIn";
import {useTranslations} from "use-intl";
import {usePostLogin, usePostPasswordReset} from "../../src/api/apiComponents";
import Link from "next/link";
import {EaseOutTransition} from "../../src/modules/common/components/EaseOutTransition";
import {AlertApiError, AlertError, AlertSuccess} from "../../src/modules/common/components/alert";
import {TextField} from "../../src/modules/common/components/textField";
import {FormSubmitButton} from "../../src/modules/common/components/FormSubmitButton";
import {defaultMetadata} from "../../src/utils/seo";
import {Seo} from "../../src/modules/common/components/GlobalSeo";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}

type FormValues = {
    email: string
}

export default function PasswordRecoverRequestPage() {
    const router = useRouter()
    const t = useTranslations("PasswordRecoverRequestPage");
    const {register, handleSubmit, formState: {errors}} = useForm<FormValues>({
        defaultValues: {
            email: router.query.email ? router.query.email as string : ""
        }
    });

    const {mutate: sendPasswordRecoverRequest, isSuccess, isError, error, isLoading} = usePostPasswordReset()

    const {user, setUser} = useAppStateManager()

    if (user) {
        return <AlreadyLoggedIn/>
    }

    const onSubmit = (data: FormValues) => {
        sendPasswordRecoverRequest({
            body: {
                email: data.email
            }
        })
    }

    return (
        <MainLayout>
            <div className="mx-auto flex flex-col max-w-md rounded-md">
                <div className="mb-8 text-center">
                    <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                    <p>{t("PasswordRecoverDescription")}</p>
                    <p>
                        {t("RecallPassword")}
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

                {isSuccess && (
                    <EaseOutTransition>
                        <AlertSuccess message={t("PasswordResetSuccess")}/>
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

                    <FormSubmitButton label={t("ResetPassword")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}