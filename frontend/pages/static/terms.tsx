import MainLayout from "../../src/modules/common/components/mainLayout";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {fetchGetStatic} from "../../src/api/apiComponents";

export async function getServerSideProps() {
    let terms: string

    try {
        const response = await fetchGetStatic({})
        terms = response.terms
    } catch (e) {
        terms = "We are updating our terms and conditions. Please check back later."
    }

    return {
        props: {
            content: terms,
        }
    }
}

export default function TermsPage({content}: { content: string }) {
    const markup = {__html: content};

    return (
        <MainLayout>
            <NarrowWrapper>
                <article className={"prose"} dangerouslySetInnerHTML={markup}/>
            </NarrowWrapper>
        </MainLayout>
    )
}