import MainLayout from "../../src/modules/common/components/mainLayout";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {fetchGetStatic} from "../../src/api/apiComponents";

export async function getStaticProps() {
    let privacy: string

    try {
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