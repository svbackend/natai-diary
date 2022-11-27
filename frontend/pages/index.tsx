import MainLayout from "../src/modules/common/components/mainLayout";
import Image from "next/image";
import Link from "next/link";

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
    return (
        <MainLayout>
            <HeroSection/>
            <WhyYouShouldTryNataiDiarySection/>
        </MainLayout>
    )
}

function HeroSection() {
    return (
        <section className={"hero-section bg-hero rounded-lg shadow-sm"}>
            <div className="flex flex-col items-center justify-center mt-5 p-5 glass">
                <h1 className="text-4xl font-bold text-center text-white font-alegreya">Mental-health focused
                    journaling/diary app</h1>
                <p className="text-secondary text-xl text-center text-white font-alegreya-text mt-4">
                    Natai Diary is a diary ✍️ application with ☁️ cloud synchronization available for Android & WEB,
                    it helps to track your daily activities and how they affect your mood
                    <Image className={"w-8 h-8 inline"} src={care01} alt={"Natai Diary Mood Icon"}/>
                </p>

                <div className="flex flex-row items-center justify-center mt-5">
                    <Link href="https://play.google.com/store/apps/details?id=com.svbackend.natai"
                          className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-full border border-white shadow-lg">
                        Get it on Google Play
                    </Link>
                    <Link href={"/diary"}
                          className={"bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full ml-4 border border-white shadow-lg"}>
                        Try web version
                    </Link>
                </div>
            </div>
        </section>
    )
}

function WhyYouShouldTryNataiDiarySection() {

    const items = [
        {
            title: "It helps you to remember your daily life",
            description: "Stay more self-aware and mindful of what you do and how you feel"
        },
        {
            title: "It helps you to understand what brings you joy and happiness to focus on those things",
            description: "Journaling is a small habit that can make a big difference in your life"
        },
        {
            title: "It brings you closer to supporting and welcoming community",
            description: "Natai is not just an application, it's a community of people who share their struggles and victories."
        },
        {
            title: "It's free and open source, no ads whatsoever",
            description: ""
        }
    ]

    const WhyListItem = ({title, description}: { title: string, description: string }) => {
        return (
            <li className="text-xl text-white font-alegreya-text mt-4">
                <h3>{title}</h3>

                <p className="text-secondary text-sm text-white font-alegreya-text mt-2">
                    {description}
                </p>
            </li>
        )
    }

    return (
        <section className={"why-section bg-why rounded-lg shadow-sm"}>
            <div className="flex flex-col items-center justify-center mt-5 p-5 glass">
                <h1 className="text-4xl font-bold text-center text-white font-alegreya">Why you should try Natai Diary?</h1>

                <div className="flex flex-row w-full mt-5 px-5">
                    <ul className="list-disc">
                        {items.map((item, index) => <WhyListItem key={index} title={item.title} description={item.description}/>)}
                    </ul>
                </div>
            </div>
        </section>
    )
}