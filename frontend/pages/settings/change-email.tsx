import {useForm} from "react-hook-form";
import {useRouter} from "next/router";
import {useTranslations} from "use-intl";
import {usePostChangeEmail} from "../../src/api/apiComponents";
import {useAppStateManager} from "../../src/modules/common/state";
import {validationService} from "../../src/modules/common/services/validationService";
import MainLayout from "../../src/modules/common/components/mainLayout";
import {EaseOutTransition} from "../../src/modules/common/components/EaseOutTransition";
import {AlertApiError, AlertSuccess} from "../../src/modules/common/components/alert";
import {FormSubmitButton} from "../../src/modules/common/components/FormSubmitButton";
import {TextField} from "../../src/modules/common/components/textField";
import {defaultMetadata} from "../../src/utils/seo";

type FormValues = {
    newEmail: string
}

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function ChangeEmailPage() {
    const t = useTranslations("ChangeEmailPage");
    const router = useRouter()
    const {register, handleSubmit, watch, formState: {errors}, setError} = useForm<FormValues>();

    const {mutateAsync: sendChangeEmailRequest, isSuccess, isError, error, isLoading} = usePostChangeEmail()

    const {user} = useAppStateManager()

    const changeEmail = async (data: FormValues) => {
        try {
            const res = await sendChangeEmailRequest({
                body: {
                    newEmail: data.newEmail,
                }
            })
        } catch (e) {
            // todo scroll into view AlertApiError
            validationService.handleValidationErrors(e, setError)
            return;
        }
    }

    return (
        <>
            <MainLayout>
                <div className="mx-auto flex flex-col max-w-md rounded-md">
                    <div className="mb-8 text-center">
                        <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                        <p>{t("Description")}</p>
                    </div>

                    {isError && error && (
                        <EaseOutTransition>
                            <AlertApiError error={error}/>
                        </EaseOutTransition>
                    )}

                    {isSuccess && (
                        <EaseOutTransition>
                            <AlertSuccess message={t("SuccessMessage")}/>
                        </EaseOutTransition>
                    )}

                    <form onSubmit={handleSubmit(changeEmail)}>
                        <TextField label={t("CurrentEmail")} name={"oldEmail"} type={"email"} readonly={true} value={user?.email}/>
                        <TextField label={t("NewEmail")} name={"newEmail"} type={"email"} placeholder={"new@email.here"}
                                   helperText={t("NewEmailHelperText")}
                                   errors={errors}
                                   register={register("newEmail", {required: true})}/>

                        <FormSubmitButton loading={isLoading} label={t("ChangeEmail")}/>
                    </form>
                </div>
            </MainLayout>
        </>
    )
}
