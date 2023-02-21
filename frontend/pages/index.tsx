import MainLayout from "../src/modules/common/components/mainLayout";
import Image from "next/image";
import Link from "next/link";
import circlesImg from "../public/assets/hero/circles.svg";
import circlesDarkImg from "../public/assets/hero/circles-dark.svg";
import whySectionGirlImg from "../public/assets/why/girl-new.png";
import whySectionGirlDarkImg from "../public/assets/why/girl-dark.png";

import screenshot1Img from "../public/assets/hero/screenshot1.png"
import screenshot2Img from "../public/assets/hero/screenshot2.png"
import screenshot1DarkImg from "../public/assets/hero/screenshot1-dark.png"
import screenshot2DarkImg from "../public/assets/hero/screenshot2-dark.png"
import pencilIcon from "../public/assets/icons/pencil.svg"
import coupleIcon from "../public/assets/icons/couple.svg"
import plantsIcon from "../public/assets/icons/plants.svg"
import receiveIcon from "../public/assets/icons/receive.svg"
import cloudIcon from "../public/assets/icons/cloud.svg"
import adsIcon from "../public/assets/icons/ads.svg"
import wifiIcon from "../public/assets/icons/wifi.svg"

import mood10 from "../public/assets/mood/10.svg"
import mood9 from "../public/assets/mood/9.svg"
import mood8 from "../public/assets/mood/8.svg"
import mood7 from "../public/assets/mood/7.svg"
import mood6 from "../public/assets/mood/6.svg"
import mood5 from "../public/assets/mood/5.svg"
import mood4 from "../public/assets/mood/4.svg"
import mood3 from "../public/assets/mood/3.svg"
import mood2 from "../public/assets/mood/2.svg"

import feat1Img from "../public/assets/features/feat1.png"
import feat2Img from "../public/assets/features/feat2-desktop.png"

import {useAtom} from "jotai/index";
import {darkModeAtom} from "../src/modules/common/atoms/darkModeAtom";

import {Swiper, SwiperSlide} from "swiper/react";
import {Pagination} from "swiper";
import "swiper/css";
import "swiper/css/pagination";
import {classNames} from "../src/utils/classNames";

const care01 = require("../public/assets/img/care01.svg");

export default function HomeLandingPage() {
    const [isDarkMode] = useAtom(darkModeAtom)

    return (
        <MainLayout containerClass={"landing"}>
            <HeroSection isDarkMode={isDarkMode}/>
            <WhyYouShouldTryNataiDiarySection isDarkMode={isDarkMode}/>
            <FeaturesSection isDarkMode={isDarkMode}/>
            <UseAnywhereSection isDarkMode={isDarkMode}/>
        </MainLayout>
    )
}

function HeroSection({isDarkMode}: { isDarkMode: boolean }) {
    let screenshot1, screenshot2;

    if (isDarkMode) {
        screenshot1 = screenshot1DarkImg;
        screenshot2 = screenshot2DarkImg;
    } else {
        screenshot1 = screenshot1Img;
        screenshot2 = screenshot2Img;
    }

    const heroArtStyle = {
        backgroundImage: isDarkMode ? `url('${circlesDarkImg.src}')` : `url('${circlesImg.src}')`,
        backgroundRepeat: "no-repeat",
        backgroundPosition: "right",
    }

    return (
        <section className={"relative hero-section overflow-hidden"}>
            <div className="bg-section"></div>
            <div className="bg-hero"></div>
            <div
                className="xl:container mx-auto flex flex-col lg:flex-row lg:justify-between px-4 lg:px-2 pt-7 lg:pt-24 pb-16 lg:pb-44">

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
                              className="flex bg-google shadow hover:shadow-2xl py-2 px-8 lg:py-4 lg:px-12 rounded-full">
                            <Image className={"mx-auto"} src={require("../public/assets/button/playStore.png")}
                                   priority={true}
                                   quality="100"
                                   alt={"get it on Google Play"}/>
                        </Link>
                        <Link href={"/diary"}
                              className={"w-full h-full flex items-center justify-center lg:w-auto bg-brand text-center text-white self-center mt-4 lg:mt-0 py-3 px-8 lg:py-5 lg:px-12 rounded-full ml-0 lg:ml-4 shadow font-semibold hover:shadow-xl"}>
                            Try Web Version
                        </Link>
                    </div>
                </div>

                <div className="flex relative lg:-mt-24 lg:-mb-28 lg:flex-grow">
                    <div className="hero-art bg-cover lg:-mb-28 lg:bg-auto" style={heroArtStyle}>
                        <div className="hero-art-screenshots">
                            <Image
                                className={"hero-screenshot-1"}
                                priority={true}
                                src={screenshot1}
                                alt={"Natai Diary Screenshot 1"}
                                placeholder={"blur"}
                            />
                            <Image
                                className={"hero-screenshot-2"}
                                priority={true}
                                src={screenshot2}
                                alt={"Natai Diary Screenshot 2"}
                                placeholder={"blur"}
                            />
                            <Image
                                className={"w-16 h-16 drop-shadow hero-mood-1"}
                                src={require("../public/assets/mood/10.svg")} alt={"Natai Diary Mood 1"}
                            />
                            <Image
                                className={"w-16 h-16 drop-shadow hero-mood-2"}
                                src={require("../public/assets/mood/8.svg")} alt={"Natai Diary Mood 2"}
                            />
                            <Image
                                className={"w-16 h-16 drop-shadow hero-mood-3"}
                                src={require("../public/assets/mood/3.svg")}
                                alt={"Natai Diary Mood 3"}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </section>
    )
}

function WhyYouShouldTryNataiDiarySection({isDarkMode}: { isDarkMode: boolean }) {

    const items = [
        {
            title: "Remember your daily life",
            description: "Stay more grounded, self-aware, mindful of what you do and how you feel.",
            img: pencilIcon,
        },
        {
            title: "Build good habits",
            description: "Start making notes, build 1 good habit at a time and see how it changes your life.",
            img: plantsIcon,
        },
        {
            title: "Supportive community",
            description: "Natai is not just an application, it's a community of people who share their struggles and victories.",
            img: coupleIcon,
        },
        {
            title: "It's free and open-source",
            description: "Built with ❤️ by a small team of developers, designers and mental health advocates.",
            img: receiveIcon,
        }
    ]

    const girlImg = isDarkMode ? whySectionGirlDarkImg : whySectionGirlImg

    const WhyCardItem = ({img, title, description}: { img: any, title: string, description: string }) => {
        return (
            <div
                className="flex flex-col px-4 py-4 pt-8 bg-white dark:bg-why rounded-3xl why-card-item relative max-w-xs h-full">
                <div className="flex absolute p-4 -top-12 w-20 h-20 rounded-full bg-light2 dark:bg-why">
                    <Image
                        className={"w-16 h-16 mx-auto self-center"}
                        src={img}
                        alt={`${title} icon`}
                    />
                </div>

                <h3 className={"mt-2 text-xl text-dark dark:text-light font-bold"}>{title}</h3>

                <p className="text-nav-item dark:text-nav-item-alt mt-2">
                    {description}
                </p>
            </div>
        )
    }

    return (
        <section className={"why-section bg-whitish dark:bg-nav-bg py-12 lg:py-20 px-4 lg:px-0"}>
            <h2 className={"text-3xl text-dark dark:text-light font-bold text-center"}>
                Why you should try&nbsp;
                <span className={"text-brand whitespace-nowrap relative z-10"}>
                    Natai Diary?
                    <span className="brand-highlight w-[200px]"></span>
                </span>
            </h2>

            <div className="container mx-auto flex justify-between mt-12 lg:mt-28 ">
                <div className="hidden lg:flex">
                    <Image src={girlImg} alt={"Girl during journaling"}/>
                </div>
                <div className="hidden lg:flex lg:justify-end">
                    <div className="grid grid-cols-2 gap-x-8 gap-y-20">
                        {items.map((item, index) => {
                            return (
                                <WhyCardItem
                                    key={`why-desktop-${index}`}
                                    img={item.img}
                                    title={item.title}
                                    description={item.description}
                                />
                            )
                        })}
                    </div>
                </div>
                <div className="lg:hidden relative flex flex-col w-full swiper-min-height">
                    <div className="flex">
                        <Swiper
                            slidesPerView={1.2}
                            spaceBetween={30}
                            pagination={{
                                clickable: true,
                            }}
                            modules={[Pagination]}
                            className="mySwiper"
                        >
                            {items.map((item, index) => {
                                return (
                                    <SwiperSlide key={`why-mobile-${index}`}>
                                        <WhyCardItem
                                            img={item.img}
                                            title={item.title}
                                            description={item.description}
                                        />
                                    </SwiperSlide>
                                )
                            })}
                        </Swiper>
                    </div>

                    <Link href={"/registration"}
                          className={"mt-4 w-full rounded-3xl text-center font-semibold py-3 mx-auto bg-brand text-white"}>
                        Create an account
                    </Link>
                </div>

            </div>
        </section>
    )
}

function FeaturesSection({isDarkMode}: { isDarkMode: boolean }) {
    const items = [
        {
            title: "Track your mood daily via fun hand-drawn art",
            description: "Natai Diary has a unique feature to track your mood daily via fun hand-drawn art, it helps you to understand your emotions better and how they affect your daily life.",
            img: feat1Img,
            smiles: [mood9, mood4, mood3]
        },
        {
            title: "Track your activities via tags - with no limits, you can create your own tags",
            description: "Gain valuable insights into your well-being and make positive changes in your life by analyzing your tagged activities over time.",
            img: feat2Img,
            smiles: [mood7, mood8, mood5]
        },
        {
            title: "You can see how your activities and mood are correlated",
            description: "Natai Diary helps you to visualize the link between your daily activities and mood, giving you a better understanding of how to optimize your mental well-being.",
            img: feat1Img,
            smiles: [mood2, mood6, mood10]
        },
    ]

    const FeatureCardItem1 = (
        {title, description, img, smiles}: {
            title: string,
            description: string,
            img: any,
            smiles: any[]
        }
    ) => {
        return (
            <div className="flex flex-col mt-72 relative px-8 lg:px-0">
                <div
                    className="relative flex flex-col lg:flex-row lg:justify-between items-center xl:container mx-auto rounded-3xl overflow-hidden">
                    <div className="bg-feature-card rounded-3xl overflow-hidden"></div>
                    <div className="bg-feature-card-circles rounded-3xl overflow-hidden"></div>

                    <div className="flex flex-col lg:max-w-md mt-36 mb-8 px-4 lg:px-0 lg:my-20 lg:ml-28">
                        <h3 className={"font-bold text-lg lg:text-2xl text-dark dark:text-light"}>{title}</h3>

                        <p className="mt-6 text-nav-item dark:text-nav-item-alt">
                            {description}
                        </p>
                    </div>
                </div>

                <div className="flex relative items-end container mx-auto rounded-3xl">
                    <Image
                        className={"absolute feature-screenshot-right"}
                        src={img}
                        alt={`Feature '${title}' screenshot`}
                    />
                    <Image
                        className={"absolute feature-lines-right"}
                        src={require("../public/assets/features/lines.svg")}
                        alt={`Lines decoration`}
                    />

                    {/** features-mood-1 features-mood-2 features-mood-3 */}
                    {smiles.map((moodSmileImg, idx) => (
                        <Image
                            key={`features-mood-${1 + idx}`}
                            className={classNames("w-16 h-16 drop-shadow", `features-mood-${1 + idx}`)}
                            src={moodSmileImg} alt={"Natai Diary Mood Icon"}
                        />
                    ))}
                </div>
            </div>
        )
    }

    const FeatureCardItem2 = (
        {title, description, img, smiles}: {
            title: string,
            description: string,
            img: any,
            smiles: any[]
        }
    ) => {
        return (
            <div className="flex flex-col mt-72 relative px-8 lg:px-0">
                <div
                    className="relative flex flex-col lg:flex-row lg:justify-between items-center xl:container mx-auto rounded-3xl overflow-hidden">
                    <div className="bg-feature-card rounded-3xl overflow-hidden"></div>
                    <div className="bg-feature-card-circles rounded-3xl overflow-hidden"></div>

                    <div className="flex flex-col lg:max-w-md mt-36 mb-8 px-4 lg:px-0 lg:my-20 lg:ml-28">
                        <h3 className={"font-bold text-lg lg:text-2xl text-dark dark:text-light"}>{title}</h3>

                        <p className="mt-6 text-nav-item dark:text-nav-item-alt">
                            {description}
                        </p>
                    </div>
                </div>

                <div className="flex relative items-end container mx-auto rounded-3xl">
                    <Image
                        className={"absolute feature-screenshot-left"}
                        src={img}
                        alt={`Feature '${title}' screenshot`}
                    />
                    <Image
                        className={"absolute feature-lines-left"}
                        src={require("../public/assets/features/lines.svg")}
                        alt={`Lines decoration`}
                    />

                    {/** features-mood-1 features-mood-2 features-mood-3 */}
                    {smiles.map((moodSmileImg, idx) => (
                        <Image
                            key={`features-mood-${1 + idx}`}
                            className={classNames("w-16 h-16 drop-shadow left", `features-mood-${1 + idx}`)}
                            src={moodSmileImg} alt={"Natai Diary Mood Icon"}
                        />
                    ))}
                </div>
            </div>
        )
    }

    return (
        <section className={"relative features-section overflow-hidden"}>
            <div className="bg-section-features"></div>

            <h2 className={"text-3xl text-dark dark:text-light font-bold text-center mt-4 lg:mt-8"}>
                <span className={"text-brand whitespace-nowrap relative z-10"}>
                    Natai Diary
                    <span className="brand-highlight w-[184px]"></span>
                </span>
                &nbsp;as your friend!
            </h2>

            <div className="flex flex-col">
                {items.map((row, idx) => (
                    idx % 2 === 0 ? (
                        <FeatureCardItem1
                            key={`feature-${idx}`}
                            title={row.title}
                            description={row.description}
                            img={row.img}
                            smiles={row.smiles}
                        />
                    ) : (
                        <FeatureCardItem2
                            key={`feature-${idx}`}
                            title={row.title}
                            description={row.description}
                            img={row.img}
                            smiles={row.smiles}
                        />
                    )
                ))}
            </div>
        </section>
    )
}

function UseAnywhereSection({isDarkMode}: { isDarkMode: boolean }) {

    const items = [
        {
            title: "Cloud synchronization",
            description: "We keep your progress, ensuring your data is always accessible on all your devices.",
            img: cloudIcon,
        },
        {
            title: "Web and mobile apps",
            description: "You can use it on your phone, tablet or computer whatever works best for you at the moment.",
            img: adsIcon,
        },
        {
            title: "Offline first",
            description: "You can use it even when you don't have internet connection, all notes will be synced once you get back online.",
            img: wifiIcon,
        },
    ]

    const WhyCardItem = ({img, title, description}: { img: any, title: string, description: string }) => {
        return (
            <div
                className="flex flex-col px-4 py-4 pt-8 bg-white dark:bg-why rounded-3xl why-card-item relative max-w-xs h-full">
                <div className="flex absolute p-4 -top-12 w-20 h-20 rounded-full bg-light2 dark:bg-why">
                    <Image
                        className={"w-16 h-16 mx-auto self-center"}
                        src={img}
                        alt={`${title} icon`}
                    />
                </div>

                <h3 className={"mt-2 text-xl text-dark dark:text-light font-bold"}>{title}</h3>

                <p className="text-nav-item dark:text-nav-item-alt mt-2">
                    {description}
                </p>
            </div>
        )
    }

    return (
        <section className={"why-section bg-whitish dark:bg-nav-bg py-12 lg:py-20 px-4 lg:px-0"}>
            <h2 className={"text-3xl text-dark dark:text-light font-bold text-center"}>
                Use&nbsp;
                <span className={"text-brand whitespace-nowrap relative z-10"}>
                    Natai Diary
                    <span className="brand-highlight w-[184px]"></span>
                </span>
                &nbsp;anytime & anywhere!
            </h2>

            <div className="container mx-auto flex justify-between mt-12 lg:mt-28 ">
                <div className="hidden lg:flex lg:justify-between mx-auto">
                    <div className="grid grid-cols-3 gap-x-8 gap-y-20">
                        {items.map((item, index) => {
                            return (
                                <WhyCardItem
                                    key={`why-desktop-${index}`}
                                    img={item.img}
                                    title={item.title}
                                    description={item.description}
                                />
                            )
                        })}
                    </div>
                </div>
                <div className="lg:hidden relative flex flex-col w-full swiper-min-height">
                    <div className="flex">
                        <Swiper
                            slidesPerView={1.2}
                            spaceBetween={30}
                            pagination={{
                                clickable: true,
                            }}
                            modules={[Pagination]}
                            className="mySwiper"
                        >
                            {items.map((item, index) => {
                                return (
                                    <SwiperSlide key={`why-mobile-${index}`}>
                                        <WhyCardItem
                                            img={item.img}
                                            title={item.title}
                                            description={item.description}
                                        />
                                    </SwiperSlide>
                                )
                            })}
                        </Swiper>
                    </div>

                    <Link href={"/diary"}
                          className={"mt-4 w-full rounded-3xl text-center font-semibold py-3 mx-auto bg-brand text-white"}>
                        Try web version
                    </Link>
                </div>

            </div>
        </section>
    )
}