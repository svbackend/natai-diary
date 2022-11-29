import {useRouter} from "next/router";
import {useEffect, useState} from "react";
import {useTranslations} from "use-intl";
import {usePostVerifyEmail} from "../../src/api/apiComponents";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import {EaseOutTransition} from "../../src/modules/common/components/EaseOutTransition";
import {AlertApiError} from "../../src/modules/common/components/alert";
import MainLayout from "../../src/modules/common/components/mainLayout";

function VerifyEmailComponent() {
    const router = useRouter();

    const t = useTranslations("VerifyEmailPage");

    const [showError, setShowError] = useState(false);

    const {mutateAsync: sendVerifyEmailRequest, isLoading, isSuccess, isError, error} = usePostVerifyEmail({})

    interface Token {
        token: string;
    }

    useEffect(() => {
        if (router.query.token) {
            sendVerifyEmailRequest({
                body: {
                    token: router.query.token as string
                }
            })
                .then(r => {
                    setTimeout(() => {
                        router.push("/");
                    }, 3500)
                })
                .catch(e => {
                    console.error(e)
                })
        }
    }, [router.query.token]);


    return (
        <div className={"flex flex-col items-center justify-center"}>
            <div className={"text-center"}>
                {isLoading && (
                    <>
                        <h1 className={"text-3xl font-bold"}>
                            {t("weAreCheckingYourEmail")}
                        </h1>
                        <AppSpinner/>
                    </>
                )}

                {isSuccess && (
                    <>
                        <h1 className={"text-3xl font-bold"}>
                            {t("successMessage")}
                        </h1>
                        <p className={"text-lg mt-4"}>
                            {t("youWillBeRedirected")}
                        </p>
                    </>
                )}

                {isError && error && (
                    <>
                        <EaseOutTransition>
                            <AlertApiError error={error}/>
                        </EaseOutTransition>
                    </>
                )}
            </div>
        </div>
    )
}

export default function VerifyEmail() {
    return (
        <MainLayout>
            <VerifyEmailComponent/>
        </MainLayout>
    )
}
