import Link from "next/link";
import MainLayout from "../../common/components/mainLayout";

export const AlreadyLoggedIn = () => {
    return (
        <MainLayout>
            <div className="mx-auto flex flex-col max-w-md rounded-md">
                <div className="mb-8 text-center">
                    <h1 className="my-3 text-4xl font-bold">You are already logged in</h1>
                    <Link href={"/"} className={"text-blue-500"}>Go to home page</Link>
                </div>
            </div>
        </MainLayout>
    )
}