import {ReactNode} from "react";
import {Disclosure, Transition} from "@headlessui/react";
import Image from "next/image";
import flowerSvg from '../../../../public/assets/img/flower.svg';
import Link from "next/link";
import {NextRouter, useRouter} from "next/router";
import {authService} from "../../auth/services/authService";
import {useAppStateManager} from "../state";
import {classNames} from "../../../utils/classNames";
import UserDropdownMenu from "../../auth/components/userDropdownMenu";


const Header = ({router}: { router: NextRouter }) => {
    const {isLoading} = useAppStateManager()

    return (
        <Disclosure as="nav">
            {({open}) => (
                <>
                    <header className="p-4 bg-gray-800 text-gray-100">
                        <div className="container flex justify-between h-10 mx-auto">
                            <Link href="/" aria-label="Back to homepage"
                                  className="flex items-center p-2">

                                <ProjectLogo/>

                                <span className="ml-2 text-xl">Natai</span>
                            </Link>
                            <DesktopNavBar router={router}/>
                            <Disclosure.Button className="lg:hidden">
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                     stroke="currentColor"
                                     className="w-6 h-6 text-gray-100">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                          d="M4 6h16M4 12h16M4 18h16"></path>
                                </svg>
                            </Disclosure.Button>
                        </div>
                    </header>

                    <Transition
                        enter="transition duration-100 ease-out"
                        enterFrom="transform scale-95 opacity-0"
                        enterTo="transform scale-100 opacity-100"
                        leave="transition duration-75 ease-out"
                        leaveFrom="transform scale-100 opacity-100"
                        leaveTo="transform scale-95 opacity-0"
                    >
                        <Disclosure.Panel>
                            <MobileNavBar router={router}/>
                        </Disclosure.Panel>
                    </Transition>
                </>
            )}
        </Disclosure>
    )
}


const DesktopNavBar = ({router}: { router: NextRouter }) => {
    const from = authService.createUrlForRedirect(router)

    const {user} = useAppStateManager()

    const isActive = (path: string) => {
        return router.pathname === path
    }

    return (
        <>
            <ul className="items-stretch hidden space-x-3 lg:flex">
                <li className="flex">
                    <Link href={"/"}
                          className={classNames("flex items-center px-4 -mb-1 border-b-2 border-transparent", isActive("/") ? "text-violet-400 border-violet-400" : "")}>
                        Home
                    </Link>
                </li>
                <li className="flex">
                    <Link href={"/diary"}
                          className={classNames("flex items-center px-4 -mb-1 border-b-2 border-transparent", isActive("/diary") ? "text-violet-400 border-violet-400" : "")}>
                        My Diary
                    </Link>
                </li>
                <li className="flex">
                    <Link href={"/stories"}
                          className={classNames("flex items-center px-4 -mb-1 border-b-2 border-transparent", isActive("/stories") ? "text-violet-400 border-violet-400" : "")}>
                        Stories
                    </Link>
                </li>
                <li className="flex">
                    <Link href={"/static/contacts"}
                          className={classNames("flex items-center px-4 -mb-1 border-b-2 border-transparent", isActive("/static/contacts") ? "text-violet-400 border-violet-400" : "")}>
                        Contacts
                    </Link>
                </li>
            </ul>
            <div className="items-center flex-shrink-0 hidden lg:flex">
                {user ? (
                    <UserDropdownMenu user={user}/>
                ) : (
                    <>
                        <Link href={"/login" + from} className="self-center px-8 py-3 rounded">Sign in</Link>
                        <Link href={"/registration" + from}
                              className="self-center px-8 py-3 font-semibold rounded bg-violet-400 text-gray-900">Sign
                            up</Link>
                    </>
                )}

            </div>
        </>
    )
}

const MobileNavBar = ({router}: { router: NextRouter }) => {
    const {user} = useAppStateManager()
    const from = authService.createUrlForRedirect(router);

    return (
        <>
            <ul className="bg-gray-600 px-2 pt-2 pb-3 space-y-1 sm:px-3">
                <li>
                    <Link href={"/"}
                          className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Home</Link>
                </li>
                <li>
                    <Link href={"/diary"}
                          className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">My
                        Diary</Link>
                </li>
                <li>
                    <Link href={"/stories"}
                          className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Stories</Link>
                </li>
                <li>
                    <Link href={"/static/contacts"}
                          className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Contacts</Link>
                </li>
                {user ? (
                    <>
                        <li>
                            <Link href={"/login" + from}
                                  className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Login</Link>
                        </li>
                        <li>
                            <Link href={"/registration" + from}
                                  className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Registration</Link>
                        </li>
                    </>
                ) : (
                    <>
                        <li>
                            <Link href={"/login" + from}
                                  className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Login</Link>
                        </li>
                        <li>
                            <Link href={"/registration" + from}
                                  className="text-gray-300 hover:bg-gray-700 hover:text-white block px-3 py-2 rounded-md text-base font-medium">Registration</Link>
                        </li>
                    </>
                )}


            </ul>
        </>
    )
}

const Footer = () => {
    return (
        <footer className="px-4 py-8 bg-gray-800 text-gray-400">
            <div
                className="container flex flex-wrap items-center justify-center mx-auto space-y-4 sm:justify-between sm:space-y-0">
                <div className="flex flex-row pr-3 space-x-4 sm:space-x-8">
                    <ProjectLogo/>
                    <ul className="flex flex-wrap items-center space-x-4 sm:space-x-8">
                        <li>
                            <Link href={"/static/terms"}>Terms of Use</Link>
                        </li>
                        <li>
                            <Link href={"/static/privacy"}>Privacy</Link>
                        </li>
                    </ul>
                </div>
                <ul className="flex flex-wrap pl-3 space-x-4 sm:space-x-8">
                    <li>
                        <Link href="#">Instagram</Link>
                    </li>
                    <li>
                        <Link href="#">Facebook</Link>
                    </li>
                    <li>
                        <Link href="#">Twitter</Link>
                    </li>
                </ul>
            </div>
        </footer>
    )
}

const MainLayout = ({children}: { children: ReactNode }) => {
    const router = useRouter()
    return (
        <div className="min-h-screen flex flex-col">
            <Header router={router}/>
            <div className="flex-1 container mx-auto p-3 sm:p-0">
                {children}
            </div>
            <Footer/>
        </div>
    );
}

export const ProjectLogo = () => {
    return (
        <div
            className="flex items-center justify-center flex-shrink-0 w-12 h-12 rounded-full bg-brand">
            <Image src={flowerSvg} alt={"Flower Natai Diary Logo"} className={"w-7 h-7"}/>
        </div>
    )
}

export default MainLayout;