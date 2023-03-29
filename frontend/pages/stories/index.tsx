import React from "react";
import MainLayout from "../../src/modules/common/components/mainLayout";
import {CloudBlogArticleDto, FindAllArticlesResponse} from "../../src/api/apiSchemas";
import {fetchGetArticles} from "../../src/api/apiComponents";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import Link from "next/link";
import {defaultMetadata} from "../../src/utils/seo";
import Image from "next/image";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}

export async function getServerSideProps() {
    let articlesResponse: FindAllArticlesResponse

    try {
        articlesResponse = await fetchGetArticles({})
    } catch (e) {
        console.error(e)
        return {
            props: {
                articles: [],
            },
        }
    }

    return {
        props: {
            articles: articlesResponse.articles,
        },
    }
}

export default function StoriesIndexPage(props: { articles: CloudBlogArticleDto[] }) {
    return (
        <MainLayout>
            <NarrowWrapper>
                <h1 className={"text-3xl font-bold"}>Blog & Stories</h1>

                <div className={"flex my-8 flex-col gap-5"}>
                    {props.articles.map((article) => (
                        <ArticlePreviewCard article={article} key={article.id}/>
                    ))}
                </div>
            </NarrowWrapper>
        </MainLayout>
    )
}

function ArticlePreviewCard(props: { article: CloudBlogArticleDto }) {
    const trans = props
        .article
        .translations
        .find((t) => t.locale === "en")

    if (!trans) {
        return null
    }

    const shortDescription = trans.content.substring(0, 100) + "..."

    const preview = shortDescription.replace(/(<([^>]+)>)/gi, "");
    const url = `/article/${props.article.shortId}/${trans.slug}`

    return (
        <div className={"bg-light3 text-dark dark:bg-menu dark:text-light rounded-md"}>
            <Image
                className={"rounded-t-md w-full"}
                src={props.article.cover}
                alt={`Article cover for ${trans.title}`}
                width={1280}
                height={720}
            />

            <div className="p-4">
                <h2 className={"text-2xl font-bold mt-2 mb-4"}>
                    <Link href={url}>
                        {trans.title}
                    </Link>
                </h2>
                <p className={"text-darkish dark:text-light2"}>
                    {preview}
                    <Link href={url} className={"text-brand-alt ml-2"}>
                        Read more
                    </Link>
                </p>

            </div>
        </div>
    )
}