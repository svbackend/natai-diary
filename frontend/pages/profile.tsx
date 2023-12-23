import {useForm} from "react-hook-form";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";
import {usePutMe} from "../src/api/apiComponents";
import {useAppStateManager} from "../src/modules/common/state";
import {AlertApiError} from "../src/modules/common/components/alert";
import {validationService} from "../src/modules/common/services/validationService";
import {CityDto, UserDto} from "../src/api/apiSchemas";
import {useTranslations} from "use-intl";
import {EaseOutTransition} from "../src/modules/common/components/EaseOutTransition";
import {useState} from "react";
import CitySearch from "../src/modules/common/components/CitySearch";
import UserLayout, {UserLoadingScreen} from "../src/modules/common/components/userLayout";

type FormValues = {
    name: string
    enableEmailNotifications: boolean
}

export default function ProfilePage() {
    const {user, setUser, isLoaded} = useAppStateManager()

    if (!user) {
        return UserLoadingScreen
    }

    return (
        <UserLayout>
            <ProfilePageInner user={user}/>
        </UserLayout>
    )
}


function ProfilePageInner({user}: { user: UserDto }) {
    const t = useTranslations("ProfilePage");

    const [showSuccess, setShowSuccess] = useState(false)
    const [cityId, setCityId] = useState<number | null>(user?.profile?.city.id || null)

    const {register, handleSubmit, formState: {errors}, setError} = useForm<FormValues>({
        defaultValues: {
            name: user?.name || "",
            enableEmailNotifications: user?.profile?.enableEmailNotifications || true
        }
    });

    const {mutateAsync: sendUpdateProfileRequest, isError, error, isLoading} = usePutMe()

    const updateProfile = async (data: FormValues) => {
        if (!cityId) {
            alert("Please select a city")
            return;
        }

        try {
            const res = await sendUpdateProfileRequest({
                body: {
                    name: data.name,
                    cityId: cityId,
                    enableEmailNotifications: data.enableEmailNotifications,
                    timezoneOffset: (new Date()).getTimezoneOffset()
                }
            })
            setShowSuccess(true)
            setTimeout(() => {
                setShowSuccess(false)
            }, 3000)
        } catch (e) {
            validationService.handleValidationErrors(e, setError)
            return;
        }
    }

    const onCitySelected = (city: CityDto) => {
        setCityId(city.id)
    }

    return (
        <>
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

                {showSuccess && (
                    <EaseOutTransition>
                        <div
                            className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4"
                            role="alert">
                            <span className="block sm:inline">{t("SuccessMessage")}</span>
                        </div>
                    </EaseOutTransition>
                )}

                <form onSubmit={handleSubmit(updateProfile)}>
                    <TextField label={t("Name")}
                               name={"name"}
                               type={"text"}
                               placeholder={"John"}
                               errors={errors}
                               required={true}
                               register={register("name", {required: true})}
                    />

                    {/* enableEmailNotifications checkbox */}
                    <div className="flex flex-row items-center mb-4">
                        <input
                            id="enableEmailNotifications"
                            type="checkbox"
                            className="focus:ring-brand h-4 w-4 text-brand border-gray-300 rounded"
                            defaultChecked={user?.profile?.enableEmailNotifications || true}
                            {...register("enableEmailNotifications")}
                        />
                        <label htmlFor="enableEmailNotifications"
                               className="ml-2 block text-sm text-gray-900 dark:text-gray-300">
                            {t("EnableEmailNotifications")}
                        </label>
                    </div>

                    <CitySearch currentCity={user?.profile?.city || null} onCitySelected={onCitySelected}/>

                    <FormSubmitButton loading={isLoading} label={t("updateButton")}/>
                </form>
            </div>
        </>
    )
}