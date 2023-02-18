import Link from "next/link";
import Image from "next/image";
import instagramIcon from "../../../../public/assets/social/instagram.svg";
import facebookIcon from "../../../../public/assets/social/fb.svg";
import twitterIcon from "../../../../public/assets/social/twitter.svg";
import {ProjectLogo} from "./mainLayout";

export function Footer() {
    return (
        <footer className="bg-white dark:bg-nav-bg">
            <div className="container flex flex-col lg:flex-row lg:justify-between mx-auto py-8">
                <div className="flex flex-col items-center lg:items-start">
                    <div className="flex items-center">
                        <ProjectLogo/>
                        <span className="ml-2 text-dark dark:text-light font-poppins font-bold text-xl">Natai</span>
                    </div>

                    <div className="flex mt-4">
                        <p className={"text-nav-item dark:text-nav-item-alt"}>Mental-health journaling & diary app</p>
                    </div>
                </div>

                <div className="flex flex-col items-center lg:items-end mt-8 lg:mt-0">
                    <ul className="flex flex-row gap-2">
                        <li className={"flex items-center w-12 h-12 bg-brand rounded-full"}>
                            <Link href="https://www.instagram.com/natai.app/" target="_blank" rel="noreferrer"
                                  className={"mx-auto"}>
                                <InstagramIcon className="h-6 w-6 text-nav-item dark:text-nav-item-alt"/>
                            </Link>
                        </li>
                        <li className={"flex items-center w-12 h-12 bg-brand rounded-full"}>
                            <Link href="https://www.facebook.com/natai.app/" target="_blank" rel="noreferrer"
                                  className={"mx-auto"}>
                                <FacebookIcon className="h-6 w-6 text-nav-item dark:text-nav-item-alt"/>
                            </Link>
                        </li>
                        <li className={"flex items-center w-12 h-12 bg-brand rounded-full"}>
                            <Link href="https://twitter.com/natai_app" target="_blank" rel="noreferrer"
                                  className={"mx-auto"}>
                                <TwitterIcon className="h-6 w-6 text-nav-item dark:text-nav-item-alt"/>
                            </Link>
                        </li>

                    </ul>
                    <div className="flex flex-row mt-4">
                        <ul className="flex flex-wrap pl-3 space-x-4 sm:space-x-8">
                            <li>
                                <Link
                                    href="/static/terms"
                                    className={"font-semibold text-nav-item dark:text-nav-item-alt"}>
                                    Terms of Use
                                </Link>
                            </li>
                            <li>
                                <Link
                                    href="/static/privacy"
                                    className={"font-semibold text-nav-item dark:text-nav-item-alt"}>
                                    Privacy Policy
                                </Link>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <div className="flex flex-row self-center py-4 bg-light2 dark:bg-copyright">
                <p className={"text-center mx-auto text-sm text-nav-item dark:text-nav-item-alt"}>
                    &copy; Copyright 2023 Natai, All rights reserved.
                </p>
            </div>
        </footer>
    )
}

function InstagramIcon(props: any) {
    return (
        <Image src={instagramIcon} alt={"Natai Diary on Instagram"} {...props} />
    )
}

function FacebookIcon(props: any) {
    return (
        <Image src={facebookIcon} alt={"Natai Diary on Facebook"} {...props} />
    )
}

function TwitterIcon(props: any) {
    return (
        <Image src={twitterIcon} alt={"Natai Diary on Twitter"} {...props} />
    )
}