import {ArticleResponse, CloudBlogArticleDto} from "../../../src/api/apiSchemas";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import {NotFound} from "../../../src/modules/common/components/NotFound";
import {fetchGetArticlesById} from "../../../src/api/apiComponents";
import NarrowWrapper from "../../../src/modules/common/components/NarrowWrapper";
import {defaultMetadata} from "../../../src/utils/seo";
import {Seo} from "../../../src/modules/common/components/GlobalSeo";
import Image from "next/image";
import {BookOpenIcon} from "@heroicons/react/24/outline";

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

                    <Image
                        src={props.article.cover}
                        alt={`Cover image for article ${translation.title}`}
                        width={1280}
                        height={720}
                        className={"rounded-lg"}
                    />

                    <div dangerouslySetInnerHTML={innerHtml}></div>
                </div>

                <ArticleBottomBanner2/>
            </NarrowWrapper>
        </MainLayout>
    )
}

function ArticleBottomBanner2() {
    return (
        <div className="bg-copyright p-4 rounded-lg flex items-center">
            <BookOpenIcon className="w-8 h-8 text-gray-400 mr-4"/>
            <div>
                <p className="text-gray-400 mb-2">Considering to start journaling?</p>
                <a href="https://play.google.com/store/apps/details?id=com.svbackend.natai"
                   className="text-brand hover:text-blue-400 font-bold">
                    Try Natai Diary
                </a>
            </div>
        </div>
    );
}