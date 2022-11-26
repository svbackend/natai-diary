import MainLayout from "../src/modules/common/components/mainLayout";
import {useForm} from "react-hook-form";
import {TextField} from "../src/modules/common/components/textField";
import {FormSubmitButton} from "../src/modules/common/components/FormSubmitButton";

type FormValues = {
    name: string
    email: string
    password: string
}

export default function RegistrationPage() {

    const {register, handleSubmit, formState: {errors}} = useForm<FormValues>();

    const registerUser = (data: FormValues) => {
        console.log(data)
    }

    return (
        <>
            <MainLayout>
                <div className="mx-auto flex flex-col max-w-md rounded-md">
                    <div className="mb-8 text-center">
                        <h1 className="my-3 text-4xl font-bold">Registration</h1>
                        <p>Register to get access to the dashboard</p>
                    </div>

                    <form onSubmit={handleSubmit(registerUser)}>
                        <TextField label={"Name"} name={"name"} type={"text"} placeholder={"John Doe"}
                                   register={register}/>
                        <TextField label={"Email"} name={"email"} type={"email"} placeholder={"john.doe@example.com"}
                                   register={register}/>
                        <TextField label={"Password"} name={"password"} type={"password"} placeholder={"********"}
                                   register={register}/>

                        <FormSubmitButton label={"Register"}/>
                    </form>
                </div>
            </MainLayout>
        </>
    )
}
