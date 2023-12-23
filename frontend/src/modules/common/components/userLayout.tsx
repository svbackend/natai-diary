import React, {ReactNode} from "react";
import Image from "next/image";
import flowerSvg from '../../../../public/assets/img/flower.svg';
import {useAppStateManager} from "../state";
import {NotLoggedIn} from "./NotLoggedIn";
import MainLayout from "./mainLayout";

export const UserLoadingScreen = (
    <div className="h-screen w-screen flex justify-center items-center dark:bg-gray-800">
        <div className="flex flex-col items-center">
            <Image src={flowerSvg} alt="Flower" width={100} height={100} className={"animate-spin"}/>
            <p className="text-gray-600 dark:text-gray-300">Loading...</p>
        </div>
    </div>
)

function UserLayout({children, containerClass}: { children: ReactNode, containerClass?: string }) {
    const {user, isLoading, isLoaded} = useAppStateManager()

    if (!isLoaded) {
        return UserLoadingScreen
    }

    if (!user) {
        return <NotLoggedIn/>
    }

    if (user) {
        return <MainLayout containerClass={containerClass}>{children}</MainLayout>
    }

    return null
}

export default UserLayout;