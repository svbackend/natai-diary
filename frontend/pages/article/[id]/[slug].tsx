import {ArticleResponse, CloudBlogArticleDto} from "../../../src/api/apiSchemas";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import {NotFound} from "../../../src/modules/common/components/NotFound";
import {fetchGetArticlesById} from "../../../src/api/apiComponents";
import NarrowWrapper from "../../../src/modules/common/components/NarrowWrapper";
import {defaultMetadata} from "../../../src/utils/seo";
import {Seo} from "../../../src/modules/common/components/GlobalSeo";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}

export async function getServerSideProps(context: any) {
    const {slug, id} = context.query;

    let articleResponse: ArticleResponse
    try {
        articleResponse = await fetchGetArticlesById({
            pathParams: {id: id},
        })
    } catch (e) {
        return {
            notFound: true,
        }
    }

    const t = articleResponse.article.translations.find(a => a.locale === "en")

    if (!t) {
        return {
            notFound: true,
        }
    }

    if (t.slug !== slug) {
        return {
            redirect: {
                destination: `/article/${id}/${t.slug}`,
                permanent: true,
            },
        }
    }

    return {
        props: {
            article: articleResponse.article,
        }
    }
}

export default function ArticleViewPage(props: { article: CloudBlogArticleDto }) {

    const translation = props.article
        .translations
        .find(a => a.locale === "en")

    if (!translation) {
        return <NotFound/>
    }

    const innerHtml = {__html: translation.content}

    return (
        <MainLayout>
            <Seo
                title={translation.title}
                description={translation.metaDescription}
                keywords={translation.metaKeywords}
                img={props.article.cover}
            />
            <NarrowWrapper>
                <div className="prose dark:prose-invert">
                    <h1>
                        {translation.title}
                    </h1>

                    <div dangerouslySetInnerHTML={innerHtml}></div>
                </div>
            </NarrowWrapper>
        </MainLayout>
    )
}