import {useRouter} from "next/router";
import {useState} from "react";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import {useTranslations} from "use-intl";
import MainLayout from "../../src/modules/common/components/mainLayout";
import {fetchGetMe} from "../../src/api/apiComponents";
import {useAppStateManager} from "../../src/modules/common/state";
import PrimaryButton from "../../src/modules/common/components/PrimaryButton";

function EmailNotYetVerifiedError() {
    const t = useTranslations("NotVerifiedPage");

    return (
        <div
            className="p-4 mt-4 mb-4 text-sm text-red-700 bg-red-100 rounded-lg dark:bg-red-200 dark:text-red-800 animate-bounce"
            role="alert">
            <span className="font-medium">{t("yourEmailStillNotConfirmed")}</span>
            {t("pleaseCheckYourEmail")}
        </div>
    );
}

function NotVerifiedEmptyState() {
    const t = useTranslations("NotVerifiedPage");

    const {user, setUser} = useAppStateManager()

    const router = useRouter();

    const [isLoading, setIsLoading] = useState(false);
    const [showError, setShowError] = useState(false);

    const showErrorFor3Seconds = () => {
        setShowError(true);
        setTimeout(() => {
            setShowError(false);
        }, 3750);
    }

    const checkVerification = () => {
        setIsLoading(true);

        fetchGetMe({})
            .then(res => {
                if (res.user.isEmailVerified) {
                    router.push("/diary");
                } else {
                    showErrorFor3Seconds()
                }
            })
            .catch(e => {

            })
            .finally(() => {
                setIsLoading(false);
            })
    }

    return (
        <div className={"flex flex-col items-center justify-center"}>
            <div className={"text-center"}>
                <h1 className={"text-3xl font-bold"}>
                    {t("confirmYourEmail")}
                </h1>
                {user && (
                    <p className={"text-lg mt-4"}>
                        {t("weSentYouALink")} <strong>{user.email}</strong>
                    </p>
                )}

                <p className={"text-lg mt-4"}>
                    {t("checkYourEmail")}
                </p>

                {showError && <EmailNotYetVerifiedError/>}

                <PrimaryButton onClick={checkVerification}
                        className={"mt-4"}>
                    {t("done")}
                    {isLoading && <AppSpinner/>}
                </PrimaryButton>
            </div>
        </div>
    )
}

export default function NotVerified() {
    return (
        <MainLayout>
            <NotVerifiedEmptyState/>
        </MainLayout>
    )
}
