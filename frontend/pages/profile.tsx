import MainLayout from "../src/modules/common/components/mainLayout";

export default function Profile() {
    return (
        <MainLayout>
            <div className={"flex flex-col items-center justify-center"}>
                <div className={"text-center"}>
                    <h1 className={"text-3xl font-bold"}>Profile</h1>

                    <p className={"text-lg mt-4"}>
                        In development...
                    </p>
                </div>
            </div>
        </MainLayout>
    )
}