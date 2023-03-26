import {useRouter} from "next/router";
import {useTranslations} from "use-intl";
import {ResolverResult, useForm} from "react-hook-form";
import MainLayout from "../../src/modules/common/components/mainLayout";
import {usePostChangePassword} from "../../src/api/apiComponents";
import {TextField} from "../../src/modules/common/components/textField";
import {FormSubmitButton} from "../../src/modules/common/components/FormSubmitButton";
import {validationService} from "../../src/modules/common/services/validationService";
import {AlertApiError, AlertSuccess} from "../../src/modules/common/components/alert";
import {defaultMetadata} from "../../src/utils/seo";

type FormValues = {
    oldPassword: string,
    newPassword: string,
    newPasswordConfirmation: string
}

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function ChangePasswordPage() {
    const router = useRouter();
    const t = useTranslations("ChangePasswordPage");

    const validationResolver = (data: FormValues): ResolverResult => {
        if (data.newPassword === data.newPasswordConfirmation) {
            delete errors["newPasswordConfirmation"];
        } else {
            errors["newPasswordConfirmation"] = {
                type: "notMatch",
                message: t("NewPasswordConfirmationNotMatch")
            }
        }

        return {values: data, errors};
    }

    const {register, handleSubmit, watch, formState: {errors}, setError} = useForm<FormValues>({
        resolver: validationResolver
    });


    const {mutateAsync: sendChangePasswordRequest, isLoading, isSuccess, isError, error} = usePostChangePassword()

    const onSubmit = async (data: FormValues) => {
        try {
            const res = await sendChangePasswordRequest({
                body: {
                    oldPassword: data.oldPassword,
                    newPassword: data.newPassword,
                }
            })
        } catch (e) {
            // todo scroll into view AlertApiError
            validationService.handleValidationErrors(e, setError)
            return;
        }

        setTimeout(() => {
            router.push("/settings")
        }, 3000)
    }

    return (
        <MainLayout>
            <div className="mx-auto flex flex-col max-w-md rounded-md">
                <div className="mb-8 text-center">
                    <h1 className="my-3 text-4xl font-bold">{t("Title")}</h1>
                    <p>{t("Description")}</p>
                </div>

                {isError && error && (
                    <AlertApiError error={error}/>
                )}

                {isSuccess && (
                    <AlertSuccess message={t("SuccessMessage")}/>
                )}

                <form onSubmit={handleSubmit(onSubmit)}>
                    <TextField
                        label={t("CurrentPassword")}
                        name={"oldPassword"}
                        type={"password"}
                        placeholder={"********"}
                        register={register("oldPassword", {required: true})}
                        errors={errors}
                    />
                    <TextField
                        label={t("NewPassword")}
                        name={"newPassword"}
                        type={"password"}
                        placeholder={"********"}
                        register={register("newPassword", {required: true})}
                        errors={errors}
                    />

                    <TextField
                        label={t("NewPasswordConfirmation")}
                        name={"newPasswordConfirmation"}
                        type={"password"}
                        placeholder={"********"}
                        register={register("newPasswordConfirmation", {required: true})}
                        errors={errors}
                    />

                    <FormSubmitButton label={t("ResetPassword")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}