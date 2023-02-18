import MainLayout from "../src/modules/common/components/mainLayout";
import Image from "next/image";
import Link from "next/link";
import circlesImg from "../public/assets/hero/circles.svg";
import circlesDarkImg from "../public/assets/hero/circles-dark.svg";
import whySectionImg from "../public/assets/img/abstract-circles.svg";
import featuresSectionImg from "../public/assets/img/bullseye-gradient.svg";
import {useAtom} from "jotai/index";
import {darkModeAtom} from "../src/modules/common/atoms/darkModeAtom";

const care01 = require("../public/assets/img/care01.svg");

/**
 * Natai Diary landing & home page
 *
 * Why you should try Natai Diary?
 * - It helps you to remember your daily life
 * - It helps you to understand what brings you joy and happiness to focus on those things
 * - It helps you to stay more self-aware and mindful
 * - Journaling is a small habit that can make a big difference in your life
 * - It brings you closer to supporting and welcoming community
 * - It's free and open source, no ads whatsoever
 *
 * It's proven that journaling helps your mental health and well-being. Start making small notes today!
 *
 * Features:
 * Track your mood daily via fun hand-drawn art
 * Track your activities via tags - with no limits, you can create your own tags
 * On "Insights" tab you can see how your activities and mood are correlated
 * Cloud synchronization - don't lose your data switching between devices
 * Web and mobile apps - you can use it on your phone, tablet or computer whatever works best for you at the moment
 * Offline-first - you can use it even when you don't have internet connection, all notes will be synced as soon as you get back online
 */
export default function HomeLandingPage() {
    const [isDarkMode] = useAtom(darkModeAtom)

    return (
        <MainLayout containerClass={"landing"}>
            <HeroSection isDarkMode={isDarkMode}/>
            <WhyYouShouldTryNataiDiarySection/>
            <FeaturesSection/>
        </MainLayout>
    )
}

function HeroSection({isDarkMode}: { isDarkMode: boolean }) {
    let screenshot1, screenshot2;

    if (isDarkMode) {
        screenshot1 = require("../public/assets/hero/screenshot1-dark.png");
        screenshot2 = require("../public/assets/hero/screenshot2-dark.png");
    } else {
        screenshot1 = require("../public/assets/hero/screenshot1.png");
        screenshot2 = require("../public/assets/hero/screenshot2.png");
    }

    const heroArtStyle = {
        backgroundImage: isDarkMode ? `url('${circlesDarkImg.src}')` : `url('${circlesImg.src}')`,
        backgroundRepeat: "no-repeat",
        backgroundPosition: "right",
    }

    return (
        <section className={"relative hero-section rounded-b-3xl"}>
            <div className="bg-section rounded-b-3xl"></div>
            <div className="bg-hero rounded-b-3xl"></div>
            <div
                className="xl:container mx-auto flex flex-col lg:flex-row lg:justify-between px-4 lg:px-2 pt-7 lg:pt-24 pb-16 lg:pb-28">

                <div className="flex flex-col">
                    <h1 className="text-3xl lg:text-4xl font-bold text-dark dark:text-light leading-10 text-center lg:text-left">
                        <span className="whitespace-nowrap">Mental-health focused</span><br/>
                        <span className="whitespace-nowrap">journaling & diary app</span>
                    </h1>
                    <p className="mx-auto text-secondary text-white dark:text-nav-item-alt mt-5 text-center lg:text-left">
                        Natai Diary is a diary application with cloud synchronization available for Android & WEB,
                        it helps to track your daily activities and how they affect your mood
                        <Image className={"w-8 h-8 inline"} src={care01} alt={"Natai Diary Mood Icon"}/>
                    </p>

                    <div className="flex flex-col lg:flex-row mt-4 lg:mt-8 px-8 lg:px-0">
                        <Link href="https://play.google.com/store/apps/details?id=com.svbackend.natai"
                              target={"_blank"}
                              className="flex bg-google shadow hover:shadow-2xl py-4 px-12 rounded-full">
                            <Image className={"mx-auto"} src={require("../public/assets/button/playStore.png")}
                                   alt={"get it on Google Play"}/>
                        </Link>
                        <Link href={"/diary"}
                              className={"w-full h-full flex items-center justify-center lg:w-auto bg-brand text-center text-white self-center mt-4 lg:mt-0 py-5 px-12 rounded-full ml-0 lg:ml-4 shadow font-semibold hover:shadow-xl"}>
                            Try Web Version
                        </Link>
                    </div>
                </div>

                <div className="flex relative lg:-mt-24 lg:-mb-28 lg:flex-grow">
                    <div className="hero-art bg-cover lg:bg-contain" style={heroArtStyle}>
                        <div className="hero-art-screenshots">
                            <Image className={"hero-screenshot-1"} src={screenshot1} alt={"Natai Diary Screenshot 1"}/>
                            <Image className={"hero-screenshot-2"} src={screenshot2} alt={"Natai Diary Screenshot 2"}/>
                            <Image className={"w-16 h-16 drop-shadow hero-mood-1"}
                                   src={require("../public/assets/mood/10.svg")} alt={"Natai Diary Mood 1"}/>
                            <Image className={"w-16 h-16 drop-shadow hero-mood-2"}
                                   src={require("../public/assets/mood/8.svg")} alt={"Natai Diary Mood 2"}/>
                            <Image className={"w-16 h-16 drop-shadow hero-mood-3"}
                                   src={require("../public/assets/mood/3.svg")} alt={"Natai Diary Mood 3"}/>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    )
}

function WhyYouShouldTryNataiDiarySection() {

    const items = [
        {
            title: "It helps you to remember your daily life",
            description: "Stay more grounded, self-aware, mindful of what you do and how you feel"
        },
        {
            title: "Journaling is a small habit that can make a big difference in your life",
            description: "Start making notes, build 1 good habit at a time and see how it changes your life"
        },
        {
            title: "It brings you closer to supporting and welcoming community",
            description: "Natai is not just an application, it's a community of people who share their struggles and victories."
        },
        {
            title: "It's free and open source, no ads whatsoever",
            description: "Built with ❤️ by a small team of developers, designers and mental health advocates"
        }
    ]

    const bgStyle = {
        backgroundImage: `url('${whySectionImg.src}')`,
    }

    const WhyCardItem = ({title, description}: { title: string, description: string }) => {
        return (
            <div className="flex flex-col card glass text-xl text-white font-alegreya-text p-4">
                <h3>{title}</h3>

                <p className="text-sm text-white mt-2">
                    {description}
                </p>
            </div>
        )
    }

    return (
        <section className={"why-section bg-why rounded-lg shadow-sm"} style={bgStyle}>
            <div className="flex flex-col items-center justify-center mt-5 p-5 sm:p-10">
                <h1 className="text-4xl font-bold text-center text-white font-alegreya">Why you should try Natai
                    Diary?</h1>

                <div className="grid grid-cols-1 sm:grid-cols-2 grid-flow-row w-full mt-5 px-5 gap-4">
                    {items.map((item, index) => <WhyCardItem key={index} title={item.title}
                                                             description={item.description}/>)}
                </div>

                <div className="flex flex-row items-center justify-center mt-5">
                    <Link href={"/stories"}
                          className={"bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full ml-4 border border-white shadow-lg"}>
                        Read community stories
                    </Link>
                </div>
            </div>
        </section>
    )
}

function FeaturesSection() {
    const items = [
        {
            title: "Track your mood daily via fun hand-drawn art",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life."
        },
        {
            title: "Track your activities via tags - with no limits, you can create your own tags",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life."
        },
        {
            title: "On \"Insights\" tab you can see how your activities and mood are correlated",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life."
        },
        {
            title: "Cloud synchronization - don't lose your data switching between devices",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life."
        },
        {
            title: "Web and mobile apps - you can use it on your phone, tablet or computer whatever works best for you at the moment",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life."
        },
        {
            title: "Offline-first - you can use it even when you don't have internet connection, all notes will be synced as soon as you get back online",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life."
        }
    ]

    const FeatureCardItem = ({title, description}: { title: string, description: string }) => {
        return (
            <div className="flex flex-col card glass text-xl text-white font-alegreya-text p-4">
                <h3>{title}</h3>

                <p className="text-sm text-white mt-2">
                    {description}
                </p>
            </div>
        )
    }

    const bgStyle = {
        backgroundImage: `url('${featuresSectionImg.src}')`,
    }

    return (
        <section className={"features-section bg-features rounded-lg shadow-sm mb-12"} style={bgStyle}>
            <div className="flex flex-col items-center justify-center mt-5 p-5 sm:p-10">
                <h1 className="text-4xl font-bold text-center text-white font-alegreya">Features</h1>

                <div className="grid grid-cols-1 sm:grid-cols-2 grid-flow-row w-full mt-5 px-5 gap-4">
                    {items.map((item, index) => <FeatureCardItem key={index} title={item.title}
                                                                 description={item.description}/>)}
                </div>
            </div>
        </section>
    )
}