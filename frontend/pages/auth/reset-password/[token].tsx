import {useRouter} from "next/router";
import {useEffect, useState} from "react";
import {useTranslations} from "use-intl";
import {usePostPasswordResetConfirmation} from "../../../src/api/apiComponents";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import Link from "next/link";
import {EaseOutTransition} from "../../../src/modules/common/components/EaseOutTransition";
import {AlertApiError, AlertSuccess} from "../../../src/modules/common/components/alert";
import {TextField} from "../../../src/modules/common/components/textField";
import {FormSubmitButton} from "../../../src/modules/common/components/FormSubmitButton";
import {useForm} from "react-hook-form";

type FormValues = {
    password: string
}

export default function ResetPasswordPage() {
    const router = useRouter();

    const t = useTranslations("ResetPasswordPage");
    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>();

    const {mutateAsync: sendResetPasswordRequest, isLoading, isSuccess, isError, error} = usePostPasswordResetConfirmation({})

    const onSubmit = async (data: FormValues) => {
        try {
            await sendResetPasswordRequest({
                body: {
                    token: router.query.token as string,
                    password: data.password,
                }
            })
        } catch(e) {
            console.error(e)
            return;
        }

        setTimeout(() => {
            router.push("/login")
        }, 5000)
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
                        label={t("NewPassword")}
                        name={"password"}
                        type={"password"}
                        placeholder={"********"}
                        register={register("password", {required: true})}
                    />

                    <FormSubmitButton label={t("ResetPassword")} loading={isLoading}/>
                </form>
            </div>
        </MainLayout>
    )
}
