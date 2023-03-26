import MainLayout from "../../src/modules/common/components/mainLayout";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {fetchGetStatic} from "../../src/api/apiComponents";
import {defaultMetadata} from "../../src/utils/seo";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}

export async function getServerSideProps() {
    let privacy: string

    try {
        console.log("Fetching privacy policy")
        const response = await fetchGetStatic({})
        privacy = response.privacy
    } catch (e) {
        privacy = "We are updating our privacy policy. Please check back later."
        console.error(e)
    }

    return {
        props: {
            content: privacy,
        }
    }
}

export default function PrivacyPage({content}: { content: string }) {
    const markup = {__html: content};

    return (
        <MainLayout>
            <NarrowWrapper>
                <article className={"prose dark:prose-invert"} dangerouslySetInnerHTML={markup}/>
            </NarrowWrapper>
        </MainLayout>
    )
}