import MainLayout from "../../src/modules/common/components/mainLayout";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {fetchGetStatic} from "../../src/api/apiComponents";

export async function getServerSideProps() {
    let privacy: string

    try {
        const response = await fetchGetStatic({})
        privacy = response.privacy
    } catch (e) {
        privacy = "We are updating our privacy policy. Please check back later."
    }

    return {
        props: {
            content: privacy,
        }
    }
}

export default function TermsPage({content}: { content: string }) {
    const markup = {__html: content};

    return (
        <MainLayout>
            <NarrowWrapper>
                <article className={"prose dark:prose-invert"} dangerouslySetInnerHTML={markup}/>
            </NarrowWrapper>
        </MainLayout>
    )
}