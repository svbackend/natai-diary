import {useRouter} from "next/router";
import {useTranslations} from "use-intl";
import {useDeleteMe} from "../../src/api/apiComponents";
import {useAppStateManager} from "../../src/modules/common/state";
import MainLayout from "../../src/modules/common/components/mainLayout";
import {EaseOutTransition} from "../../src/modules/common/components/EaseOutTransition";
import {AlertApiError, AlertSuccess} from "../../src/modules/common/components/alert";
import {FormSubmitButton} from "../../src/modules/common/components/FormSubmitButton";
import {defaultMetadata} from "../../src/utils/seo";
import {FormEvent} from "react";

type FormValues = {
    newEmail: string
}

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}

export default function RemoveProfilePage() {
    const t = useTranslations("RemoveProfilePage");
    const router = useRouter()

    const {mutateAsync: deleteProfileRequest, isSuccess, isError, error, isLoading} = useDeleteMe()

    const {user, setUser} = useAppStateManager()

    const deleteProfile = async (e: FormEvent) => {
        e.preventDefault()
        try {
            await deleteProfileRequest({})
            setUser(null)
            await router.push("/")
        } catch (e) {
            console.error(e)
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

                    <form className="flex flex-row" onSubmit={deleteProfile}>
                        <FormSubmitButton
                            label={t("removeButton")}
                            loading={isLoading}
                            disabled={isLoading}
                        />
                    </form>

                </div>
            </MainLayout>
        </>
    )
}
