import React, {ReactNode} from "react";
import {Disclosure} from "@headlessui/react";
import Image from "next/image";
import flowerSvg from '../../../../public/assets/img/flower.svg';
import Link from "next/link";
import {NextRouter, useRouter} from "next/router";
import {authService} from "../../auth/services/authService";
import {useAppStateManager} from "../state";
import {classNames} from "../../../utils/classNames";
import UserDropdownMenu from "../../auth/components/userDropdownMenu";
import {UserDto} from "../../../api/apiSchemas";
import {CheckCircleIcon} from "@heroicons/react/20/solid";
import {useTranslations} from "use-intl";
import {Animate} from "./Animate";
import lightIcon from '../../../../public/assets/theme/light.svg';
import darkIcon from '../../../../public/assets/theme/dark.svg';
import {darkModeAtom} from "../atoms/darkModeAtom";
import {useAtom} from "jotai/index";
import {Footer} from "./Footer";

const Header = ({router}: { router: NextRouter }) => {
    const {user} = useAppStateManager()
    const from = authService.createUrlForRedirect(router)
    const [darkMode, setDarkMode] = useAtom(darkModeAtom)

    const toggleDarkMode = () => {
        setDarkMode(!darkMode)
    }

    return (
        <Disclosure as="nav">
            {({open}) => (
                <>
                    <header className="p-4 bg-white dark:bg-nav-bg text-gray-100 shadow">
                        {/* mobile navbar */}
                        <div className="xl:container mx-auto flex justify-between h-10 lg:hidden">
                            <div className="flex">
                                <Disclosure.Button>
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                         stroke="currentColor"
                                         className="w-6 h-6 navbar-menu-btn dark:text-white">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                              d="M4 6h16M4 12h16M4 18h16"></path>
                                    </svg>
                                </Disclosure.Button>
                                <Link href="/" aria-label="Back to homepage"
                                      className="flex items-center p-2">

                                    <ProjectLogo/>

                                    <span
                                        className="ml-2 text-dark font-poppins font-bold text-xl dark:text-light">Natai</span>
                                </Link>
                            </div>

                            <div className="flex">
                                {!user && (
                                    <Link
                                        href={"/registration" + from}
                                        className="self-center mr-2 px-8 rounded-3xl py-3 font-semibold rounded bg-brand hover:bg-opacity-20">
                                        Sign up
                                    </Link>
                                )}
                                <div
                                    onClick={toggleDarkMode}
                                    className="cursor-pointer rounded-full p-2 bg-gray-200 flex self-center dark:bg-darkish w-10 h-10">
                                    {darkMode ? (
                                        <Image
                                            src={darkIcon}
                                            alt={"dark theme icon"}
                                            className={"mx-auto"}
                                        />
                                    ) : (
                                        <Image
                                            src={lightIcon}
                                            alt={"light theme icon"}
                                        />
                                    )}
                                </div>
                            </div>
                        </div>
                        {/* desktop navbar */}
                        <div className="hidden xl:container mx-auto justify-between h-10 lg:flex">
                            <Link href="/" aria-label="Back to homepage"
                                  className="flex items-center p-2">
                                <ProjectLogo/>

                                <span
                                    className="ml-2 text-dark dark:text-light font-poppins font-bold text-xl">Natai</span>
                            </Link>
                            <DesktopNavBar router={router} darkMode={darkMode} onToggleDarkMode={toggleDarkMode}/>
                        </div>
                    </header>

                    <div className="relative z-10">
                        <Animate>
                            <Disclosure.Panel className={"absolute w-full"}>
                                <MobileNavBar router={router}/>
                            </Disclosure.Panel>
                        </Animate>
                    </div>
                </>
            )}
        </Disclosure>
    )
}


const DesktopNavBar = ({
                           router,
                           darkMode,
                           onToggleDarkMode
                       }: { router: NextRouter, darkMode: boolean, onToggleDarkMode: () => void }) => {
    const from = authService.createUrlForRedirect(router)

    const {user} = useAppStateManager()

    const isActive = (path: string) => {
        return router.pathname === path
    }

    const activeClass = "font-semibold text-brand dark:text-brand border-brand dark:border-brand"

    return (
        <>
            <ul className="items-stretch hidden space-x-3 lg:flex -my-4">
                <li className="flex">
                    <Link href={"/"}
                          className={classNames("flex items-center px-4 border-b-2 text-nav-item dark:text-nav-item-alt", isActive("/") ? activeClass : "border-transparent")}>
                        Home
                    </Link>
                </li>
                <li className="flex">
                    <Link href={"/diary"}
                          className={classNames("flex items-center px-4 border-b-2 text-nav-item dark:text-nav-item-alt", isActive("/diary") ? activeClass : "border-transparent")}>
                        My Diary
                    </Link>
                </li>
                <li className="flex">
                    <Link href={"/stories"}
                          className={classNames("flex items-center px-4 border-b-2 text-nav-item dark:text-nav-item-alt", isActive("/stories") ? activeClass : "border-transparent")}>
                        Stories
                    </Link>
                </li>
                <li className="flex">
                    <Link href={"/static/contacts"}
                          className={classNames("flex items-center px-4 border-b-2 text-nav-item dark:text-nav-item-alt", isActive("/static/contacts") ? activeClass : "border-transparent")}>
                        Contacts
                    </Link>
                </li>
            </ul>
            <div className="items-center flex-shrink-0 hidden lg:flex">
                {user ? (
                    <UserDropdownMenu user={user}/>
                ) : (
                    <>
                        <Link
                            href={"/login" + from}
                            className="self-center px-8 py-3 font-semibold rounded color-brand">
                            Sign in
                        </Link>
                        <Link
                            href={"/registration" + from}
                            className="self-center px-8 rounded-3xl py-3 font-semibold rounded bg-brand hover:bg-opacity-20">
                            Sign up
                        </Link>
                    </>
                )}

                <div
                    onClick={onToggleDarkMode}
                    className="cursor-pointer ml-4 rounded-full p-2 bg-gray-200 flex self-center dark:bg-darkish w-10 h-10">
                    {darkMode ? (
                        <Image
                            src={darkIcon}
                            alt={"dark theme icon"}
                            className={"mx-auto"}
                        />
                    ) : (
                        <Image
                            src={lightIcon}
                            alt={"light theme icon"}
                        />
                    )}
                </div>

            </div>
        </>
    )
}

function MobileUserDropdownMenu({user}: { user: UserDto }) {
    const t = useTranslations("MobileUserDropdownMenu")
    return (
        <div className="pt-4 pb-3 border-t border-sep dark:border-sep-alt text-left">
            <div className="flex flex-col items-center px-5">
                <div className="flex-shrink-0">
                    <div className="flex justify-center align-center h-10 w-10 rounded-full bg-brand">
                        <span className="font-bold text-white uppercase m-auto">
                            {user.name.at(0)}
                        </span>
                    </div>
                </div>
                <div className="my-3 w-full text-center">
                    <div
                        className="text-base font-medium leading-none text-nav-item dark:text-nav-item-alt mb-2">
                        {user.name}
                    </div>
                    <div className="text-sm font-medium leading-none text-nav-item dark:text-nav-item-alt">
                        {user.email}
                        {user.isEmailVerified && (<CheckCircleIcon className={"ml-1 inline text-green-600 h-4 w-4"}/>)}
                    </div>
                </div>
            </div>
            <div className="mt-3 px-2 space-y-1 text-center">
                <Link
                    href="/settings"
                    className={"px-4 -mb-1 border-b-2 border-transparent text-nav-item dark:text-nav-item-alt"}>
                    {t("Settings")}
                </Link>
            </div>
        </div>
    )
}

const MobileNavBar = ({router}: { router: NextRouter }) => {
    const {user} = useAppStateManager()
    const from = authService.createUrlForRedirect(router);
    const isActive = (path: string) => {
        return router.pathname === path
    }

    return (
        <>
            <ul className="bg-white dark:bg-nav-bg text-center shadow-2xl px-2 pt-2 pb-3 space-y-2 rounded-b-3xl">
                <li>
                    <Link
                        href={"/"}
                        className={classNames("text-nav-item dark:text-nav-item-alt block px-3 py-2 rounded-md text-base", isActive("/") ? "text-brand dark:text-brand font-semibold" : "font-medium")}>
                        Home
                    </Link>
                </li>
                <li>
                    <Link
                        href={"/diary"}
                        className={classNames("text-nav-item dark:text-nav-item-alt block px-3 py-2 rounded-md text-base", isActive("/diary") ? "text-brand dark:text-brand font-semibold" : "font-medium")}>
                        My Diary
                    </Link>
                </li>
                <li>
                    <Link
                        href={"/stories"}
                        className={classNames("text-nav-item dark:text-nav-item-alt block px-3 py-2 rounded-md text-base", isActive("/stories") ? "text-brand dark:text-brand font-semibold" : "font-medium")}>
                        Stories
                    </Link>
                </li>
                <li>
                    <Link
                        href={"/static/contacts"}

                        className={classNames("text-nav-item dark:text-nav-item-alt block px-3 py-2 rounded-md text-base", isActive("/static/contacts") ? "text-brand dark:text-brand font-semibold" : "font-medium")}>
                        Contacts
                    </Link>
                </li>
                {user ? (
                    <>
                        <MobileUserDropdownMenu user={user}/>
                    </>
                ) : (

                    <div className="pt-4 pb-3 border-t border-sep dark:border-sep-alt">
                        <div className="flex flex-col items-center px-5">
                            <li>
                                <Link
                                    href={"/login" + from}
                                    className={"nav-item-active block px-3 py-2 rounded-md text-base font-medium"}>
                                    Login
                                </Link>
                            </li>
                            <li className={"mt-4 mb-2"}>
                                <Link
                                    href={"/registration" + from}
                                    className={"self-center text-white mr-2 px-16 rounded-3xl py-3 font-semibold rounded bg-brand hover:bg-opacity-20"}>
                                    Sign Up
                                </Link>
                            </li>
                        </div>
                    </div>
                )}


            </ul>
        </>
    )
}

function MainLayout({children, containerClass}: { children: ReactNode, containerClass?: string }) {
    const router = useRouter()

    const [darkMode, setDarkMode] = useAtom(darkModeAtom)

    return (
        <div className={classNames("main-layout min-h-screen flex flex-col", darkMode && "dark")}>
            <Header router={router}/>
            <div className={classNames(containerClass || "flex-1 container mx-auto p-3 sm:p-0")}>
                {children}
            </div>
            <Footer/>
        </div>
    );
}

export const ProjectLogo = () => {
    return (
        <div
            className="flex items-center justify-center flex-shrink-0 w-12 h-12 rounded-full bg-flower">
            <Image src={flowerSvg} alt={"Flower Natai Diary Logo"} className={"w-7 h-7"}/>
        </div>
    )
}

export default MainLayout;